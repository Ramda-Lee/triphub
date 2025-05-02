# TripHub 데이터 흐름 설계

## 1. 주요 데이터 흐름 시나리오

### 1.1 사용자 인증 흐름
```
Client -> API Gateway -> Auth Service
  ├── (성공) -> User Service (프로필 조회)
  └── (실패) -> Client (에러 응답)
```

### 1.2 숙소 검색 흐름
```
Client -> API Gateway -> Search Service
  ├── (캐시 히트) -> Redis -> Client
  └── (캐시 미스) -> Accommodation Service -> Redis -> Client
```

### 1.3 예약 생성 흐름
```
Client -> API Gateway -> Booking Service
  ├── Payment Service (결제 처리)
  ├── Accommodation Service (가용성 확인)
  └── Notification Service (예약 확인)
```

## 2. 이벤트 기반 데이터 흐름

### 2.1 예약 관련 이벤트
```
Booking Service -> Kafka
  ├── Payment Service (결제 이벤트)
  ├── Notification Service (알림 이벤트)
  └── Accommodation Service (가용성 업데이트)
```

### 2.2 사용자 관련 이벤트
```
User Service -> Kafka
  ├── Search Service (프로필 업데이트)
  └── Notification Service (설정 변경)
```

## 3. 데이터 일관성 전략

### 3.1 이벤트 소싱 패턴
- 모든 상태 변경을 이벤트로 저장
- 이벤트 스트림을 통한 상태 재구성
- 서비스별 이벤트 저장소 관리

### 3.2 CQRS 패턴
- 명령(Command)과 조회(Query) 분리
- 조회 모델 최적화
- 이벤트 기반 동기화

## 4. 캐싱 전략

### 4.1 Redis 캐시 계층
```
API Gateway
  ├── 사용자 세션 캐시
  ├── 검색 결과 캐시
  └── 정적 데이터 캐시
```

### 4.2 캐시 무효화 전략
- TTL 기반 자동 만료
- 이벤트 기반 수동 무효화
- 캐시 일관성 보장

## 5. 장애 처리 및 복구

### 5.1 서킷 브레이커 패턴
```
Service A -> Service B
  ├── 정상: 직접 통신
  ├── 장애: 캐시된 데이터 사용
  └── 복구: 점진적 트래픽 복원
```

### 5.2 재시도 전략
- 지수 백오프
- 최대 재시도 횟수 제한
- 폴백 메커니즘

## 6. 데이터 동기화

### 6.1 실시간 동기화
- gRPC 스트리밍
- WebSocket 연결
- 이벤트 기반 업데이트

### 6.2 배치 동기화
- 주기적인 데이터 동기화
- 증분 동기화
- 충돌 해결 전략

## 7. 보안 고려사항

### 7.1 데이터 암호화
- 전송 중 암호화 (TLS)
- 저장 데이터 암호화
- 키 관리

### 7.2 접근 제어
- 서비스 간 인증
- 데이터 접근 권한
- 감사 로깅 