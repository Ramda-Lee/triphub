# TripHub 서비스 아키텍처

## 1. 서비스 구성

### 1.1 핵심 서비스
- **User Service**
  - 사용자 관리
  - 인증/인가
  - 프로필 관리

- **Accommodation Service**
  - 숙소 정보 관리
  - 객실 관리
  - 가격 정책 관리

- **Reservation Service**
  - 예약 관리
  - 가용성 관리
  - 예약 상태 관리

- **Payment Service**
  - 결제 처리
  - 환불 처리
  - 결제 내역 관리

- **Review Service**
  - 리뷰 관리
  - 평점 관리
  - 리뷰 통계

### 1.2 지원 서비스
- **API Gateway**
  - 라우팅
  - 인증/인가
  - 요청/응답 변환
  - 로드 밸런싱

- **Service Registry**
  - 서비스 등록/발견
  - 헬스 체크
  - 서비스 상태 모니터링

- **Config Server**
  - 중앙화된 설정 관리
  - 환경별 설정 관리
  - 동적 설정 변경

## 2. 통신 아키텍처

### 2.1 서비스 간 통신 (gRPC)
- **Protocol Buffers 정의**
  ```protobuf
  // 예약 서비스와 결제 서비스 간 통신
  message PaymentRequest {
    string reservation_id = 1;
    double amount = 2;
    string currency = 3;
  }

  message PaymentResponse {
    string payment_id = 1;
    PaymentStatus status = 2;
  }
  ```

- **서비스 간 통신 패턴**
  - 동기 통신: gRPC
  - 비동기 통신: 이벤트 기반 (Kafka)

### 2.2 프론트엔드 통신 (REST API)
- **API 엔드포인트**
  ```
  /api/v1/users
  /api/v1/accommodations
  /api/v1/reservations
  /api/v1/payments
  /api/v1/reviews
  ```

- **API Gateway를 통한 통합**
  - 단일 진입점 제공
  - API 버전 관리
  - 요청/응답 변환

## 3. 데이터 관리

### 3.1 데이터베이스 전략
- 각 서비스별 독립적인 데이터베이스
- 데이터 일관성: 이벤트 소싱 패턴 사용
- 데이터 동기화: CDC(Change Data Capture) 활용

### 3.2 캐싱 전략
- Redis를 사용한 분산 캐시
- 서비스별 로컬 캐시
- 캐시 무효화 전략

## 4. 보안

### 4.1 인증/인가
- JWT 기반 인증
- OAuth2.0 통합
- API Gateway 레벨 인증

### 4.2 서비스 간 보안
- mTLS를 통한 서비스 간 통신 암호화
- 서비스 메시 인증
- API 키 관리

## 5. 모니터링 및 로깅

### 5.1 모니터링
- Prometheus + Grafana
- 서비스 메트릭 수집
- 알림 설정

### 5.2 로깅
- ELK Stack
- 분산 추적 (Jaeger)
- 중앙화된 로그 관리

## 6. 배포 전략

### 6.1 컨테이너화
- Docker 컨테이너
- Kubernetes 오케스트레이션
- 서비스 메시 (Istio)

### 6.2 CI/CD
- GitOps 기반 배포
- Blue/Green 배포
- Canary 배포 