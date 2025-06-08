# TripHub 마이크로서비스 아키텍처

## 1. 아키텍처 개요

TripHub는 마이크로서비스 아키텍처를 기반으로 구축된 여행 예약 플랫폼입니다. 각 서비스는 독립적으로 배포되고 운영되며, 서비스 간 통신은 gRPC를 통해 이루어집니다. 프론트엔드와 백엔드 사이의 통신은 REST API를 사용합니다.

## 2. 서비스 구성

### 2.1 핵심 서비스

1. **Auth Service**
   - 사용자 인증 및 권한 관리
   - JWT 토큰 발급 및 검증
   - 소셜 로그인 통합
   - REST API: 프론트엔드 통신
   - gRPC: 다른 서비스와의 통신

2. **Accommodation Service**
   - 숙소 정보 관리
   - 객실 가용성 관리
   - 숙소 검색 및 필터링
   - REST API: 프론트엔드 통신
   - gRPC: Reservation Service와 통신

3. **Reservation Service**
   - 예약 생성 및 관리
   - 예약 상태 추적
   - 예약 취소 처리
   - REST API: 프론트엔드 통신
   - gRPC: Accommodation, Payment 서비스와 통신

4. **Payment Service**
   - 결제 처리
   - 결제 상태 관리
   - 환불 처리
   - REST API: 프론트엔드 통신
   - gRPC: Reservation Service와 통신

5. **Review Service**
   - 리뷰 작성 및 관리
   - 평점 계산
   - 리뷰 통계 제공
   - REST API: 프론트엔드 통신
   - gRPC: Accommodation Service와 통신

### 2.2 공통 서비스

1. **API Gateway**
   - 요청 라우팅
   - 인증/인가 처리
   - 부하 분산
   - REST API 엔드포인트 제공

2. **Service Registry**
   - 서비스 등록 및 발견
   - 서비스 상태 모니터링
   - 동적 라우팅 지원

3. **Config Server**
   - 중앙화된 설정 관리
   - 환경별 설정 분리
   - 실시간 설정 업데이트

## 3. 통신 아키텍처

### 3.1 서비스 간 통신 (gRPC)
- Protocol Buffers를 사용한 효율적인 직렬화
- 양방향 스트리밍 지원
- 강력한 타입 시스템
- 자동 코드 생성
- 높은 성능

#### 주요 서비스 간 통신 예시
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

### 3.2 프론트엔드 통신 (REST API)
- HTTP/HTTPS 프로토콜
- JSON 형식의 데이터 교환
- 캐싱 지원
- 브라우저 호환성
- 쉬운 디버깅

#### API 엔드포인트
```
/api/v1/users
/api/v1/accommodations
/api/v1/reservations
/api/v1/payments
/api/v1/reviews
```

## 4. 데이터 관리

### 4.1 데이터베이스 전략
- Database per Service 패턴 적용
- 각 서비스는 독립적인 데이터베이스 보유
- 데이터 일관성을 위한 이벤트 소싱 패턴 사용
- CQRS 패턴으로 읽기/쓰기 성능 최적화

### 4.2 캐싱 전략
- Redis를 사용한 분산 캐시
  - 세션 데이터
  - 인증 토큰
  - 자주 접근하는 데이터
- 서비스별 로컬 캐시
  - 빠른 데이터 접근
  - 부하 감소

## 5. 보안

### 5.1 인증/인가
- JWT 기반 인증
- OAuth 2.0 통합
- 역할 기반 접근 제어

### 5.2 API 보안
- HTTPS 통신
- API 키 관리
- 요청 제한 (Rate Limiting)
- mTLS를 통한 서비스 간 통신 암호화

### 5.3 데이터 보안
- 데이터 암호화
- 민감 정보 마스킹
- 접근 로깅

## 6. 모니터링 및 로깅

### 6.1 중앙화된 로깅
- ELK 스택 (Elasticsearch, Logstash, Kibana)
- 분산 추적 (Jaeger)
- 로그 집계

### 6.2 메트릭 수집
- Prometheus
- Grafana 대시보드
- 성능 모니터링

### 6.3 알림 시스템
- 이상 감지
- 자동 알림
- 문제 해결 지원

## 7. 배포 전략

### 7.1 컨테이너화
- Docker 컨테이너
- Kubernetes 오케스트레이션
- 서비스 메시 (Istio)
- 자동 스케일링

### 7.2 CI/CD
- GitOps 기반 배포
- 지속적 통합
- 자동화된 테스트
- 지속적 배포

### 7.3 배포 방식
- 블루-그린 배포
- 카나리 배포
- 무중단 배포
- 롤백 지원 