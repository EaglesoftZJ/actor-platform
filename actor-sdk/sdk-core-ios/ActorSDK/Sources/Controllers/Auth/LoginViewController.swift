//
//  LoginViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/10.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
import Masonry
import MBProgressHUD
import SDWebImage
class LoginViewController: AAAuthViewController,UITextFieldDelegate {
    
    let logoImgView = UIImageView()
    let backgroundImgView = UIImageView()
    let welcomeLabel = UILabel()
    let loginBtn = UIButton()
    let checkRemind = UIButton()
    let userField = UITextField()
    let pwdField = UITextField()
    let companyLabel = UILabel()
    let companyBtn = UIButton()
    var companyIP:String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        backgroundImgView.image = UIImage.bundled("backgroundImg.jpg")
        
        loginBtn.setBackgroundImage(UIImage.bundled("loginBackground"), for: .normal)
        loginBtn.setTitle("登录", for: .normal)
        loginBtn.setTitleColor(UIColor.white, for: .normal)
        loginBtn.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        loginBtn.addTarget(self, action: #selector(loginAction), for: UIControlEvents.touchUpInside)
        
        userField.placeholder = "用户名"
        userField.setValue(UIColor.white, forKeyPath: "_placeholderLabel.textColor")
        userField.keyboardType = .default
        
        pwdField.placeholder = "密码"
        pwdField.isSecureTextEntry = true
        pwdField.clearButtonMode = UITextFieldViewMode.whileEditing
        pwdField.setValue(UIColor.white, forKeyPath: "_placeholderLabel.textColor")
        
        companyLabel.textColor = UIColor.white
        companyLabel.font = UIFont.systemFont(ofSize: 16)
        companyLabel.text = "宁波舟山港舟山港务有限公司"
        
        companyBtn.setImage(UIImage.bundled("选择"), for: .normal)
        companyBtn.addTarget(self, action: #selector(chooseCompany), for: .touchUpInside)
        
        checkRemind.setImage(UIImage.bundled("不记住"), for: .normal)
        checkRemind.isSelected = false
        checkRemind.titleLabel?.font = UIFont.boldTextFontOfSize(14)
        checkRemind.imageView?.contentMode = UIViewContentMode.scaleAspectFit//不拉伸
        checkRemind.imageEdgeInsets = UIEdgeInsets(top: 0, left: -20, bottom: 0, right: 0)
        checkRemind.setTitle("记住用户名和密码", for: .normal)
        checkRemind.setTitleColor(UIColor.white, for: .normal)
        checkRemind.addTarget(self, action: #selector(checkAutoLogin(button:)), for: .touchUpInside)
        
        let defaults = UserDefaults.standard
        if defaults.object(forKey: "loginImage") != nil{
            let loginImg = defaults.object(forKey: "loginImage") as! String
            let loginBtnImg = defaults.object(forKey: "loginBtnImage") as! String
            
            if SDImageCache.shared().imageFromDiskCache(forKey: "loginImage") != nil{
                backgroundImgView.image = SDImageCache.shared().imageFromDiskCache(forKey: "loginImage")
            }
            if SDImageCache.shared().imageFromDiskCache(forKey: "loginBtnImage") != nil{
                loginBtn.setBackgroundImage(SDImageCache.shared().imageFromDiskCache(forKey: "loginBtnImage"), for: .normal)
            }
            do{
                let data = try Data.init(contentsOf: URL(string:loginImg)!)
                let image = UIImage(data:data as Data,scale:1.0)
                SDImageCache.shared().store(image, forKey: "loginImage", toDisk: true, completion:nil)
            }catch{}
            
            do{
                let btnData = try Data.init(contentsOf: URL(string:loginBtnImg)!)
                let btnImage = UIImage(data:btnData as Data,scale:1.0)
                SDImageCache.shared().store(btnImage, forKey: "loginBtnImage", toDisk: true, completion:nil)
            }catch{}
            
        }
        
        view.addSubview(backgroundImgView)
        view.addSubview(loginBtn)
        view.addSubview(checkRemind)
        view.addSubview(userField)
        view.addSubview(pwdField)
        view.addSubview(companyLabel)
        view.addSubview(companyBtn)
    }
    
    func loginAction(){
        let userDefault = UserDefaults.standard
        
        let user = userField.text!.trim()
        let pass = pwdField.text!.trim()
        if user.length == 0 {
            textMBProgress(text: "用户名不能为空")
            return
        }
        if pass.length == 0 {
            textMBProgress(text: "密码不能为空")
            return
        }
        if self.companyLabel.text == "" {
            textMBProgress(text: "请选择公司")
            return
        }
        let _ = Actor.doStartAuth(withUsername: user).startUserAction().then {
            (res: ACAuthStartRes!) -> () in
            if res.authMode.toNSEnum() == .OTP {
                let promise = Actor.doValidatePassword(pass, withTransaction: res.transactionHash)
                    .startUserAction(["EMAIL_CODE_INVALID", "PHONE_CODE_INVALID", "EMAIL_CODE_EXPIRED", "PHONE_CODE_EXPIRED" ,"PASSWORD_INVALID" , "PASSWORD_EXPIRED"])
                //PASSWORD_INVALID 无效  PASSWORD_EXPIRED 过期
                let _ = promise.then { (r: ACAuthCodeRes!) -> () in
                    if r.needToSignup
                    {
                        self.textMBProgress(text: "账号不存在")
                    }
                    else
                    {
                        let _ = Actor.doCompleteAuth(r.result).startUserAction().then{ (r: JavaLangBoolean!) -> () in
                            if self.checkRemind.isSelected {
                                userDefault.set(["user":user,"pass":pass], forKey: "isRemind")
//                                userDefault.synchronize()
                            }
                            else{
                                userDefault.removeObject(forKey: "isRemind")
//                                userDefault.synchronize()
                            }
                            userDefault.set(user, forKey: "zh")
                            userDefault.synchronize()
                            self.pwdField.resignFirstResponder()
                            self.onAuthenticated()
                        }
                    }
                }
                let _ = promise.failure { (e: JavaLangException!) -> () in
                    if let rpc = e as? ACRpcException {
                        if rpc.tag == "PASSWORD_INVALID" || rpc.tag == "PASSWORD_EXPIRED"
                        {
                            self.textMBProgress(text: "密码错误")
                        }
                        else if rpc.tag == "EMAIL_CODE_EXPIRED" || rpc.tag == "PHONE_CODE_EXPIRED"
                        {
                            AAExecutions.errorWithTag(rpc.tag, rep: nil, cancel: { () -> () in
                                self.navigateBack()
                            })
                        }
                    }
                }
            } else {
                self.alertUser(AALocalized("AuthUnsupported").replace("{app_name}", dest: ActorSDK.sharedActor().appName))
            }
        }
        
        
        
    }
    func chooseCompany(){
        
    }
    open func textMBProgress(text:String) -> () {
        let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
        hud.mode = MBProgressHUDMode.text
        hud.label.text = text
        hud.margin = 10
        hud.offset.y = 50
        hud.removeFromSuperViewOnHide = true
        hud.hide(animated: true, afterDelay: 1)
    }
    func checkAutoLogin(button:UIButton){
        button.isSelected = !button.isSelected;
        button.setImage(button.isSelected ? UIImage.bundled("记住"):UIImage.bundled("不记住"), for: .normal)
    }
    override func viewDidLayoutSubviews() {
        let userImgView = UIImageView(image:UIImage.bundled("用户名.png"))
        userImgView.frame = CGRect(x:appStyle.kScaleW(w: 90),y:appStyle.kScaleH(h: 540),width:appStyle.kScaleW(w: 30),height:appStyle.kScaleH(h: 30))
        view.addSubview(userImgView)
        userField.frame = CGRect(x:appStyle.kScaleW(w: 145),y:appStyle.kScaleH(h: 500),width:appStyle.kScaleW(w: 515),height:appStyle.kScaleH(h: 108))
        
        let pwdImgView = UIImageView(image:UIImage.bundled("密码.png"))
        pwdImgView.frame = CGRect(x:appStyle.kScaleW(w: 90),y:appStyle.kScaleH(h: 648),width:appStyle.kScaleW(w: 30),height:appStyle.kScaleH(h: 30))
        view.addSubview(pwdImgView)
        pwdField.frame = CGRect(x:appStyle.kScaleW(w: 145),y:appStyle.kScaleH(h: 608),width:appStyle.kScaleW(w: 515),height:appStyle.kScaleH(h: 108))
        
        let companyImgview = UIImageView(image:UIImage.bundled("选择公司"))
        companyImgview.frame = CGRect(x:appStyle.kScaleW(w: 90),y:appStyle.kScaleH(h: 756),width:appStyle.kScaleW(w: 30),height:appStyle.kScaleH(h: 30))
        view.addSubview(companyImgview)
        companyLabel.frame = CGRect(x:appStyle.kScaleW(w: 145),y:appStyle.kScaleH(h: 716),width:appStyle.kScaleW(w: 465),height:appStyle.kScaleH(h: 108))
        companyBtn.frame = CGRect(x:appStyle.kScaleW(w: 610),y:appStyle.kScaleH(h: 756),width:appStyle.kScaleW(w: 30),height:appStyle.kScaleH(h: 30))
        
        checkRemind.frame = CGRect(x:appStyle.kScaleW(w: 90),y:appStyle.kScaleH(h: 864),width:appStyle.kScaleW(w: 280),height:appStyle.kScaleH(h: 30))
        
        createLine(h: 608)
        createLine(h: 716)
        createLine(h: 824)
        
        backgroundImgView.frame = view.frame;
        loginBtn.mas_makeConstraints { (make:MASConstraintMaker!) in
            make.bottom.mas_equalTo()(view.mas_bottom)?.setOffset(appStyle.kScaleH(h: -255))
            make.left.mas_equalTo()(appStyle.kScaleW(w: 98))
            make.width.mas_equalTo()(appStyle.kScaleW(w: 553))
            make.height.mas_equalTo()(appStyle.kScaleH(h: 105))
        }
        
    }
    func createLine(h:CGFloat) {
        let line = UIView(frame:CGRect(x:appStyle.kScaleW(w: 90),y:appStyle.kScaleH(h: h),width:appStyle.kScaleW(w: 570),height:1))
        line.backgroundColor = UIColor.white
        view.addSubview(line)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    



}
