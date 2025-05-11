# 공통 유틸리티 설계

## 1. 개요

TripHub 프로젝트의 공통 유틸리티는 모든 서비스에서 재사용 가능한 유틸리티 클래스들을 정의합니다.

## 2. 날짜/시간 유틸리티 (DateUtils)

### 2.1 주요 기능
```java
public class DateUtils {
    // 날짜 포맷팅
    public static String format(LocalDateTime dateTime, String pattern) {
        // 날짜 포맷팅 로직
    }
    
    // 날짜 계산
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        // 날짜 더하기 로직
    }
    
    // 타임존 변환
    public static LocalDateTime convertTimeZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        // 타임존 변환 로직
    }
}
```

### 2.2 사용 예시
```java
// 날짜 포맷팅
String formattedDate = DateUtils.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");

// 날짜 계산
LocalDateTime nextWeek = DateUtils.addDays(LocalDateTime.now(), 7);

// 타임존 변환
LocalDateTime seoulTime = DateUtils.convertTimeZone(
    LocalDateTime.now(), 
    ZoneId.of("UTC"), 
    ZoneId.of("Asia/Seoul")
);
```

## 3. 문자열 유틸리티 (StringUtils)

### 3.1 주요 기능
```java
public class StringUtils {
    // 문자열 검증
    public static boolean isValidEmail(String email) {
        // 이메일 검증 로직
    }
    
    // 문자열 변환
    public static String toCamelCase(String str) {
        // 카멜케이스 변환 로직
    }
    
    // 문자열 포맷팅
    public static String format(String template, Object... args) {
        // 문자열 포맷팅 로직
    }
}
```

### 3.2 사용 예시
```java
// 이메일 검증
boolean isValid = StringUtils.isValidEmail("user@example.com");

// 카멜케이스 변환
String camelCase = StringUtils.toCamelCase("user_name"); // userName

// 문자열 포맷팅
String message = StringUtils.format("Hello, %s!", "World");
```

## 4. 파일 유틸리티 (FileUtils)

### 4.1 주요 기능
```java
public class FileUtils {
    // 파일 업로드
    public static String upload(MultipartFile file, String directory) {
        // 파일 업로드 로직
    }
    
    // 파일 다운로드
    public static Resource download(String filePath) {
        // 파일 다운로드 로직
    }
    
    // 파일 검증
    public static boolean isValidFile(MultipartFile file, List<String> allowedExtensions) {
        // 파일 검증 로직
    }
}
```

### 4.2 사용 예시
```java
// 파일 업로드
String filePath = FileUtils.upload(multipartFile, "uploads");

// 파일 다운로드
Resource file = FileUtils.download("uploads/example.jpg");

// 파일 검증
boolean isValid = FileUtils.isValidFile(multipartFile, Arrays.asList("jpg", "png"));
```

## 5. 검증 유틸리티 (ValidationUtils)

### 5.1 주요 기능
```java
public class ValidationUtils {
    // 입력값 검증
    public static void validate(Object object) {
        // 입력값 검증 로직
    }
    
    // 비즈니스 규칙 검증
    public static void validateBusinessRule(BusinessRule rule) {
        // 비즈니스 규칙 검증 로직
    }
    
    // 크로스 필드 검증
    public static void validateCrossFields(Object object) {
        // 크로스 필드 검증 로직
    }
}
```

### 5.2 사용 예시
```java
// 입력값 검증
ValidationUtils.validate(userDTO);

// 비즈니스 규칙 검증
ValidationUtils.validateBusinessRule(new DateRangeRule(startDate, endDate));

// 크로스 필드 검증
ValidationUtils.validateCrossFields(reservationDTO);
```

## 6. 사용 가이드라인

### 6.1 유틸리티 클래스 설계 원칙
- 모든 메서드는 static으로 선언
- 인스턴스화 방지를 위해 private 생성자 사용
- 명확한 메서드 이름과 문서화
- 예외 처리 및 로깅 포함

### 6.2 성능 고려사항
- 불필요한 객체 생성 최소화
- 캐싱 활용
- 대용량 데이터 처리 고려

### 6.3 테스트
- 단위 테스트 작성
- 경계값 테스트
- 예외 케이스 테스트 