# MSA 트랜잭션 처리

## 왜 트랜잭션 처리가 문제인가?
- MSA에서는 **서비스마다 DB가 분리**되어 있음
- 기존 모놀리식처럼 하나의 DB에서 트랜잭션을 묶을 수 없음 (ACID 불가능)
- 예: 예약 성공 + 결제 실패 → 데이터 불일치 발생

---

## 분산 트랜잭션 처리 전략

| 전략 | 설명 | 특징 |
|------|------|------|
| 2PC (Two-Phase Commit) | 각 DB에 예비 커밋 요청 → 최종 커밋 | 락 발생, 복잡, 성능 저하로 거의 사용 안 함 |
| SAGA 패턴 | 각 서비스가 로컬 트랜잭션을 수행하고, 실패 시 보상 트랜잭션 실행 | **MSA에서 가장 현실적인 방식** |

---

## SAGA 패턴
> 여러 개의 로컬 트랜잭션을 이벤트 기반으로 연결하고, 실패 시 **보상 트랜잭션**으로 처리하는 패턴

---

## SAGA 구현 방식

### 1. Choreography (코레오그래피)
- **중앙 제어 없이** 서비스들이 이벤트를 주고받음
- Kafka 등 메시지 브로커 사용
- 이 방식을 더 많이 사용
**예시 흐름:**
```
  Booking → 이벤트 발행 (booking.created)
  Payment → 이벤트 수신 → 결제 처리 → payment.success 또는 payment.failed
  Booking → payment.failed 수신 → 예약 취소
```

장점: 구조 간단, 느슨한 결합, 확장성 우수  
단점: 흐름 복잡해지면 추적 어려움

---

### 2. Orchestration (오케스트레이션)
- **중앙의 SAGA 오케스트레이터**가 전체 프로세스를 제어
- 각 서비스에 명령 → 결과 수신 → 다음 단계 진행

**예시 흐름:**
Orchestrator → Booking 요청 → 결제 요청 → 알림 요청

장점: 흐름 추적 쉬움, 로직 명확  
단점: 중앙 집중, 장애 시 전체 영향 가능

---

## 보상 트랜잭션 (Compensation Transaction)

> 실패한 작업을 원상복구하는 트랜잭션

**예시:**
- 예약 생성 → 결제 실패 → 예약 취소
- 포인트 차감 → 배송 실패 → 포인트 복원

```
public void cancelBooking(Long bookingId) {
    bookingRepository.updateStatus(bookingId, "CANCELED");
}
```