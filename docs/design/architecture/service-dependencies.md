# TripHub 서비스 의존성 관리

## 1. 서비스 의존성 다이어그램

```
API Gateway
├── User Service
│   └── Config Server
├── Accommodation Service
│   ├── Config Server
│   └── Review Service (읽기 전용)
├── Reservation Service
│   ├── Config Server
│   ├── User Service (읽기 전용)
│   ├── Accommodation Service (읽기 전용)
│   └── Payment Service
└── Review Service
    ├── Config Server
    ├── User Service (읽기 전용)
    └── Accommodation Service (읽기 전용)
```

## 2. 서비스 간 의존성 유형

### 2.1 직접 의존성
- **강한 의존성**
  - Reservation Service → Payment Service
  - User Service → Config Server
  - 모든 서비스 → Service Registry

- **약한 의존성**
  - Review Service → User Service (읽기 전용)
  - Accommodation Service → Review Service (읽기 전용)

### 2.2 이벤트 기반 의존성
- **예약 관련 이벤트**
  ```
  ReservationCreatedEvent
  ├── Payment Service (결제 처리)
  └── Notification Service (알림 발송)
  ```

- **결제 관련 이벤트**
  ```
  PaymentCompletedEvent
  ├── Reservation Service (예약 확정)
  └── Notification Service (결제 완료 알림)
  ```

## 3. 의존성 관리 전략

### 3.1 서킷 브레이커 패턴
- **적용 서비스**
  - Reservation Service → Payment Service
  - Accommodation Service → Review Service
  - 모든 서비스 → Config Server

- **장애 대응**
  - 기본값 사용
  - 캐시된 데이터 활용
  - 대체 서비스 사용

### 3.2 데이터 일관성 관리
- **이벤트 소싱**
  - 모든 도메인 이벤트 저장
  - 이벤트 재생을 통한 상태 복구
  - 이벤트 버전 관리

- **CQRS 패턴**
  - 명령과 조회 분리
  - 읽기 전용 복제본 활용
  - 실시간 데이터 동기화

## 4. 장애 대응 전략

### 4.1 서비스 장애 시나리오
- **Payment Service 장애**
  - 예약 서비스: 결제 보류 상태로 전환
  - 재시도 메커니즘 활성화
  - 수동 개입 프로세스

- **User Service 장애**
  - 읽기 전용 모드로 전환
  - 캐시된 사용자 정보 활용
  - 제한된 기능 제공

### 4.2 데이터베이스 장애
- **주 데이터베이스 장애**
  - 읽기 전용 복제본으로 전환
  - 쓰기 작업 큐잉
  - 데이터 복구 프로세스

- **복제본 장애**
  - 다른 복제본으로 전환
  - 복제본 재구축
  - 모니터링 및 알림 