# 공통 도메인 모델 설계

## 1. 개요

TripHub 프로젝트의 공통 도메인 모델은 모든 서비스에서 공통적으로 사용되는 도메인 객체들을 정의합니다.

## 2. 기본 엔티티 (BaseEntity)

### 2.1 구조
```java
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
}
```

### 2.2 필드 설명
- `id`: 엔티티의 고유 식별자
- `createdAt`: 엔티티 생성 일시
- `updatedAt`: 엔티티 수정 일시
- `createdBy`: 엔티티 생성자
- `updatedBy`: 엔티티 수정자

### 2.3 사용 예시
```java
@Entity
public class User extends BaseEntity {
    private String name;
    private String email;
}
```

## 3. 공통 값 객체 (Value Objects)

### 3.1 Money (금액)
```java
@Value
public class Money {
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money add(Money other) {
        // 통화 검증 및 덧셈 로직
    }
    
    public Money subtract(Money other) {
        // 통화 검증 및 뺄셈 로직
    }
    
    public String format() {
        // 금액 포맷팅 로직
    }
}
```

### 3.2 Address (주소)
```java
@Value
public class Address {
    private final String zipCode;
    private final String baseAddress;
    private final String detailAddress;
    
    public String getFullAddress() {
        return String.format("%s %s", baseAddress, detailAddress);
    }
    
    public boolean isValid() {
        // 주소 유효성 검증 로직
    }
}
```

### 3.3 Period (기간)
```java
@Value
public class Period {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    
    public boolean isOverlapping(Period other) {
        // 기간 중복 검사 로직
    }
    
    public long getDurationInDays() {
        // 기간 일수 계산 로직
    }
}
```

## 4. 공통 열거형 (Enums)

### 4.1 StatusCode
```java
public enum StatusCode {
    SUCCESS("성공"),
    FAIL("실패"),
    ERROR("오류");
    
    private final String description;
}
```

### 4.2 ErrorCode
```java
public enum ErrorCode {
    VALIDATION_ERROR("입력값 검증 실패"),
    NOT_FOUND("리소스를 찾을 수 없음"),
    UNAUTHORIZED("인증되지 않은 접근"),
    FORBIDDEN("접근 권한 없음");
    
    private final String message;
}
```

## 5. 도메인 규칙

### 5.1 값 객체 불변성
- 모든 값 객체는 불변(immutable)으로 설계
- 생성자에서 유효성 검증 수행
- equals()와 hashCode() 메서드 구현

### 5.2 엔티티 식별
- 모든 엔티티는 고유한 ID 보유
- ID 생성 전략은 데이터베이스에 위임
- 복합 키 사용 지양

### 5.3 도메인 이벤트
- 도메인 이벤트는 값 객체로 정의
- 이벤트 발생 시점 기록
- 이벤트 버전 관리

## 6. 사용 가이드라인

### 6.1 엔티티 사용
- 모든 엔티티는 BaseEntity 상속
- 생성/수정 정보 자동 관리
- JPA 어노테이션 활용

### 6.2 값 객체 사용
- 원시 타입 대신 값 객체 사용
- 도메인 규칙 캡슐화
- 불변성 보장

### 6.3 열거형 사용
- 상태 코드는 StatusCode 사용
- 에러 코드는 ErrorCode 사용
- 확장 시 기존 코드 영향 최소화 