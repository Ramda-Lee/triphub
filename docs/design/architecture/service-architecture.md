# TripHub 서비스 아키텍처

## 1. 서비스 구성

### 1.1 핵심 서비스

#### API Gateway
- 모든 외부 요청의 진입점
- 라우팅, 인증, 부하 분산 담당
- 서비스 디스커버리 통합

#### Auth Service
- 사용자 인증 및 권한 관리
- JWT 토큰 발급 및 검증
- OAuth2 통합

#### User Service
- 사용자 프로필 관리
- 개인 정보 관리
- 사용자 설정 관리

#### Accommodation Service
- 숙소 정보 관리
- 검색 기능
- 가격 및 예약 가능 여부 관리

#### Booking Service
- 예약 생성 및 관리
- 결제 처리
- 예약 상태 관리

#### Payment Service
- 결제 처리
- 결제 내역 관리
- 환불 처리

#### Search Service
- 통합 검색 기능
- 필터링 및 정렬
- 검색 결과 캐싱

#### Notification Service
- 이메일 알림
- SMS 알림
- 푸시 알림

### 1.2 공통 인프라 서비스

#### Service Registry
- 서비스 디스커버리
- 서비스 상태 관리

#### Config Server
- 중앙화된 설정 관리
- 환경별 설정 관리

#### Monitoring Service
- 메트릭 수집
- 로그 집계
- 알림 관리

## 2. 서비스 간 의존성

```
API Gateway
  ├── Auth Service
  ├── User Service
  ├── Search Service
  ├── Accommodation Service
  └── Booking Service
      ├── Payment Service
      └── Notification Service
```

## 3. 통신 방식

### 3.1 동기 통신 (gRPC)
- 실시간 요청/응답이 필요한 경우
- 예: 예약 생성, 결제 처리

### 3.2 비동기 통신 (Kafka)
- 이벤트 기반 통신
- 예: 예약 상태 변경, 알림 발송

## 4. 데이터베이스 전략

### 4.1 서비스별 독립 데이터베이스
- 각 서비스는 자체 데이터베이스 보유
- 데이터 일관성 유지를 위한 이벤트 기반 통신

### 4.2 캐시 전략
- Redis를 사용한 분산 캐싱
- 검색 결과, 사용자 세션 등 캐싱

## 5. 확장성 고려사항

### 5.1 수평적 확장
- 서비스별 독립적인 스케일링
- 로드 밸런싱을 통한 트래픽 분산

### 5.2 데이터 일관성
- 이벤트 소싱 패턴 적용
- CQRS 패턴 고려

### 5.3 장애 처리
- 서킷 브레이커 패턴 적용
- 폴백 메커니즘 구현 