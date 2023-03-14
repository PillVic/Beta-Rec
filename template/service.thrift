
namespace py gen.service
namespace java gen.service

struct EchoReq {
    1: required i32 userId
    2: required string message
}

struct EchoResp {
    1: required i32 userId
    2: required string response
}

service Service{
    EchoResp echo(1: EchoReq req)
}
