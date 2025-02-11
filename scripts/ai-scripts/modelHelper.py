import sys

import random

sys.path.append("./gen-py/gen")
from service import constants


'''
1. 将请求封装成模型所需的类型(如果有需要特征，一并处理)
2. 请求模型返回打分数据
'''

class ModelHelper:
    def __init__(self):
        pass
    def getScoreFromModel(self, req):
        movieRanks = []
        if req.modelName == 'empty':
            movieRanks = [constants.MovieRankItem(m, -1.0) for m in req.movieIds]
        elif req.modelName == "random":
            movieRanks = [constants.MovieRankItem(m, random.random()*5) for m in req.movieIds]
        resp = constants.ModelResp(req.userId, movieRanks)
        return resp
