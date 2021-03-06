//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import Foundation
import Bugly
open class ActorApplicationDelegate: ActorSDKDelegateDefault, UIApplicationDelegate {
    
    
    
    public override init() {
        super.init()
        
        Bugly.start(withAppId: "3fc8148d60")
        ActorSDK.sharedActor().delegate = self
    }
    
    open func applicationDidFinishLaunching(_ application: UIApplication) {
        ActorSDK.sharedActor().applicationDidFinishLaunching(application)
    }
    
    open func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {

        ActorSDK.sharedActor().applicationDidFinishLaunching(application)
        return true
    }
    
    open func applicationDidBecomeActive(_ application: UIApplication) {
        ActorSDK.sharedActor().applicationDidBecomeActive(application)
    }
    
    open func applicationWillEnterForeground(_ application: UIApplication) {
        ActorSDK.sharedActor().applicationWillEnterForeground(application)
    }
    
    open func applicationDidEnterBackground(_ application: UIApplication) {
        ActorSDK.sharedActor().applicationDidEnterBackground(application)
    }
    
    open func applicationWillResignActive(_ application: UIApplication) {
        ActorSDK.sharedActor().applicationWillResignActive(application)
    }
    
    open func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        ActorSDK.sharedActor().application(application, didReceiveRemoteNotification: userInfo)
    }
    
    open func application(_ application: UIApplication, didRegister notificationSettings: UIUserNotificationSettings) {
        ActorSDK.sharedActor().application(application, didRegisterUserNotificationSettings: notificationSettings)
    }
    
    open func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        //swift3需要做个转换否则无法转换成string 为什么别问我。。。
        let nsataStr = NSData.init(data:deviceToken)
        
        let tokenString = "\(nsataStr)".replace(" ", dest: "").replace("<", dest: "").replace(">", dest: "")
        ActorSDK.sharedActor().pushRegisterToken(tokenString)
    }
    
    open func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        ActorSDK.sharedActor().application(application, didReceiveRemoteNotification: userInfo, fetchCompletionHandler: completionHandler)
    }
    
    open func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        ActorSDK.sharedActor().application(application, performFetchWithCompletionHandler: completionHandler)
    }
    
    open func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        return ActorSDK.sharedActor().application(application, openURL: url, sourceApplication: sourceApplication, annotation: annotation as AnyObject)
    }
    open func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
        return ActorSDK.sharedActor().application(app, openURL: url)
    }
    open func application(_ application: UIApplication, handleOpen url: URL) -> Bool {
        return ActorSDK.sharedActor().application(application, handleOpenURL: url)
    }
}
