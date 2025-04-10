package com.triphub.auth.service;

import com.triphub.auth.dto.LoginReq;
import com.triphub.auth.dto.LoginRes;
import com.triphub.auth.dto.SignupReq;
import com.triphub.auth.entity.User;
import com.triphub.auth.repository.UserRepository;
import com.triphub.auth.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void signup(SignupReq req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Redis에 저장
        redisTemplate.opsForValue().set("refresh_token:" + user.getEmail(), refreshToken, Duration.ofDays(7));

        return new LoginRes(accessToken, refreshToken);
    }

    @Override
    public String reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtProvider.parseToken(refreshToken).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String savedToken = redisTemplate.opsForValue().get("refresh_token:" + email);
        if (!refreshToken.equals(savedToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        return jwtProvider.generateAccessToken(email, user.getRole());
    }
}
