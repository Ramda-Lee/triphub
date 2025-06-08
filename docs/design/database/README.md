# TripHub 데이터베이스 설계

## 서비스 간 연관관계

### Accommodation Service
- `room.id` → Reservation Service의 `reservation.room_id` 참조
- `accommodation.id` → Review Service의 `review.accommodation_id` 참조
- `accommodation.id` → Reservation Service의 `cancellation_policy.accommodation_id` 참조

### Reservation Service
- `reservation.id` → Payment Service의 `payment.reservation_id` 참조
- `reservation.id` → Review Service의 `review.reservation_id` 참조

### Payment Service
- `payment.id` → Review Service의 `review.payment_id` 참조

### Review Service
- `review.accommodation_id` → Accommodation Service의 `accommodation.id` 참조
- `review.reservation_id` → Reservation Service의 `reservation.id` 참조

## 서비스별 ERD
- [Accommodation 서비스 ERD](https://dbdiagram.io/d/67f689534f7afba184f7b40a)
- [Reservation 서비스 ERD](https://dbdiagram.io/d/ACCOMMODATION-684534b85a9a94714e63af89)
- [Payment 서비스 ERD](https://dbdiagram.io/d/6845350b5a9a94714e63b60b)
- [Review 서비스 ERD](https://dbdiagram.io/d/REVIEW-6845372a5a9a94714e63d82c)

## 서비스별 데이터베이스 스키마
- [Accommodation 서비스 스키마](accommodation/accommodation.sql)
- [Reservation 서비스 스키마](reservation/reservation.sql)
- [Payment 서비스 스키마](payment/payment.sql)
- [Review 서비스 스키마](review/review.sql)

> 위 ERD는 `dbdiagram.io`를 사용해 작성되었습니다.
> ERD DSL, SQL DDL 파일은 `/docs/ERD/` 디렉토리에 함께 관리됩니다.