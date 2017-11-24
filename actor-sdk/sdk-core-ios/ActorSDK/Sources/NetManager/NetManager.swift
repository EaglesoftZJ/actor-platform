//
//  NetManager.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/16.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
import AFNetworking

enum RequestMethod: String {
    case GET = "GET"
    case POST = "POST"
}

class NetManager: AFHTTPSessionManager {
    // 单例
    static let sharedTools: NetManager = {
        let instance = NetManager()
        instance.responseSerializer.acceptableContentTypes?.insert("text/html")
        instance.responseSerializer.acceptableContentTypes?.insert("text/plain")
        return instance
    }()
    
    func reqeust(url: String, paramaters: [String: Any]? = nil, method: RequestMethod = .GET, callBack:@escaping (_ responseObject:Any?)->()){
        
        //抽取请求成功和失败的闭包
        let success = {
            (task: URLSessionDataTask, responseObject: Any?) in callBack(responseObject)
        }
        let failure = {
            (task: URLSessionDataTask?,error: Error) in
            callBack(nil)
        }
        
        //GET请求网络数据
        if method == .GET {
            self.get(url, parameters: paramaters, progress: nil, success: success, failure: failure)
        }
        //POST请求网络数据
        if method == .POST {
            self.post(url, parameters: paramaters, progress: nil, success: success, failure: failure)
        }
    }
}
