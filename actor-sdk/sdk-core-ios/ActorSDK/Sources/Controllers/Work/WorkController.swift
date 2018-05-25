//
//  WorkController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/31.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit
import WebKit
import WebViewJavascriptBridge
import CommonCrypto
import MBProgressHUD
class WorkController: AAViewController,WKNavigationDelegate {

    let wkWebView = WKWebView()
    var bridge = WebViewJavascriptBridge()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let statusHeight:CGFloat = UIApplication.shared.statusBarFrame.size.height
//        let topHeight:CGFloat = CGFloat(UIApplication.shared.statusBarFrame.size.height+self.navigationController!.navigationBar.frame.size.height)//状态栏+导航栏高度
        let tabHeight:CGFloat = CGFloat((self.tabBarController?.tabBar.frame.size.height)!)//tabbar高度
        
        wkWebView.frame = CGRect(x:0,y:statusHeight,width:view.frame.size.width,height:view.frame.size.height-statusHeight-tabHeight)
        view.addSubview(wkWebView)
        
        let htmlPath = "http://127.0.0.1:8086/m/main"
        wkWebView.load(URLRequest.init(url:URL.init(string: htmlPath)!))
        
        let net = NetManager()
        let user:String = UserDefaults.standard.string(forKey: "zh")!
        
//        net.reqeust(url: "http://192.168.1.182:8080/rest/phone/JcYhglManage/login", paramaters: ["zh":("ealgesoft_zaq1xsw2_cft6vgy7_"+user).md5()], method: .POST) { (res) in
//            if res != nil {
//                let dic = res as! NSDictionary
//                print(dic)
//                if dic["statusCode"] as! Int == 2 {
//                    self.createWebview(dic: dic)
//                }
//                else {
//                    self.textMBProgress(text: dic["errorReason"] as! String)
//                }//http://192.168.1.182:8080
//            }
//        }
    }
    
    func createWebview(dic:NSDictionary) {
        wkWebView.navigationDelegate = self
        WebViewJavascriptBridge.enableLogging()
        bridge = WebViewJavascriptBridge.init(forWebView: wkWebView)
        bridge.setWebViewDelegate(self)
        bridge.disableJavscriptAlertBoxSafetyTimeout()
        bridge.registerHandler("moa.zh") { (data, responseCallback) in
            responseCallback!(dic)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let statusBarWindow : UIView = UIApplication.shared.value(forKey: "statusBarWindow") as! UIView
        let statusBar : UIView = statusBarWindow.value(forKey: "statusBar") as! UIView
        if statusBar.responds(to:#selector(setter: UIView.backgroundColor)) {
            statusBar.backgroundColor = UIColor(red: 60/255.0, green: 95/255.0, blue: 150/255.0, alpha: 1.0)
        }
        self.navigationController?.setNavigationBarHidden(true, animated: true)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        let statusBarWindow : UIView = UIApplication.shared.value(forKey: "statusBarWindow") as! UIView
        let statusBar : UIView = statusBarWindow.value(forKey: "statusBar") as! UIView
        if statusBar.responds(to:#selector(setter: UIView.backgroundColor)) {
            statusBar.backgroundColor = UIColor.white
        }
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    public override init() {
        super.init()
        tabBarItem = UITabBarItem(title: "工作", img: "TabIconWork", selImage: "TabIconWorkHighlighted")
        self.title = "工作"
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func textMBProgress(text:String) -> () {
        let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
        hud.mode = MBProgressHUDMode.text
        hud.label.text = text
        hud.margin = 10
        hud.offset.y = 50
        hud.removeFromSuperViewOnHide = true
        hud.hide(animated: true, afterDelay: 1)
    }

}

extension String {
    func md5() ->String! {
        let str = self.cString(using: String.Encoding.utf8)
        let strLen = CUnsignedInt(self.lengthOfBytes(using: String.Encoding.utf8))
        let digestLen = Int(CC_MD5_DIGEST_LENGTH)
        let result = UnsafeMutablePointer<CUnsignedChar>.allocate(capacity: digestLen)
        CC_MD5(str!, strLen, result)
        let hash = NSMutableString()
        for i in 0 ..< digestLen {
            hash.appendFormat("%02x", result[i])
        }
        result.deinitialize()
        return String(format: hash as String)

    }
}

