# 결제 API 초기 설계 명세서

## 1. 기본 정보
- Base URL: `https://api.triphub.com/v1/payments`
- Content-Type: `application/json`
- 인증: Bearer Token (JWT)

## 2. API 엔드포인트

### 2.1 결제 요청
```http
POST /
Content-Type: application/json

{
  "reservationId": "string",
  "amount": "number",
  "paymentMethod": "string",
  "cardNumber": "string",
  "cardExpiry": "string",
  "cardCvc": "string"
}

Response 201 Created
{
  "id": "string",
  "status": "string",
  "amount": "number",
  "paymentMethod": "string",
  "transactionId": "string",
  "createdAt": "string"
}
```

### 2.2 결제 조회
```http
GET /{paymentId}

Response 200 OK
{
  "id": "string",
  "reservationId": "string",
  "status": "string",
  "amount": "number",
  "paymentMethod": "string",
  "transactionId": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### 2.3 결제 취소 (환불)
```http
POST /{paymentId}/cancel
Content-Type: application/json

{
  "reason": "string"
}

Response 200 OK
{
  "id": "string",
  "status": "string",
  "refundAmount": "number",
  "refundReason": "string",
  "updatedAt": "string"
}
```

### 2.4 결제 내역 조회
```http
GET /
Query Parameters:
  - reservationId: string
  - status: string
  - startDate: string (YYYY-MM-DD)
  - endDate: string (YYYY-MM-DD)
  - page: number
  - size: number

Response 200 OK
{
  "items": [
    {
      "id": "string",
      "reservationId": "string",
      "status": "string",
      "amount": "number",
      "paymentMethod": "string",
      "createdAt": "string"
    }
  ],
  "totalPages": "number",
  "totalElements": "number"
}
```

### 2.5 결제 웹훅 (외부 결제 서비스에서 호출)
```http
POST /webhook
Content-Type: application/json

{
  "paymentId": "string",
  "transactionId": "string",
  "status": "string",
  "amount": "number",
  "timestamp": "string"
}

Response 200 OK
{
  "status": "string"
}
```

## 3. 에러 응답

### 주요 에러 코드
- `PAY_001`: 결제를 찾을 수 없음
- `PAY_002`: 결제 실패
- `PAY_003`: 결제 취소 실패
- `PAY_004`: 결제 금액 불일치
- `PAY_005`: 결제 수단 오류
- `PAY_006`: 결제 권한 없음 