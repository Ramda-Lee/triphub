# Review Service gRPC API 명세서

## 1. 서비스 정의

```protobuf
syntax = "proto3";

package review;

option java_multiple_files = true;
option java_package = "com.triphub.review.proto";
option java_outer_classname = "ReviewServiceProto";

service ReviewService {
  // 리뷰 생성
  rpc CreateReview(CreateReviewRequest) returns (CreateReviewResponse) {}
  
  // 리뷰 조회
  rpc GetReview(GetReviewRequest) returns (GetReviewResponse) {}
  
  // 리뷰 수정
  rpc UpdateReview(UpdateReviewRequest) returns (UpdateReviewResponse) {}
  
  // 리뷰 삭제
  rpc DeleteReview(DeleteReviewRequest) returns (DeleteReviewResponse) {}
  
  // 리뷰 목록 조회
  rpc GetReviews(GetReviewsRequest) returns (GetReviewsResponse) {}
  
  // 평점 통계 조회
  rpc GetRatingStats(GetRatingStatsRequest) returns (GetRatingStatsResponse) {}
}

// 메시지 정의
message CreateReviewRequest {
  string accommodation_id = 1;
  string user_id = 2;
  int32 rating = 3;
  string title = 4;
  string content = 5;
  repeated string images = 6;
}

message CreateReviewResponse {
  string review_id = 1;
  string created_at = 2;
}

message GetReviewRequest {
  string review_id = 1;
}

message GetReviewResponse {
  string review_id = 1;
  string accommodation_id = 2;
  string user_id = 3;
  int32 rating = 4;
  string title = 5;
  string content = 6;
  repeated string images = 7;
  string created_at = 8;
  string updated_at = 9;
  UserInfo user_info = 10;
}

message UserInfo {
  string user_id = 1;
  string name = 2;
  string profile_image = 3;
}

message UpdateReviewRequest {
  string review_id = 1;
  string user_id = 2;
  int32 rating = 3;
  string title = 4;
  string content = 5;
  repeated string images = 6;
}

message UpdateReviewResponse {
  string review_id = 1;
  string updated_at = 2;
}

message DeleteReviewRequest {
  string review_id = 1;
  string user_id = 2;
}

message DeleteReviewResponse {
  string review_id = 1;
  string deleted_at = 2;
}

message GetReviewsRequest {
  string accommodation_id = 1;
  int32 page = 2;
  int32 size = 3;
  string sort_by = 4;
  string sort_direction = 5;
}

message GetReviewsResponse {
  repeated ReviewInfo reviews = 1;
  int32 total_pages = 2;
  int64 total_elements = 3;
}

message ReviewInfo {
  string review_id = 1;
  string user_id = 2;
  int32 rating = 3;
  string title = 4;
  string content = 5;
  repeated string images = 6;
  string created_at = 7;
  string updated_at = 8;
  UserInfo user_info = 9;
}

message GetRatingStatsRequest {
  string accommodation_id = 1;
}

message GetRatingStatsResponse {
  double average_rating = 1;
  int64 total_reviews = 2;
  map<int32, int64> rating_distribution = 3;
}
```

## 2. 에러 코드

| 코드 | 설명 |
|------|------|
| REV_001 | 리뷰를 찾을 수 없음 |
| REV_002 | 리뷰 작성 권한이 없음 |
| REV_003 | 리뷰 수정 권한이 없음 |
| REV_004 | 리뷰 삭제 권한이 없음 |
| REV_005 | 평점이 유효하지 않음 |
| REV_006 | 리뷰 내용이 유효하지 않음 |
| REV_007 | 이미 리뷰를 작성함 |
| REV_008 | 내부 서버 오류 | 