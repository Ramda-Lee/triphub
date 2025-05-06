# Reservation Service gRPC API 명세서

## 1. 서비스 정의

```protobuf
syntax = "proto3";

package reservation;

option java_multiple_files = true;
option java_package = "com.triphub.reservation.proto";
option java_outer_classname = "ReservationServiceProto";

service ReservationService {
  // 예약 생성
  rpc CreateReservation(CreateReservationRequest) returns (CreateReservationResponse) {}
  
  // 예약 조회
  rpc GetReservation(GetReservationRequest) returns (GetReservationResponse) {}
  
  // 예약 취소
  rpc CancelReservation(CancelReservationRequest) returns (CancelReservationResponse) {}
  
  // 예약 상태 변경
  rpc UpdateReservationStatus(UpdateReservationStatusRequest) returns (UpdateReservationStatusResponse) {}
}

// 메시지 정의
message CreateReservationRequest {
  string accommodation_id = 1;
  string room_id = 2;
  string user_id = 3;
  string check_in_date = 4;
  string check_out_date = 5;
  int32 number_of_guests = 6;
  string special_requests = 7;
  int64 total_price = 8;
}

message CreateReservationResponse {
  string reservation_id = 1;
  string status = 2;
  string created_at = 3;
}

message GetReservationRequest {
  string reservation_id = 1;
}

message GetReservationResponse {
  string reservation_id = 1;
  string accommodation_id = 2;
  string room_id = 3;
  string user_id = 4;
  string check_in_date = 5;
  string check_out_date = 6;
  int32 number_of_guests = 7;
  int64 total_price = 8;
  string status = 9;
  string special_requests = 10;
  string created_at = 11;
  string updated_at = 12;
}

message CancelReservationRequest {
  string reservation_id = 1;
  string user_id = 2;
  string reason = 3;
}

message CancelReservationResponse {
  string reservation_id = 1;
  string status = 2;
  string cancelled_at = 3;
  int64 refund_amount = 4;
}

message UpdateReservationStatusRequest {
  string reservation_id = 1;
  string status = 2;
  string reason = 3;
}

message UpdateReservationStatusResponse {
  string reservation_id = 1;
  string status = 2;
  string updated_at = 3;
}
```

## 2. 에러 코드

| 코드 | 설명 |
|------|------|
| RES_001 | 예약을 찾을 수 없음 |
| RES_002 | 예약 가능한 기간이 아님 |
| RES_003 | 예약 가능한 인원 초과 |
| RES_004 | 예약이 이미 취소됨 |
| RES_005 | 예약 취소 실패 |
| RES_006 | 예약 상태 변경 실패 |
| RES_007 | 권한이 없음 |
| RES_008 | 내부 서버 오류 | 