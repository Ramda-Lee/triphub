# 리뷰 API 초기 설계 명세서

## 1. 기본 정보
- Base URL: `https://api.triphub.com/v1/reviews`
- Content-Type: `application/json`
- 인증: Bearer Token (JWT)

## 2. API 엔드포인트

### 2.1 리뷰 작성
```http
POST /
Content-Type: application/json

{
  "accommodationId": "string",
  "reservationId": "string",
  "rating": "number",
  "content": "string",
  "images": "string[]"
}

Response 201 Created
{
  "id": "string",
  "rating": "number",
  "content": "string",
  "images": "string[]",
  "createdAt": "string"
}
```

### 2.2 리뷰 조회
```http
GET /{reviewId}

Response 200 OK
{
  "id": "string",
  "userId": "string",
  "userName": "string",
  "accommodationId": "string",
  "accommodationName": "string",
  "rating": "number",
  "content": "string",
  "images": "string[]",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### 2.3 리뷰 수정
```http
PUT /{reviewId}
Content-Type: application/json

{
  "rating": "number",
  "content": "string",
  "images": "string[]"
}

Response 200 OK
{
  "id": "string",
  "rating": "number",
  "content": "string",
  "images": "string[]",
  "updatedAt": "string"
}
```

### 2.4 리뷰 삭제
```http
DELETE /{reviewId}

Response 204 No Content
```

### 2.5 숙소별 리뷰 목록 조회
```http
GET /accommodations/{accommodationId}
Query Parameters:
  - page: number
  - size: number
  - sort: string (createdAt,rating)

Response 200 OK
{
  "items": [
    {
      "id": "string",
      "userId": "string",
      "userName": "string",
      "rating": "number",
      "content": "string",
      "images": "string[]",
      "createdAt": "string"
    }
  ],
  "totalPages": "number",
  "totalElements": "number",
  "averageRating": "number"
}
```

### 2.6 사용자별 리뷰 목록 조회
```http
GET /users/{userId}
Query Parameters:
  - page: number
  - size: number
  - sort: string (createdAt,rating)

Response 200 OK
{
  "items": [
    {
      "id": "string",
      "accommodationId": "string",
      "accommodationName": "string",
      "rating": "number",
      "content": "string",
      "images": "string[]",
      "createdAt": "string"
    }
  ],
  "totalPages": "number",
  "totalElements": "number"
}
```

## 3. 에러 응답

### 주요 에러 코드
- `REV_001`: 리뷰를 찾을 수 없음
- `REV_002`: 리뷰 작성 권한 없음
- `REV_003`: 이미 리뷰를 작성함
- `REV_004`: 리뷰 수정 권한 없음
- `REV_005`: 리뷰 삭제 권한 없음 