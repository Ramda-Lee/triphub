# TripHub 서비스 경계 정의

## 1. 서비스 개요

TripHub은 다음과 같은 핵심 서비스들로 구성됩니다:

### 1.1 인증 서비스 (Auth Service)
- **책임**: 사용자 인증 및 권한 관리
- **주요 기능**:
  - 회원가입/로그인
  - 토큰 발급/검증
  - 권한 관리
- **데이터 소유권**: 사용자 인증 정보, 권한 정보
- **의존성**: 없음

### 1.2 숙박 서비스 (Accommodation Service)
- **책임**: 숙소 및 객실 정보 관리
- **주요 기능**:
  - 숙소 정보 관리
  - 객실 정보 관리
  - 가용성 확인
  - 검색 기능
- **데이터 소유권**: 숙소 정보, 객실 정보, 가용성 정보
- **의존성**: 없음

### 1.3 예약 서비스 (Reservation Service)
- **책임**: 예약 프로세스 관리
- **주요 기능**:
  - 예약 생성/조회/수정/취소
  - 예약 상태 관리
- **데이터 소유권**: 예약 정보, 예약 상태
- **의존성**:
  - 숙박 서비스 (가용성 확인)
  - 결제 서비스 (결제 처리)

### 1.4 결제 서비스 (Payment Service)
- **책임**: 결제 프로세스 관리
- **주요 기능**:
  - 결제 처리
  - 환불 처리
  - 결제 내역 관리
- **데이터 소유권**: 결제 정보, 결제 내역
- **의존성**:
  - 예약 서비스 (예약 정보 확인)

### 1.5 리뷰 서비스 (Review Service)
- **책임**: 리뷰 및 평점 관리
- **주요 기능**:
  - 리뷰 작성/조회/수정/삭제
  - 평점 관리
- **데이터 소유권**: 리뷰 정보, 평점 정보
- **의존성**:
  - 숙박 서비스 (숙소 정보 조회)
  - 예약 서비스 (예약 확인)

## 2. 서비스 간 통신

### 2.1 동기 통신 (gRPC)
- 예약 생성 시 숙박 서비스의 가용성 확인
- 결제 처리 시 예약 정보 확인
- 리뷰 작성 시 예약 정보 확인

### 2.2 비동기 통신 (Kafka)
- 예약 생성 시 결제 서비스에 결제 요청 이벤트 발행
- 결제 완료 시 예약 서비스에 상태 업데이트 이벤트 발행
- 리뷰 작성 시 숙박 서비스에 평점 업데이트 이벤트 발행

## 3. 데이터 일관성 전략

### 3.1 데이터 소유권
- 각 서비스는 자신의 도메인 데이터를 독립적으로 관리
- 다른 서비스의 데이터는 이벤트를 통해 동기화

### 3.2 이벤트 기반 동기화
- 예약 생성 → 결제 요청 이벤트
- 결제 완료 → 예약 상태 업데이트 이벤트
- 리뷰 작성 → 평점 업데이트 이벤트

### 3.3 트랜잭션 경계
- 각 서비스는 자신의 도메인 내에서만 트랜잭션 보장
- 서비스 간 트랜잭션은 이벤트 기반으로 처리

## 4. API 게이트웨이

### 4.1 라우팅 규칙
- /api/auth/* → 인증 서비스
- /api/accommodations/* → 숙박 서비스
- /api/reservations/* → 예약 서비스
- /api/payments/* → 결제 서비스
- /api/reviews/* → 리뷰 서비스

### 4.2 공통 기능
- 인증/인가 처리
- 요청/응답 변환
- 로깅 및 모니터링
- 레이트 리미팅 