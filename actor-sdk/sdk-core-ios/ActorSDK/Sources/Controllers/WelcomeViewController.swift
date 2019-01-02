//
//  WelcomeViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/22.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
import SDWebImage
open class WelcomeViewController: AAViewController {

    var bgImage:String = ""
    let bgImageView: UIImageView  = UIImageView()
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        
        view.addSubview(bgImageView)
        
//        let defaults = UserDefaults.standard
//
//        if (defaults.object(forKey: "welcomeImage") != nil){
//            bgImage = defaults.object(forKey: "welcomeImage") as! String
//
//            let image = SDImageCache.shared().imageFromDiskCache(forKey: "launchImage")
//
//            bgImageView.sd_setImage(with: URL(string:bgImage)!, placeholderImage: image, options: SDWebImageOptions.retryFailed, completed: nil)
//            do{
//                let data = try Data.init(contentsOf: URL(string:bgImage)!)
//                let image = UIImage(data:data as Data,scale:1.0)
////                bgImageView.image = image
//                SDImageCache.shared().store(image, forKey: "launchImage", toDisk: true, completion: {
//
//                })
//            }catch{}
//
//        }
        bgImageView.image = UIImage.init(named: "welcomeImage.png")
        PushToController()
        
    }
    private func PushToController(){
//        if (UserDefaults.standard.object(forKey: "welcomeImage") != nil){sleep(1)}
        NotificationCenter.default.post(name: ActorSDK.sharedActor().switchRootController, object: nil)
    }
    override open func viewDidLayoutSubviews() {
        bgImageView.frame = view.frame
    }
   
}
