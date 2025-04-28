# 예약 API 초기 설계 명세서

## 1. 기본 정보
- Base URL: `https://api.triphub.com/v1/reservations`
- Content-Type: `application/json`
- 인증: Bearer Token (JWT)

## 2. API 엔드포인트

### 2.1 예약 생성
```http
POST /
Content-Type: application/json

{
  "accommodationId": "string",
  "roomId": "string",
  "checkInDate": "string",
  "checkOutDate": "string",
  "numberOfGuests": "number"
}

Response 201 Created
{
  "id": "string",
  "status": "string",
  "totalPrice": "number",
  "paymentUrl": "string"
}
```

### 2.2 예약 조회
```http
GET /{reservationId}

Response 200 OK
{
  "id": "string",
  "accommodation": {
    "id": "string",
    "name": "string"
  },
  "room": {
    "id": "string",
    "name": "string"
  },
  "checkInDate": "string",
  "checkOutDate": "string",
  "numberOfGuests": "number",
  "status": "string",
  "totalPrice": "number"
}
```

### 2.3 예약 취소
```http
POST /{reservationId}/cancel

Response 200 OK
{
  "id": "string",
  "status": "string",
  "refundAmount": "number"
}
```

### 2.4 예약 목록 조회
```http
GET /
Query Parameters:
  - status: string
  - page: number
  - size: number

Response 200 OK
{
  "items": [
    {
      "id": "string",
      "accommodationName": "string",
      "roomName": "string",
      "checkInDate": "string",
      "checkOutDate": "string",
      "status": "string",
      "totalPrice": "number"
    }
  ],
  "totalPages": "number",
  "totalElements": "number"
}
```

## 3. 에러 응답

### 주요 에러 코드
- `RESV_001`: 예약을 찾을 수 없음
- `RESV_002`: 예약 불가능한 날짜
- `RESV_003`: 예약 취소 불가능한 상태
- `RESV_004`: 권한이 없음 