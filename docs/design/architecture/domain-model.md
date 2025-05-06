# TripHub 도메인 모델

## 1. 핵심 도메인 엔티티

### 1.1 사용자 도메인
```
User
├── id: UUID
├── email: String
├── password: String (암호화)
├── name: String
├── phone: String
├── role: UserRole (USER, ADMIN)
└── status: UserStatus (ACTIVE, INACTIVE, BLOCKED)
```

### 1.2 숙소 도메인
```
Accommodation
├── id: UUID
├── name: String
├── description: String
├── address: Address
├── type: AccommodationType (HOTEL, GUESTHOUSE, PENSION)
├── rating: Float
├── price: Money
└── status: AccommodationStatus (ACTIVE, INACTIVE)

Room
├── id: UUID
├── accommodationId: UUID
├── name: String
├── description: String
├── capacity: Integer
├── price: Money
└── status: RoomStatus (AVAILABLE, OCCUPIED, MAINTENANCE)
```

### 1.3 예약 도메인
```
Reservation
├── id: UUID
├── userId: UUID
├── roomId: UUID
├── checkIn: DateTime
├── checkOut: DateTime
├── totalPrice: Money
├── status: ReservationStatus (PENDING, CONFIRMED, CANCELLED)
└── paymentStatus: PaymentStatus (PENDING, COMPLETED, REFUNDED)

Payment
├── id: UUID
├── reservationId: UUID
├── amount: Money
├── method: PaymentMethod (CREDIT_CARD, BANK_TRANSFER)
├── status: PaymentStatus
└── transactionId: String
```

### 1.4 리뷰 도메인
```
Review
├── id: UUID
├── userId: UUID
├── accommodationId: UUID
├── reservationId: UUID
├── rating: Integer (1-5)
├── content: String
└── status: ReviewStatus (ACTIVE, DELETED)
```

## 2. 도메인 규칙

### 2.1 예약 규칙
- 체크인 날짜는 현재 날짜보다 이후여야 함
- 체크아웃 날짜는 체크인 날짜보다 이후여야 함
- 같은 객실의 예약 기간이 중복될 수 없음
- 예약 취소는 체크인 24시간 전까지 가능

### 2.2 결제 규칙
- 예약 확정을 위해서는 결제가 완료되어야 함
- 환불은 예약 취소 정책에 따라 처리
- 결제 실패 시 예약은 자동 취소

### 2.3 리뷰 규칙
- 실제 숙박이 완료된 예약에 대해서만 리뷰 작성 가능
- 리뷰 작성 기간은 체크아웃 후 14일 이내
- 한 예약당 하나의 리뷰만 작성 가능

## 3. 도메인 이벤트

### 3.1 예약 관련 이벤트
- `ReservationCreatedEvent`
- `ReservationConfirmedEvent`
- `ReservationCancelledEvent`

### 3.2 결제 관련 이벤트
- `PaymentCompletedEvent`
- `PaymentFailedEvent`
- `RefundCompletedEvent`

### 3.3 리뷰 관련 이벤트
- `ReviewCreatedEvent`
- `ReviewUpdatedEvent`
- `ReviewDeletedEvent`

## 4. 도메인 서비스

### 4.1 예약 도메인 서비스
- `ReservationService`
  - 예약 생성
  - 예약 확인
  - 예약 취소
  - 가용성 확인

### 4.2 결제 도메인 서비스
- `PaymentService`
  - 결제 처리
  - 환불 처리
  - 결제 상태 확인

### 4.3 리뷰 도메인 서비스
- `ReviewService`
  - 리뷰 작성
  - 리뷰 수정
  - 리뷰 삭제
  - 평점 계산

## 5. 값 객체 (Value Objects)

### 5.1 주소
```
Address
├── street: String
├── city: String
├── state: String
├── country: String
└── zipCode: String
```

### 5.2 금액
```
Money
├── amount: BigDecimal
└── currency: Currency
```

## 6. 도메인 예외

### 6.1 예약 도메인 예외
- `ReservationNotAvailableException`
- `InvalidReservationDateException`
- `ReservationCancellationNotAllowedException`

### 6.2 결제 도메인 예외
- `PaymentFailedException`
- `RefundNotAllowedException`
- `InvalidPaymentAmountException`

### 6.3 리뷰 도메인 예외
- `ReviewNotAllowedException`
- `InvalidReviewRatingException`
- `ReviewPeriodExpiredException` 