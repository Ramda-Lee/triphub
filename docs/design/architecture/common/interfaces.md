# 공통 인터페이스 설계

## 1. 개요

TripHub 프로젝트의 공통 인터페이스는 모든 서비스에서 일관된 방식으로 사용되는 인터페이스들을 정의합니다.

## 2. API 응답 형식

### 2.1 기본 응답 구조
```java
public class ApiResponse<T> {
    private StatusCode status;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    
    // 정적 팩토리 메서드
    public static <T> ApiResponse<T> success(T data) {
        // 성공 응답 생성
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        // 에러 응답 생성
    }
}
```

### 2.2 사용 예시
```java
@GetMapping("/users/{id}")
public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ApiResponse.success(UserDTO.from(user));
}

@PostMapping("/users")
public ApiResponse<UserDTO> createUser(@RequestBody UserCreateRequest request) {
    try {
        User user = userService.create(request);
        return ApiResponse.success(UserDTO.from(user));
    } catch (BusinessException e) {
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }
}
```

## 3. 예외 처리

### 3.1 예외 계층 구조
```java
// 비즈니스 예외
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}

// 검증 예외
public class ValidationException extends BusinessException {
    private final Map<String, String> errors;
}

// 리소스 없음 예외
public class ResourceNotFoundException extends BusinessException {
    private final String resourceName;
    private final String identifier;
}
```

### 3.2 전역 예외 처리
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        // 비즈니스 예외 처리
    }
    
    @ExceptionHandler(ValidationException.class)
    public ApiResponse<Map<String, String>> handleValidationException(ValidationException ex) {
        // 검증 예외 처리
    }
    
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        // 기타 예외 처리
    }
}
```

## 4. 로깅

### 4.1 로그 컨텍스트
```java
public class LogContext {
    private static final ThreadLocal<Map<String, String>> context = new ThreadLocal<>();
    
    public static void set(String key, String value) {
        // 컨텍스트 값 설정
    }
    
    public static String get(String key) {
        // 컨텍스트 값 조회
    }
    
    public static void clear() {
        // 컨텍스트 초기화
    }
}
```

### 4.2 AOP 기반 로깅
```java
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(Loggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 실행 전 로깅
        Object result = joinPoint.proceed();
        // 메서드 실행 후 로깅
        return result;
    }
}
```

### 4.3 로그 레벨
```java
public enum LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}
```

## 5. 인증/인가

### 5.1 보안 컨텍스트
```java
public class SecurityContext {
    private static final ThreadLocal<Authentication> authentication = new ThreadLocal<>();
    
    public static Authentication getAuthentication() {
        return authentication.get();
    }
    
    public static void setAuthentication(Authentication auth) {
        authentication.set(auth);
    }
    
    public static void clear() {
        authentication.remove();
    }
}
```

### 5.2 인증 정보
```java
public class Authentication {
    private final Long userId;
    private final String username;
    private final Set<Role> roles;
    
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}
```

### 5.3 권한 정보
```java
public enum Role {
    USER,
    ADMIN,
    PARTNER
}

public enum Permission {
    READ,
    WRITE,
    DELETE
}
```

## 6. 사용 가이드라인

### 6.1 API 응답 사용
- 모든 API 응답은 ApiResponse 클래스 사용
- 성공/실패 케이스 명확히 구분
- 적절한 에러 코드와 메시지 사용

### 6.2 예외 처리
- 비즈니스 예외는 BusinessException 사용
- 검증 예외는 ValidationException 사용
- 예외 메시지는 사용자 친화적으로 작성

### 6.3 로깅
- 적절한 로그 레벨 사용
- 민감 정보 마스킹
- 구조화된 로그 포맷 사용

### 6.4 인증/인가
- SecurityContext를 통한 인증 정보 접근
- Role 기반 접근 제어
- Permission 기반 세부 권한 제어 

## 7. gRPC 인터페이스

### 7.1 기본 서비스 인터페이스
```java
public interface GrpcService {
    // 서비스 상태 확인
    HealthCheckResponse check(HealthCheckRequest request);
    
    // 서비스 메타데이터 조회
    ServiceMetadata getMetadata();
}

// 서비스 메타데이터
public class ServiceMetadata {
    private final String serviceName;
    private final String version;
    private final Map<String, String> capabilities;
}
```

### 7.2 공통 gRPC 메시지
```protobuf
// 기본 응답 메시지
message BaseResponse {
    StatusCode status = 1;
    string message = 2;
    google.protobuf.Timestamp timestamp = 3;
}

// 페이지네이션 메시지
message PaginationRequest {
    int32 page = 1;
    int32 size = 2;
    string sort_by = 3;
    bool ascending = 4;
}

message PaginationResponse {
    int32 total_pages = 1;
    int32 total_elements = 2;
    int32 current_page = 3;
    int32 page_size = 4;
}
```

### 7.3 gRPC 서비스 구현 가이드라인
1. 모든 gRPC 서비스는 기본 서비스 인터페이스 구현
2. 서비스 메서드는 idempotent하게 구현
3. 적절한 에러 처리와 로깅 포함
4. 서비스 간 통신은 mTLS 사용

### 7.4 gRPC 클라이언트 구현
```java
public abstract class GrpcClient {
    protected final ManagedChannel channel;
    protected final Stub stub;
    
    protected GrpcClient(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target)
            .useTransportSecurity()
            .build();
        this.stub = createStub(channel);
    }
    
    protected abstract Stub createStub(ManagedChannel channel);
    
    public void shutdown() {
        channel.shutdown();
    }
}
```

### 7.5 gRPC 인터셉터
```java
public class GrpcLoggingInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {
        // 요청 로깅
        return next.startCall(call, headers);
    }
}

public class GrpcAuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {
        // 인증 처리
        return next.startCall(call, headers);
    }
}
```

### 7.6 사용 가이드라인
1. 서비스 정의는 명확하고 일관된 네이밍 사용
2. 메시지 타입은 재사용 가능하도록 설계
3. 적절한 타임아웃과 재시도 정책 설정
4. 서비스 간 통신은 비동기 처리 고려
5. 모니터링과 트레이싱 구현 