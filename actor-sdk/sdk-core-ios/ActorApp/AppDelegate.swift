//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import Foundation

import ActorSDK
import UIKit

open class AppDelegate : ActorApplicationDelegate,CommonServiceDelegate {
    //# 小于服务器版本号{"welcomePage_bg":"http:\/\/61.175.100.14:5433\/photoImage\/bg1-2560.png","loginPage_bg":"http:\/\/61.175.100.14:5433\/photoImage\/bg-2560.png","loginPage_but":"http:\/\/61.175.100.14:5433\/photoImage\/login_but.png","version":1,"canUpdate":true}
    //# 大于，等于服务器版本号{"canUpdate":false}
    public func serviceSuccess(_ svc: CommonService!, object obj: Any!) {
        if svc == image
        {
            guard (obj as? NSDictionary) != nil else{return}
            let dic:[String:AnyObject] = obj as! Dictionary
            let defaults = UserDefaults.standard
            if dic["canUpdate"] as! Int == 1 {
                defaults.set(String(describing: dic["version"]), forKey: "version")
                defaults.set(dic["welcomePage_bg"], forKey: "welcomeImage")
                defaults.set(dic["loginPage_bg"], forKey: "loginImage")
                defaults.set(dic["loginPage_but"], forKey: "loginBtnImage")
                defaults.synchronize()
            }
        }
        else if svc == companyService
        {
            guard (obj as? NSDictionary) != nil else{return}
            let result_dict = obj as! NSDictionary
            UserDefaults.standard.set(result_dict, forKey: "ZZJG")
            UserDefaults.standard.synchronize()
        }
    }
    public func serviceFail(_ svc: CommonService!, info: String!) {
        
    }
    
    let image = PhoneImageService()
    let companyService = CompanyService()
    var window: UIWindow?
    
    override init() {
        super.init()
        
        ActorSDK.sharedActor().inviteUrlHost = "quit.email"
        ActorSDK.sharedActor().inviteUrlScheme = "actor"
        
        ActorSDK.sharedActor().style.searchStatusBarStyle = .default
        
        // Enabling experimental features
        ActorSDK.sharedActor().enableExperimentalFeatures = true
        
        ActorSDK.sharedActor().enableCalls = true
        
        ActorSDK.sharedActor().enableVideoCalls = true
        
        // Setting Development Push Id
        ActorSDK.sharedActor().apiPushId = 1
        
        ActorSDK.sharedActor().authStrategy = .usernameOnly
        
        ActorSDK.sharedActor().style.dialogAvatarSize = 58
        
        ActorSDK.sharedActor().autoJoinGroups = ["actor_news"]
        
        // Creating Actor
        ActorSDK.sharedActor().createActor()
        
    }
    
    open override func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]?) -> Bool {
        let _ = super.application(application, didFinishLaunchingWithOptions: launchOptions)
        //#注册切换Root控制器通知
        NotificationCenter.default.addObserver(self, selector: #selector(switchRootViewController), name: ActorSDK.sharedActor().switchRootController, object: nil)
//        ActorSDK.sharedActor().presentMessengerInNewWindow()
        
        window = UIWindow(frame:UIScreen.main.bounds)
        window?.backgroundColor = UIColor.white
        window?.rootViewController = WelcomeViewController()
        window?.makeKeyAndVisible()
        
        
        //#启动图
        addLaunchController()
        //#组织架构
//        companyService.delegate = self
//        companyService.chooseCompany()
        
        return true
    }
    
//    open override func actorRootControllers() -> [UIViewController]? {
//        return [AAContactsViewController(), AARecentViewController(), AASettingsViewController()]
//    }
    
    open override func actorRootInitialControllerIndex() -> Int? {
        return 0
    }
    @objc func switchRootViewController(){
        ActorSDK.sharedActor().presentMessengerInNewWindow()
    }
    private func addLaunchController() {
        image.delegate = self
        image.getLaunchImage()
    }
}
