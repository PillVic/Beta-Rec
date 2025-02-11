import argparse
import sys

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer


sys.path.append("./gen-py/gen")
sys.path.append(".")
from service import ModelService, constants
from modelHelper import ModelHelper
'''

'''

class Server:
    def __init__(self):
        self.log = {}
        self.helper = ModelHelper()
    def ping(self, pingReq):
        pingResp = "pong"
        print("[ping]: req:{}, resp->{}".format(pingReq, pingResp))
        return pingResp
    def movieModelRank(self, req):
        print("[movieModelRank]:req:{}".format(req))
        resp = self.helper.getScoreFromModel(req)
        return resp


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, required=True, help="port number")
    args = parser.parse_args()
    port  = args.port

    handler = Server()
    processor = ModelService.Processor(handler)
    
    transport = TSocket.TServerSocket(host="127.0.0.1", port=port)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()
    server = TServer.TThreadPoolServer(processor, transport, tfactory, pfactory)
    server.setNumThreads(5)
    print("start serve....")
    server.serve()
