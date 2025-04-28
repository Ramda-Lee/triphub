# 숙박 API 초기 설계 명세서

## 1. 기본 정보
- Base URL: `https://api.triphub.com/v1/accommodations`
- Content-Type: `application/json`
- 인증: Bearer Token (JWT)

## 2. API 엔드포인트

### 2.1 숙소 검색
```http
GET /
Query Parameters:
  - location: string
  - checkInDate: string (YYYY-MM-DD)
  - checkOutDate: string (YYYY-MM-DD)
  - numberOfGuests: number
  - minPrice: number
  - maxPrice: number
  - amenities: string[]
  - page: number
  - size: number

Response 200 OK
{
  "items": [
    {
      "id": "string",
      "name": "string",
      "location": "string",
      "price": "number",
      "rating": "number",
      "reviewCount": "number",
      "thumbnailUrl": "string",
      "amenities": "string[]"
    }
  ],
  "totalPages": "number",
  "totalElements": "number"
}
```

### 2.2 숙소 상세 조회
```http
GET /{accommodationId}

Response 200 OK
{
  "id": "string",
  "name": "string",
  "description": "string",
  "location": "string",
  "rating": "number",
  "reviewCount": "number",
  "amenities": "string[]",
  "images": "string[]",
  "rooms": [
    {
      "id": "string",
      "name": "string",
      "description": "string",
      "price": "number",
      "capacity": "number",
      "bedType": "string",
      "amenities": "string[]",
      "images": "string[]"
    }
  ]
}
```

### 2.3 객실 가용성 확인
```http
GET /{accommodationId}/rooms/{roomId}/availability
Query Parameters:
  - checkInDate: string (YYYY-MM-DD)
  - checkOutDate: string (YYYY-MM-DD)

Response 200 OK
{
  "isAvailable": "boolean",
  "price": "number",
  "unavailableDates": "string[]"
}
```

## 3. 에러 응답

### 주요 에러 코드
- `ACCOM_001`: 숙소를 찾을 수 없음
- `ACCOM_002`: 객실을 찾을 수 없음
- `ACCOM_003`: 가용성 확인 실패
- `ACCOM_004`: 검색 조건이 유효하지 않음 