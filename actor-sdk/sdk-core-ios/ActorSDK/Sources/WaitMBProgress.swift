//
//  WaitMBProgress.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/17.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
import MBProgressHUD
class WaitMBProgress: NSObject {
    var hud = MBProgressHUD()
    
    override init() {
        
    }
    open func show(view:UIView){
        hud = MBProgressHUD(view:view)
        hud.mode = MBProgressHUDMode.indeterminate
        hud.removeFromSuperViewOnHide = true
        view.addSubview(hud)
        view.bringSubview(toFront: hud)
        hud.show(animated: true)
    }
    open func hide(){
        hud.hide(animated: true)
    }
    open func text(text:String,view:UIView){
        let hud = MBProgressHUD.showAdded(to: view, animated: true)
        hud.mode = MBProgressHUDMode.text
        hud.label.text = text
        hud.margin = 10
        hud.offset.y = 50
        hud.removeFromSuperViewOnHide = true
        hud.hide(animated: true, afterDelay: 1)
    }
}


