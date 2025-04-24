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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Mock
    private ValueOperations<String, String> valueOperations;

    private User testUser;
    private Claims testClaims;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role("USER")
                .build();

        testClaims = mock(Claims.class);
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복 체크")
    void signup_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        SignupReq req = SignupReq.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();
        assertThrows(EmailAlreadyExistsException.class, () -> authService.signup(req));
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signup_WhenValidRequest_ShouldSaveUser() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        SignupReq req = SignupReq.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        authService.signup(req);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일")
    void login_WhenEmailNotExists_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginReq req = LoginReq.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호")
    void login_WhenWrongPassword_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        LoginReq req = LoginReq.builder()
                .email("test@example.com")
                .password("wrongpassword")
                .build();

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_WhenValidCredentials_ShouldReturnTokens() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateAccessToken("test@example.com", "USER")).thenReturn("accessToken");
        when(jwtProvider.generateRefreshToken("test@example.com")).thenReturn("refreshToken");

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().doNothing().when(valueOperations).set(anyString(), anyString(), any(Duration.class));

        LoginReq req = LoginReq.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginRes response = authService.login(req);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(valueOperations).set(eq("refresh_token:test@example.com"), eq("refreshToken"), any(Duration.class));
    }

    @Test
    @DisplayName("토큰 갱신 - 유효하지 않은 토큰")
    void reissue_WhenInvalidToken_ShouldThrowException() {
        when(jwtProvider.validateToken("invalid.token")).thenReturn(false);

        assertThrows(TokenValidationException.class, () -> authService.reissue("invalid.token"));
    }

    @Test
    @DisplayName("토큰 갱신 - Redis 토큰 불일치")
    void reissue_WhenTokenMismatch_ShouldThrowException() {
        when(jwtProvider.validateToken("valid.token")).thenReturn(true);
        when(jwtProvider.parseToken("valid.token")).thenReturn(testClaims);
        when(testClaims.getSubject()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:test@example.com")).thenReturn("different.token");

        assertThrows(TokenValidationException.class, () -> authService.reissue("valid.token"));
    }

    @Test
    @DisplayName("토큰 갱신 - 성공")
    void reissue_WhenValidToken_ShouldReturnNewAccessToken() {
        when(jwtProvider.validateToken("valid.token")).thenReturn(true);
        when(jwtProvider.parseToken("valid.token")).thenReturn(testClaims);
        when(testClaims.getSubject()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:test@example.com")).thenReturn("valid.token");
        when(jwtProvider.generateAccessToken("test@example.com", "USER")).thenReturn("new.access.token");

        String newAccessToken = authService.reissue("valid.token");

        assertEquals("new.access.token", newAccessToken);
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    void logout_ShouldRemoveTokens() {
        when(jwtProvider.validateToken("valid.refresh.token")).thenReturn(true);
        when(jwtProvider.parseToken("valid.refresh.token")).thenReturn(testClaims);
        when(jwtProvider.validateToken("valid.access.token")).thenReturn(true);
        when(testClaims.getSubject()).thenReturn("test@example.com");

        LogoutReq req = LogoutReq.builder()
                .accessToken("valid.access.token")
                .refreshToken("valid.refresh.token")
                .build();

        authService.logout(req);

        verify(redisTemplate).delete("refresh_token:test@example.com");
    }
}
