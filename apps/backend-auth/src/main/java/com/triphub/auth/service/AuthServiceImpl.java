package com.triphub.auth.service;

import com.triphub.auth.dto.LoginReq;
import com.triphub.auth.dto.LoginRes;
import com.triphub.auth.dto.LogoutReq;
import com.triphub.auth.dto.SignupReq;
import com.triphub.auth.entity.User;
import com.triphub.auth.exception.EmailAlreadyExistsException;
import com.triphub.auth.exception.InvalidCredentialsException;
import com.triphub.auth.exception.TokenValidationException;
import com.triphub.auth.repository.UserRepository;
import com.triphub.auth.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void signup(SignupReq req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException(req.getEmail());
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role("USER")
                .build();

        userRepository.save(user);
    }

    @Override
    public LoginRes login(LoginReq req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("존재하지 않는 이메일입니다"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("잘못된 비밀번호입니다");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getEmail(),
                refreshToken,
                Duration.ofDays(7)
        );

        return new LoginRes(accessToken, refreshToken);
    }

    @Override
    public String reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenValidationException("유효하지 않은 리프레시 토큰입니다");
        }

        Claims claims = jwtProvider.parseToken(refreshToken);
        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("존재하지 않는 사용자입니다"));

        String storedRefreshToken = redisTemplate.opsForValue().get("refresh_token:" + email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new TokenValidationException("저장된 리프레시 토큰과 일치하지 않습니다");
        }

        return jwtProvider.generateAccessToken(email, user.getRole());
    }

    @Override
    public void logout(LogoutReq req) {
        String accessToken = req.getAccessToken();
        String refreshToken = req.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new TokenValidationException("유효하지 않은 리프레시 토큰입니다");
        }

        Claims claims = jwtProvider.parseToken(refreshToken);
        String email = claims.getSubject();

        redisTemplate.delete("refresh_token:" + email);

        if (jwtProvider.validateToken(accessToken)) {
            long expiration = jwtProvider.getExpirationFromToken(accessToken);
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    "revoked",
                    Duration.ofMillis(expiration - System.currentTimeMillis())
            );
        }
    }
}
