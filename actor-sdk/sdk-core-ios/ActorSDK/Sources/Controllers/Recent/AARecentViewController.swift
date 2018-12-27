//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import UIKit
import AFNetworking
open class AARecentViewController: AADialogsListContentController, AADialogsListContentControllerDelegate {

    fileprivate var isBinded = true
    
    var filePath = String() //V6传过来的路径
    var fileTitle = String() //v6传过来的标题
    
    public override init() {
        
        super.init()
        
        // Enabling dialogs page tracking
        
        content = ACAllEvents_Main.recent()
        
        // Setting delegate
        
        self.delegate = self
        
        // Setting UITabBarItem
        
        tabBarItem = UITabBarItem(title: "TabMessages", img: "TabIconChats", selImage: "TabIconChatsHighlighted")
        
        // Setting navigation item
        
        navigationItem.title = AALocalized("TabMessages")
        navigationItem.leftBarButtonItem = editButtonItem
        navigationItem.leftBarButtonItem!.title = AALocalized("NavigationEdit")
        navigationItem.backBarButtonItem = UIBarButtonItem(title: AALocalized("DialogsBack"), style: UIBarButtonItemStyle.plain, target: nil, action: nil)
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.compose, target: self, action: #selector(AARecentViewController.compose))
        
        bindCounter()
    }

    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // Implemention of editing
    
    open override func setEditing(_ editing: Bool, animated: Bool) {
        super.setEditing(editing, animated: animated)
        tableView.setEditing(editing, animated: animated)
        
        if (editing) {
            self.navigationItem.leftBarButtonItem!.title = AALocalized("NavigationDone")
            self.navigationItem.leftBarButtonItem!.style = UIBarButtonItemStyle.done
            
            navigationItem.rightBarButtonItem = nil
        } else {
            self.navigationItem.leftBarButtonItem!.title = AALocalized("NavigationEdit")
            self.navigationItem.leftBarButtonItem!.style = UIBarButtonItemStyle.plain
            
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.compose, target: self, action: #selector(AARecentViewController.compose))
        }
        
        if editing == true {
            navigationItem.rightBarButtonItem = nil
        } else {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.compose, target: self, action: #selector(AARecentViewController.compose))
        }
    }
    
    open func compose() {
        if AADevice.isiPad {
            self.presentElegantViewController(AANavigationController(rootViewController: AAComposeController()))
        } else {
            navigateNext(AAComposeController())
        }
    }
    
    // Tracking app state
    
    open override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        bindCounter()
        
        if filePath != ""
        {
            navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .stop,target: self, action:#selector(back))
        }
    }
    
    func bindCounter() {
        if !isBinded {
            isBinded = true
            binder.bind(Actor.getGlobalState().globalCounter, closure: { (value: JavaLangInteger?) -> () in
                if value != nil {
                    if value!.intValue > 0 {
                        self.tabBarItem.badgeValue = "\(value!.intValue)"
                    } else {
                        self.tabBarItem.badgeValue = nil
                    }
                } else {
                    self.tabBarItem.badgeValue = nil
                }
            })
        }
    }
    
    func back() {
        self.navigationController?.popViewController(animated: true)
    }
    
    open override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        isBinded = false
    }
    
    open override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        Actor.onDialogsOpen()
    }
    
    open override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        Actor.onDialogsClosed()
    }
    
    // Handling selections
    
    open func recentsDidTap(_ controller: AADialogsListContentController, dialog: ACDialog) -> Bool {
        //点击选定发送消息
        if filePath != "" {
            let alertController = UIAlertController(title: "确定要发送吗?",message: "",preferredStyle: .alert)
            let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
            let OKAction = UIAlertAction(title: "确定",style: .default,handler: {
                action in
                let fileURL = URL.init(string: self.filePath.removingPercentEncoding!)!
                let fileName = fileURL.lastPathComponent.removingPercentEncoding!
                //AFN下载文件到本地
                //路径
                let descriptor = "/tmp/\(UUID().uuidString)"
                let destPath = CocoaFiles.pathFromDescriptor(descriptor)
                //下载
                let configuration = URLSessionConfiguration.default
                let sessionManager = AFURLSessionManager.init(sessionConfiguration: configuration)
                let request = URLRequest(url: fileURL)
                let task = sessionManager.downloadTask(with: request, progress: nil, destination: { (targetPath, response) -> URL in
                    //本地路径
                    return URL.init(fileURLWithPath: destPath)
                }, completionHandler: { (response, filePath, error) in
                    //完成之后发送
                     Actor.sendDocument(with: dialog.peer, withName: fileName, withMime: "application/octet-stream", withDescriptor: descriptor)
                    self.back()
                })
                task.resume()
            })
            alertController.addAction(cancelAction)
            alertController.addAction(OKAction)
            self.present(alertController, animated: true, completion: nil)
        }
        else {
            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(dialog.peer) {
                self.navigateDetail(customController)
            } else {
                self.navigateDetail(ConversationViewController(peer: dialog.peer))
            }
        }
        return false
    }
    
    open func searchDidTap(_ controller: AADialogsListContentController, entity: ACSearchResult) {
        if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(entity.peer) {
            self.navigateDetail(customController)
        } else {
            self.navigateDetail(ConversationViewController(peer: entity.peer))
        }
    }
}
