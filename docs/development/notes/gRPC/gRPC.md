# gRPC

## gRPC?
- 구글에서 개발한 오픈소스 고성능 RPC(Remote Procedure Call) 프레임 워크
- HTTP/2 기반으로 빠르고 효율적인 통신 지원
- Protocol Buffers(ProtoBuf)를 IDL(Interface Definition Language)로 사용

## REST vs gRPC
| 항목                 | REST                          | gRPC                                           |
|----------------------|-------------------------------|------------------------------------------------|
| 전송 프로토콜   | HTTP/1.1                      | HTTP/2                                        |
| 데이터 포맷      | JSON                          | Protocol Buffers (binary)                     |
| 성능             | 느림 (텍스트 기반, 무거움)    | 빠름 (바이너리, 헤더 압축, 경량화)           |
| 호출 방식        | 단방향 (Request-Response)     | Unary / Streaming (Server, Client, BiDi)     |
| 스키마 정의      | 명시적 정의 없음              | .proto 파일로 인터페이스 명세                |
| 코드 생성        | 수동 작성                     | proto 기반 자동 생성 (Stub 등)               |
| 브라우저 호환성  | 매우 높음                     | 낮음 (직접 호출 어려움, gRPC-Web 필요)       |
| 디버깅 편의성    | 높음 (JSON 기반, 사람이 읽기 쉬움) | 낮음 (바이너리 메시지)                     |
| 표준 문서화 도구 | OpenAPI (Swagger)             | 없음 (gRPC 자체 구조 사용)                   |
| 스트리밍 지원    | 제한적 (SSE, WebSocket 등 별도) | 기본 지원 (HTTP/2 기반 양방향 스트리밍)    |

## gRPC 동작 원리

###HTTP/2
- 하나의 TCP 연결로 다중 요청/응답을 처리 (Multiplexing)
- Header 압축 (HPACK)으로 오버헤드 감소
- 양방향 스트리밍 가능

### Protocol Buffers (Protobuf)
- google에서 만든 직렬화 포멧 (binary 기반)
- JSON 보다 작고 빠르며, 정해진 스키마에 따하 데이터를 직렬화/역직렬화
- .proto 파일에 메시지와 서비스 인터페이스 정의
- 컴파일하면 각 언어로 Stub 코드 자동 생성

#### Protobuf 구조 예시
```
  syntax = "proto3";

  message User {
    string name = 1;
    int32 age = 2;
    repeated string tags = 3;
  }
```
- 각 필드에는 번호를 붙인다 (1, 2, 3  등) -> 이 번호가 바이너리에서 key로 사용됨
- repeated는 배열/리스트 의미

#### 동작 방식
1. .proto 파일 정의
2. protoc로 Stub 및 메시지 코드 생성
3. Stub을 통해 메시지를 Protobuf로 직렬화
4. HTTP/2로 전송
5. 서버는 역직렬화하여 처리하고 응답 반환

## .proto 파일 예시
```
  syntax = "proto3";
  
  package example;

  service Greeter {
    rpc SayHello (HelloRequest) returns (HelloReply);
  }

  message HelloRequest {
    string name = 1;
  }

  message HelloReply {
    string message = 1;
  }
```
## gRPC 통신 방식 4가지
- Unary : 클라이언트 -> 서버 요청 1번, 응답 1번
- Server Streaming : 클라이언트 -> 요청 1번, 서버 -> 여러 응답
- Client Streaming : 클라이언트 -> 여러 요청, 서버 -> 응답 1번
- Bidireactional Streaming : 클라이언트, 서버 -> 서로 다중 메시지 송수신

## gRPC의 실제 통신 흐름
1. 클라이언트 Stub에서 SayHello(name="Alice") 호출
2. Stub은 HTTP/2로 서버에 바이너리 메시지 전송
3. 서버는 요청 메시지를 수신하고 메서드 실행
4. 응답 메시지를 생성하고 onNext() -> onCompleted()로 전송 종료
5. 클라이언트는 응답을 수신하고 결과 처리

# gRPC 핵심 개념
## 서비스 정의
- .proto 파일에 서비스 및 RPC 메서드 정의
- 클라이언트/서버는 이를 기준으로 코드를 생성함

## API 사용
- 서버는 정의된 서비스를 구현
- 클라이언트는 Stub을 통해 서버의 메서드를 호출
- 호출은 내부적으로 HTTP/2 스트림을 통해 처리됨

## Stub
- 클라이언트에서 생성된 프록시 객체
- 원격 서버의 메서드를 로컬 함수처럼 호출 가능
- 요청을 Protobuf로 직렬화하여 전송하고, 응답을 역직렬화함

## 동기 vs 비동기
- 대부분 언어에서 동기/비동기 API 모두 지원
- 비동기 API는 Future, callback, reactive 방식으로 응답 처리 가능

## 데드라인과 타임아웃
- 클라이어느가 호출 시 최대 대기 시간을 설정할 수 있다
- 취소 시 연결은 즉시 끊어짐

## 메타데이터
- 인증, 로깅 등의 목적으로 키-값 형태의 메타데이터 전송 가능
- 사용자 정의 키는 grpc- 접두사 사용 불가

## 채널
- 클라이언트와 서버 간의 연결을 나타냄
- Stub은 channel을 기반으로 생성되며, 연결 상태를 관리함