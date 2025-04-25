# Auth Service gRPC API 명세서

## 1. 서비스 정의

```protobuf
syntax = "proto3";

package auth;

option java_multiple_files = true;
option java_package = "com.triphub.auth.proto";
option java_outer_classname = "AuthServiceProto";

service AuthService {
  // 사용자 인증
  rpc Authenticate(AuthenticateRequest) returns (AuthenticateResponse) {}
  
  // 토큰 검증
  rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse) {}
  
  // 사용자 정보 조회
  rpc GetUserInfo(GetUserInfoRequest) returns (GetUserInfoResponse) {}
  
  // 사용자 권한 조회
  rpc GetUserRole(GetUserRoleRequest) returns (GetUserRoleResponse) {}
}

// 메시지 정의
message AuthenticateRequest {
  string token = 1;
}

message AuthenticateResponse {
  bool is_authenticated = 1;
  string user_id = 2;
  string role = 3;
}

message ValidateTokenRequest {
  string token = 1;
}

message ValidateTokenResponse {
  bool is_valid = 1;
  string user_id = 2;
  string role = 3;
}

message GetUserInfoRequest {
  string user_id = 1;
}

message GetUserInfoResponse {
  string user_id = 1;
  string email = 2;
  string name = 3;
  string role = 4;
  string social_type = 5;
}

message GetUserRoleRequest {
  string user_id = 1;
}

message GetUserRoleResponse {
  string role = 1;
}
```

## 2. 에러 코드

| 코드 | 설명 |
|------|------|
| AUTH_001 | 인증 실패 |
| AUTH_002 | 토큰 만료 |
| AUTH_003 | 유효하지 않은 토큰 |
| AUTH_004 | 사용자를 찾을 수 없음 |
| AUTH_005 | 권한이 없음 |
| AUTH_006 | 내부 서버 오류 | 