# 인증 API 초기 설계 명세서

## 1. 기본 정보
- Base URL: `https://api.triphub.com/v1/auth`
- Content-Type: `application/json`

## 2. API 엔드포인트

### 2.1 로그인
```http
POST /login
Content-Type: application/json

{
  "email": "string",
  "password": "string"
}

Response 200 OK
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": "number"
}
```

### 2.2 회원가입
```http
POST /signup
Content-Type: application/json

{
  "email": "string",
  "password": "string",
  "name": "string",
  "phone": "string"
}

Response 201 Created
{
  "id": "string",
  "email": "string",
  "name": "string"
}
```

### 2.3 토큰 갱신
```http
POST /refresh
Content-Type: application/json

{
  "refreshToken": "string"
}

Response 200 OK
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": "number"
}
```

## 3. 에러 응답

### 주요 에러 코드
- `AUTH_001`: 인증 실패
- `AUTH_002`: 토큰 만료
- `AUTH_003`: 유효하지 않은 토큰
- `AUTH_004`: 이메일 중복 