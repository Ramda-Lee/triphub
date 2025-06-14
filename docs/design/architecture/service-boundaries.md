# TripHub 서비스 경계 및 의존성

## 1. 서비스 경계

### 1.1 핵심 서비스 경계

#### Auth Service
- **책임**
  - 사용자 인증/인가
  - JWT 토큰 관리
  - 사용자 프로필 관리
- **경계**
  - 독립적인 사용자 데이터베이스
  - 다른 서비스와의 느슨한 결합

#### Accommodation Service
- **책임**
  - 숙소 정보 관리
  - 객실 관리
  - 가격 정책
- **경계**
  - 독립적인 숙소 데이터베이스
  - Reservation Service와의 통신

#### Reservation Service
- **책임**
  - 예약 생성/관리
  - 가용성 관리
  - 예약 상태 추적
- **경계**
  - 독립적인 예약 데이터베이스
  - Accommodation, Payment Service와의 통신

#### Payment Service
- **책임**
  - 결제 처리
  - 환불 처리
  - 결제 내역 관리
- **경계**
  - 독립적인 결제 데이터베이스
  - Reservation Service와의 통신

#### Review Service
- **책임**
  - 리뷰 관리
  - 평점 관리
  - 리뷰 통계
- **경계**
  - 독립적인 리뷰 데이터베이스
  - Accommodation Service와의 통신

### 1.2 공통 서비스 경계

#### API Gateway
- **책임**
  - 요청 라우팅
  - 인증/인가
  - 요청/응답 변환
- **경계**
  - 모든 외부 요청의 진입점
  - 서비스 간 통신에는 관여하지 않음

#### Service Registry
- **책임**
  - 서비스 등록/발견
  - 헬스 체크
  - 서비스 상태 모니터링
- **경계**
  - 모든 서비스의 등록 정보 관리
  - 서비스 메타데이터 저장

#### Config Server
- **책임**
  - 설정 관리
  - 환경별 설정
  - 동적 설정 변경
- **경계**
  - 모든 서비스의 설정 정보 관리
  - 민감한 설정 정보 암호화

## 2. 서비스 의존성

### 2.1 직접 의존성

```
Auth Service
  ↓
User Service
  ↓
Accommodation Service ← Review Service
  ↓
Reservation Service
  ↓
Payment Service
```

### 2.2 순환 의존성 방지

- 이벤트 기반 통신 사용
- 비동기 메시징 활용
- 서비스 간 직접 호출 최소화

## 3. 서비스 통신

### 3.1 동기 통신 (gRPC)
- 서비스 간 직접 호출이 필요한 경우
- 실시간 데이터 동기화가 필요한 경우
- 트랜잭션이 필요한 경우

### 3.2 비동기 통신 (Kafka)
- 이벤트 기반 통신이 필요한 경우
- 데이터 동기화가 필요한 경우
- 순환 의존성을 피해야 하는 경우

## 4. 데이터 일관성

### 4.1 강한 일관성이 필요한 경우
- 결제 처리
- 예약 생성
- 사용자 인증

### 4.2 최종 일관성이 허용되는 경우
- 리뷰 작성
- 평점 업데이트
- 통계 데이터