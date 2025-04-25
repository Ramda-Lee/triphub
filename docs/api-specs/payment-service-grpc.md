# Payment Service gRPC API 명세서

## 1. 서비스 정의

```protobuf
syntax = "proto3";

package payment;

option java_multiple_files = true;
option java_package = "com.triphub.payment.proto";
option java_outer_classname = "PaymentServiceProto";

service PaymentService {
  // 결제 처리
  rpc ProcessPayment(ProcessPaymentRequest) returns (ProcessPaymentResponse) {}
  
  // 결제 조회
  rpc GetPayment(GetPaymentRequest) returns (GetPaymentResponse) {}
  
  // 결제 취소
  rpc CancelPayment(CancelPaymentRequest) returns (CancelPaymentResponse) {}
  
  // 결제 상태 변경
  rpc UpdatePaymentStatus(UpdatePaymentStatusRequest) returns (UpdatePaymentStatusResponse) {}
}

// 메시지 정의
message ProcessPaymentRequest {
  string order_id = 1;
  int64 amount = 2;
  string payment_method = 3;
  CardInfo card_info = 4;
  repeated PaymentItem items = 5;
  CustomerInfo customer_info = 6;
}

message CardInfo {
  string card_number = 1;
  string expiry_date = 2;
  string cvv = 3;
  string card_password = 4;
}

message PaymentItem {
  string id = 1;
  string name = 2;
  int32 quantity = 3;
  int64 price = 4;
}

message CustomerInfo {
  string name = 1;
  string email = 2;
  string phone = 3;
}

message ProcessPaymentResponse {
  string payment_id = 1;
  string order_id = 2;
  int64 amount = 3;
  string status = 4;
  string payment_method = 5;
  string paid_at = 6;
  string created_at = 7;
}

message GetPaymentRequest {
  string payment_id = 1;
}

message GetPaymentResponse {
  string payment_id = 1;
  string order_id = 2;
  int64 amount = 3;
  string status = 4;
  string payment_method = 5;
  string paid_at = 6;
  string created_at = 7;
  repeated PaymentItem items = 8;
  CustomerInfo customer_info = 9;
}

message CancelPaymentRequest {
  string payment_id = 1;
  string reason = 2;
}

message CancelPaymentResponse {
  string payment_id = 1;
  string order_id = 2;
  int64 amount = 3;
  string status = 4;
  string cancelled_at = 5;
}

message UpdatePaymentStatusRequest {
  string payment_id = 1;
  string status = 2;
}

message UpdatePaymentStatusResponse {
  string payment_id = 1;
  string status = 2;
  string updated_at = 3;
}
```

## 2. 에러 코드

| 코드 | 설명 |
|------|------|
| PAY_001 | 결제 정보를 찾을 수 없음 |
| PAY_002 | 결제 금액이 유효하지 않음 |
| PAY_003 | 결제 수단이 유효하지 않음 |
| PAY_004 | 카드 정보가 유효하지 않음 |
| PAY_005 | 결제가 이미 취소됨 |
| PAY_006 | 결제 취소 실패 |
| PAY_007 | 결제 처리 중 오류 발생 |
| PAY_008 | 권한이 없음 |
| PAY_009 | 내부 서버 오류 | 