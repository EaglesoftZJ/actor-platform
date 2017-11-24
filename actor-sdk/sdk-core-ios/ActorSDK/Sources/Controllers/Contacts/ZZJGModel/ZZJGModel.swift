//
//  ZZJGModel.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/17.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit

class ZZJGModel: NSObject {
    var id:String!
    var mc:String!
    var fid:String!
    var wzh:Int = 0
    var lb:String!
    var szk:String!

    init(dict:[String:AnyObject]) {
        super.init()
        setValuesForKeys(dict)
    }
    
}

