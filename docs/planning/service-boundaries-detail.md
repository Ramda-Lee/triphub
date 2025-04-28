# TripHub 서비스 경계 상세 정의

## 1. 도메인 이벤트 정의

### 1.1 예약 도메인 이벤트
- `ReservationCreatedEvent`
  - 예약 생성 시 발생
  - 결제 서비스로 전파
- `ReservationConfirmedEvent`
  - 결제 완료 후 발생
  - 숙박 서비스로 전파
- `ReservationCancelledEvent`
  - 예약 취소 시 발생
  - 결제 서비스로 전파 (환불 처리)

### 1.2 결제 도메인 이벤트
- `PaymentCompletedEvent`
  - 결제 완료 시 발생
  - 예약 서비스로 전파
- `PaymentFailedEvent`
  - 결제 실패 시 발생
  - 예약 서비스로 전파
- `RefundCompletedEvent`
  - 환불 완료 시 발생
  - 예약 서비스로 전파

### 1.3 리뷰 도메인 이벤트
- `ReviewCreatedEvent`
  - 리뷰 작성 시 발생
  - 숙박 서비스로 전파 (평점 업데이트)
- `ReviewUpdatedEvent`
  - 리뷰 수정 시 발생
  - 숙박 서비스로 전파
- `ReviewDeletedEvent`
  - 리뷰 삭제 시 발생
  - 숙박 서비스로 전파

## 2. 데이터 모델 상세 정의

### 2.1 예약 서비스 데이터 모델
```protobuf
message Reservation {
  string id = 1;
  string accommodation_id = 2;
  string room_id = 3;
  string user_id = 4;
  string check_in_date = 5;
  string check_out_date = 6;
  int32 number_of_guests = 7;
  int64 total_price = 8;
  ReservationStatus status = 9;
  google.protobuf.Timestamp created_at = 10;
  google.protobuf.Timestamp updated_at = 11;
}

enum ReservationStatus {
  PENDING = 0;
  CONFIRMED = 1;
  CANCELLED = 2;
  COMPLETED = 3;
}
```

### 2.2 결제 서비스 데이터 모델
```protobuf
message Payment {
  string id = 1;
  string reservation_id = 2;
  int64 amount = 3;
  PaymentStatus status = 4;
  string payment_method = 5;
  string transaction_id = 6;
  google.protobuf.Timestamp created_at = 7;
  google.protobuf.Timestamp updated_at = 8;
}

enum PaymentStatus {
  PENDING = 0;
  COMPLETED = 1;
  FAILED = 2;
  REFUNDED = 3;
}
```

## 3. API 계약 상세 정의

### 3.1 예약 서비스 API
```protobuf
service ReservationService {
  rpc CreateReservation(CreateReservationRequest) returns (CreateReservationResponse);
  rpc GetReservation(GetReservationRequest) returns (GetReservationResponse);
  rpc CancelReservation(CancelReservationRequest) returns (CancelReservationResponse);
  rpc UpdateReservationStatus(UpdateReservationStatusRequest) returns (UpdateReservationStatusResponse);
}
```

### 3.2 결제 서비스 API
```protobuf
service PaymentService {
  rpc ProcessPayment(ProcessPaymentRequest) returns (ProcessPaymentResponse);
  rpc GetPayment(GetPaymentRequest) returns (GetPaymentResponse);
  rpc ProcessRefund(ProcessRefundRequest) returns (ProcessRefundResponse);
}
```

## 4. 비즈니스 시나리오 검증

### 4.1 예약 생성 시나리오
1. 사용자가 예약 요청
2. 예약 서비스가 숙박 서비스에 가용성 확인
3. 가용성 확인 후 예약 생성
4. 결제 서비스에 결제 요청 이벤트 발행
5. 결제 완료 후 예약 상태 업데이트
6. 숙박 서비스에 예약 확정 이벤트 발행

### 4.2 예약 취소 시나리오
1. 사용자가 예약 취소 요청
2. 예약 서비스가 예약 상태 업데이트
3. 결제 서비스에 환불 요청 이벤트 발행
4. 환불 완료 후 예약 상태 업데이트
5. 숙박 서비스에 예약 취소 이벤트 발행

## 5. 성능 및 확장성 고려사항

### 5.1 성능 최적화
- 캐싱 전략
  - 숙소 정보 캐싱
  - 가용성 정보 캐싱
  - 사용자 정보 캐싱
- 데이터베이스 최적화
  - 인덱스 전략
  - 파티셔닝 전략

### 5.2 확장성 전략
- 서비스별 독립적인 스케일링
- 데이터베이스 샤딩 전략
- 메시지 큐 파티셔닝 