# Accommodation Service gRPC API 명세서

## 1. 서비스 정의

```protobuf
syntax = "proto3";

package accommodation;

option java_multiple_files = true;
option java_package = "com.triphub.accommodation.proto";
option java_outer_classname = "AccommodationServiceProto";

service AccommodationService {
  // 숙소 가용성 확인
  rpc CheckAvailability(CheckAvailabilityRequest) returns (CheckAvailabilityResponse) {}
  
  // 숙소 정보 조회
  rpc GetAccommodationInfo(GetAccommodationInfoRequest) returns (GetAccommodationInfoResponse) {}
  
  // 객실 정보 조회
  rpc GetRoomInfo(GetRoomInfoRequest) returns (GetRoomInfoResponse) {}
  
  // 숙소 검색
  rpc SearchAccommodations(SearchAccommodationsRequest) returns (SearchAccommodationsResponse) {}
}

// 메시지 정의
message CheckAvailabilityRequest {
  string accommodation_id = 1;
  string room_id = 2;
  string check_in_date = 3;
  string check_out_date = 4;
  int32 number_of_guests = 5;
}

message CheckAvailabilityResponse {
  bool is_available = 1;
  int64 price = 2;
  repeated string unavailable_dates = 3;
}

message GetAccommodationInfoRequest {
  string accommodation_id = 1;
}

message GetAccommodationInfoResponse {
  string accommodation_id = 1;
  string name = 2;
  string description = 3;
  string location = 4;
  double rating = 5;
  int64 review_count = 6;
  repeated string amenities = 7;
  repeated string images = 8;
  repeated RoomInfo rooms = 9;
}

message RoomInfo {
  string room_id = 1;
  string name = 2;
  string description = 3;
  int64 price = 4;
  int32 capacity = 5;
  string bed_type = 6;
  repeated string amenities = 7;
  repeated string images = 8;
}

message GetRoomInfoRequest {
  string room_id = 1;
}

message GetRoomInfoResponse {
  RoomInfo room = 1;
}

message SearchAccommodationsRequest {
  string location = 1;
  string check_in_date = 2;
  string check_out_date = 3;
  int32 number_of_guests = 4;
  int64 min_price = 5;
  int64 max_price = 6;
  repeated string amenities = 7;
  int32 page = 8;
  int32 size = 9;
}

message SearchAccommodationsResponse {
  repeated AccommodationSummary accommodations = 1;
  int32 total_pages = 2;
  int64 total_elements = 3;
}

message AccommodationSummary {
  string accommodation_id = 1;
  string name = 2;
  string location = 3;
  int64 price = 4;
  double rating = 5;
  int64 review_count = 6;
  string thumbnail_url = 7;
  repeated string amenities = 8;
}
```

## 2. 에러 코드

| 코드 | 설명 |
|------|------|
| ACCOM_001 | 숙소를 찾을 수 없음 |
| ACCOM_002 | 객실을 찾을 수 없음 |
| ACCOM_003 | 가용성 확인 실패 |
| ACCOM_004 | 검색 조건이 유효하지 않음 |
| ACCOM_005 | 내부 서버 오류 |
| ACCOM_006 | 권한이 없음 | 