package com.triphub.auth.service;

import com.triphub.auth.dto.LoginReq;
import com.triphub.auth.dto.LoginRes;
import com.triphub.auth.dto.LogoutReq;
import com.triphub.auth.dto.SignupReq;
import com.triphub.auth.entity.User;
import com.triphub.auth.repository.UserRepository;
import com.triphub.auth.security.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Nested
    @DisplayName("회원가입")
    class Signup {
        @Test
        @DisplayName("이메일 중복 체크")
        void whenEmailExists_thenThrowException() {
            // given
            SignupReq req = new SignupReq();
            req.setEmail("test@example.com");
            req.setPassword("password123");
            req.setName("Test User");

            when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.signup(req);
            });
        }

        @Test
        @DisplayName("비밀번호 암호화")
        void whenSignup_thenPasswordShouldBeEncrypted() {
            // given
            SignupReq req = new SignupReq();
            req.setEmail("test@example.com");
            req.setPassword("password123");
            req.setName("Test User");

            when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);
            when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

            // when
            authService.signup(req);

            // then
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("기본 역할 설정")
        void whenSignup_thenDefaultRoleShouldBeUser() {
            // given
            SignupReq req = new SignupReq();
            req.setEmail("test@example.com");
            req.setPassword("password123");
            req.setName("Test User");

            when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);
            when(passwordEncoder.encode(any()))
                .thenReturn("encodedPassword");

            // when
            authService.signup(req);

            // then
            verify(userRepository).save(argThat(user -> 
                user.getRole().equals("USER")
            ));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("존재하지 않는 이메일")
        void whenEmailNotExists_thenThrowException() {
            // given
            LoginReq req = new LoginReq();
            req.setEmail("nonexistent@example.com");
            req.setPassword("password123");

            when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(req);
            });
        }

        @Test
        @DisplayName("잘못된 비밀번호")
        void whenWrongPassword_thenThrowException() {
            // given
            User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role("USER")
                .build();

            LoginReq req = new LoginReq();
            req.setEmail("test@example.com");
            req.setPassword("wrongpassword");

            when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
                .thenReturn(false);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.login(req);
            });
        }

        @Test
        @DisplayName("토큰 발급")
        void whenLoginSuccess_thenShouldIssueTokens() {
            // given
            User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role("USER")
                .build();

            LoginReq req = new LoginReq();
            req.setEmail("test@example.com");
            req.setPassword("password123");

            when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
            when(jwtProvider.generateAccessToken("test@example.com", "USER"))
                .thenReturn("accessToken");
            when(jwtProvider.generateRefreshToken("test@example.com"))
                .thenReturn("refreshToken");

            // when
            LoginRes response = authService.login(req);

            // then
            assertNotNull(response);
            assertEquals("accessToken", response.getAccessToken());
            assertEquals("refreshToken", response.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class TokenReissue {
        @Test
        @DisplayName("유효하지 않은 리프레시 토큰")
        void whenInvalidRefreshToken_thenThrowException() {
            // given
            String invalidToken = "invalid.token";
            when(jwtProvider.validateToken(invalidToken))
                .thenReturn(false);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.reissue(invalidToken);
            });
        }

        @Test
        @DisplayName("Redis 토큰 불일치")
        void whenRefreshTokenMismatch_thenThrowException() {
            // given
            String refreshToken = "valid.token";
            String email = "test@example.com";
            User user = User.builder()
                .email(email)
                .role("USER")
                .build();

            when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);
            when(jwtProvider.parseToken(refreshToken))
                .thenReturn(mock(Claims.class));
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
            when(redisTemplate.opsForValue().get("refresh_token:" + email))
                .thenReturn("different.token");

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                authService.reissue(refreshToken);
            });
        }

        @Test
        @DisplayName("새로운 액세스 토큰 발급")
        void whenValidRefreshToken_thenShouldIssueNewAccessToken() {
            // given
            String refreshToken = "valid.token";
            String email = "test@example.com";
            User user = User.builder()
                .email(email)
                .role("USER")
                .build();

            when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);
            when(jwtProvider.parseToken(refreshToken))
                .thenReturn(mock(Claims.class));
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
            when(redisTemplate.opsForValue().get("refresh_token:" + email))
                .thenReturn(refreshToken);
            when(jwtProvider.generateAccessToken(email, "USER"))
                .thenReturn("new.access.token");

            // when
            String newAccessToken = authService.reissue(refreshToken);

            // then
            assertEquals("new.access.token", newAccessToken);
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {
        @Test
        @DisplayName("리프레시 토큰 제거")
        void whenLogout_thenRefreshTokenShouldBeRemoved() {
            // given
            String accessToken = "valid.access.token";
            String refreshToken = "valid.refresh.token";
            String email = "test@example.com";

            when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);
            when(jwtProvider.parseToken(refreshToken))
                .thenReturn(mock(Claims.class));

            // when
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.setAccessToken(accessToken);
            logoutReq.setRefreshToken(refreshToken);
            authService.logout(logoutReq);

            // then
            verify(redisTemplate).delete("refresh_token:" + email);
        }

        @Test
        @DisplayName("액세스 토큰 블랙리스트 추가")
        void whenLogout_thenAccessTokenShouldBeBlacklisted() {
            // given
            String accessToken = "valid.access.token";
            String refreshToken = "valid.refresh.token";
            String email = "test@example.com";

            when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);
            when(jwtProvider.parseToken(refreshToken))
                .thenReturn(mock(Claims.class));
            when(jwtProvider.validateToken(accessToken))
                .thenReturn(true);
            when(jwtProvider.getExpirationFromToken(accessToken))
                .thenReturn(System.currentTimeMillis() + 3600000); // 1시간 후 만료

            // when
            LogoutReq logoutReq = new LogoutReq();
            logoutReq.setAccessToken(accessToken);
            logoutReq.setRefreshToken(refreshToken);
            authService.logout(logoutReq);

            // then
            verify(redisTemplate).opsForValue().set(
                argThat(key -> key.startsWith("blacklist:")),
                eq("revoked"),
                any(Duration.class)
            );
        }
    }
} 