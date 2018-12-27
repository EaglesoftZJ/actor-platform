//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import UIKit

open class AANavigationController: UINavigationController {
    
    fileprivate let binder = AABinder()
    
    public init() {
        super.init(nibName: nil, bundle: nil)
    }
    
    public override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
    }
    
    public override init(rootViewController: UIViewController) {
        super.init(rootViewController: rootViewController)
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override func viewDidLoad() {
        super.viewDidLoad()
        
        styleNavBar()
        
//         Enabling app state sync progress
//        self.setPrimaryColor(MainAppTheme.navigation.progressPrimary)
//        self.setSecondaryColor(MainAppTheme.navigation.progressSecondary)
        
        binder.bind(Actor.getGlobalState().isSyncing, valueModel2: Actor.getGlobalState().isConnecting) { (value1: JavaLangBoolean?, value2: JavaLangBoolean?) -> () in
            let thread:Thread = Thread()
            if value1!.booleanValue() || value2!.booleanValue() {
                Thread.sleep(forTimeInterval: 0.4)
//                self.showProgress()
//                self.setIndeterminate(true)
            } else {
                 thread.start()
//                self.finishProgress()
            }
        }
        
    }
    
    open override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        styleNavBar()

        UIApplication.shared.setStatusBarStyle(ActorSDK.sharedActor().style.vcStatusBarStyle, animated: true)
    }
    
    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return ActorSDK.sharedActor().style.vcStatusBarStyle
    }
    
    fileprivate func styleNavBar() {
        navigationBar.titleTextAttributes =
            [NSForegroundColorAttributeName: ActorSDK.sharedActor().style.navigationTitleColor]
        navigationBar.tintColor = ActorSDK.sharedActor().style.navigationTintColor
        navigationBar.barTintColor = ActorSDK.sharedActor().style.navigationBgColor
//        navigationBar.hairlineHidden = ActorSDK.sharedActor().style.navigationHairlineHidden
        
        view.backgroundColor = ActorSDK.sharedActor().style.vcBgColor
    }
    
    open override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        if self.viewControllers.count > 0 {
            viewController.hidesBottomBarWhenPushed = true
        }
        super.pushViewController(viewController, animated: animated)
        
        if self.tabBarController != nil {
            let frame:CGRect = self.tabBarController!.tabBar.frame
            if !frame.equalTo(CGRect.zero) {
                self.tabBarController!.tabBar.frame = frame
            }
        }
    }
}

