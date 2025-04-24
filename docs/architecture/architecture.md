# Auth Service Architecture (TripHub)

## 개요

TripHub의 인증 시스템은 MSA 아키텍처에 기반하여 독립된 Auth 서비스로 구현됩니다.  
사용자는 이메일과 비밀번호로 회원가입/로그인하며, JWT 토큰을 통해 인증된 요청을 처리합니다.

---

## 주요 기능

- 회원가입
- 로그인 (JWT 발급)
- Access Token 인증
- Refresh Token을 통한 재발급
- 역할 기반 권한 처리 (USER, ADMIN)

---

## 시스템 구성도
```
[Client]
   ↓ (email, password)
[Auth Service]
   ↓
[User DB] ← 사용자 정보 저장
   ↓
[JWT 발급 (Access + Refresh)]
   ↓
[Client] ← Access Token + Refresh Token
```
---

## 인증 흐름

### 1. 회원가입
1. 이메일 중복 확인
2. 비밀번호 암호화 (BCrypt)
3. 사용자 정보 저장

### 2. 로그인
1. 이메일로 사용자 조회
2. 비밀번호 확인
3. JWT 발급 (Access Token + Refresh Token)
4. Refresh Token 저장 (Redis)
5. 클라이언트에 전달

### 3. Access Token 인증
1. 요청에 토큰 포함
2. JWT 필터에서 토큰 유효성 검사
3. 인증되면 사용자 정보 설정 후 서비스 진입

### 4. 토큰 재발급
1. 클라이언트가 Refresh Token 전달
2. 토큰 유효성 확인
3. 새 Access Token 발급

---

## 기술 스택

- Spring Boot
- Spring Security + JWT
- JPA (User Entity)
- BCryptPasswordEncoder
- Redis
- Jasypt (환경변수 암호화)

---

## JWT 구조

```json
{
  "sub": "user@email.com",
  "role": "USER",
  "iat": 1681170000,
  "exp": 1681173600
}
```
---

## 내부 구조(계층 구조)
```
Controller
   ↓
Service
   ↓
Repository
   ↓
Entity
   ↓
Security (Filter, Provider)
```
---

---

## Jasypt를 이용한 보안 설정

민감한 설정 값을 보호하기 위해 Jasypt를 사용합니다.

### 보호 대상 예시
- JWT 시크릿 키
- DB 접속 정보
- 메일 서버 계정 정보 등

### 사용 방식
- 암호화된 값을 `ENC(...)` 형식으로 저장
- 복호화 키는 환경 변수로 주입 (`jasypt.encryptor.password`)
- 실행 시 자동으로 복호화되어 사용됨

### 적용 예시
```properties
jwt.secret=ENC(QK38duW0jDFSS)
spring.datasource.password=ENC(Aj58k3uSdf1=)
```
---

## Refresh Token 저장소 - Redis

TripHub에서는 Refresh Token을 Redis에 저장하여 빠른 조회, TTL 기반 만료, 로그아웃 및 재발급 관리가 용이하도록 구성합니다.

- Redis Key: `refresh_token:{email}`
- Value: JWT 형식의 Refresh Token
- TTL: 7일

### 장점
- 빠른 속도 (메모리 기반)
- 자동 만료 지원
- 토큰 강제 무효화 용이
- MSA 환경에서 세션 상태 일관성 유지

### 보안 전략
- Access Token: 15분
- Refresh Token: 7일
- 재발급 시 Refresh Token도 갱신
- 로그아웃 시 Redis에서 삭제
