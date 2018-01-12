//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import UIKit
import MessageUI
import Social
import AddressBookUI
import ContactsUI
fileprivate func < <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case let (l?, r?):
    return l < r
  case (nil, _?):
    return true
  default:
    return false
  }
}

fileprivate func > <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case let (l?, r?):
    return l > r
  default:
    return rhs < lhs
  }
}


open class AAContactsViewController: AAContactsListContentController, AAContactsListContentControllerDelegate, UIAlertViewDelegate, MFMessageComposeViewControllerDelegate, MFMailComposeViewControllerDelegate {
    
    var inviteText: String {
        get {
            return AALocalized("InviteText").replace("{link}", dest: ActorSDK.sharedActor().inviteUrl).replace("{appname}", dest: ActorSDK.sharedActor().appName)
        }
    }
    
    public override init() {
        super.init()
        
        content = ACAllEvents_Main.contacts()

        tabBarItem = UITabBarItem(title: "TabPeople", img: "TabIconContacts", selImage: "TabIconContactsHighlighted")
        
        navigationItem.title = AALocalized("TabPeople")
        navigationItem.backBarButtonItem = UIBarButtonItem(title: AALocalized("ContactsBack"), style: UIBarButtonItemStyle.plain, target: nil, action: nil)
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.add, target: self, action: #selector(AAContactsViewController.findContact))
        
        delegate = self
    }

    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    //点击事件
    open func contactDidTap(_ controller: AAContactsListContentController, contact: ACContact) -> Bool {
        
        if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(contact.uid)) {
            navigateDetail(customController)
        } else {
            navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(contact.uid)))
        }
        
        return true
    }
    
    open func willAddContacts(_ controller: AAContactsListContentController, section: AAManagedSection) {
        
        _ = section.custom { (r: AACustomRow<AAContactActionCell>) -> () in
            
            r.height = 56
            
            r.closure = { (cell: AAContactActionCell)->() in
                cell.bind("ic_create_channel", actionTitle: "组织架构")
            }
            
            r.selectAction = { () -> Bool in
                self.navigateDetail(ZZJGTableViewController())
                return AADevice.isiPad
            }
        }
        
        _ = section.custom { (r: AACustomRow<AAContactActionCell>) -> () in
            
            r.height = 56
            
            r.closure = { (cell: AAContactActionCell)->() in
                cell.bind("ic_chats_outline", actionTitle: "群组")
            }
            
            r.selectAction = { () -> Bool in
                let contactsGroupController = ContactsGroupController()
                self.navigateNext(contactsGroupController)
                return AADevice.isiPad
            }
        }
        
       _ = section.custom { (r: AACustomRow<AAContactActionCell>) -> () in
            
            r.height = 56
            r.closure = { (cell: AAContactActionCell)->() in
                cell.bind("ic_add_user", actionTitle: "创建群组")
            }
            
            r.selectAction = { () -> Bool in
                self.navigateNext(AAGroupCreateViewController(isChannel: false), removeCurrent: false)
                return true
            }
        }
    }
    // Searching for contact
    
    open func findContact() {
        
        startEditField { (c) -> () in
            c.title = "FindTitle"
            c.actionTitle = "NavigationFind"
            
            c.hint = "FindHint"
            c.fieldHint = "FindFieldHint"
            
            c.fieldAutocapitalizationType = .none
            c.fieldAutocorrectionType = .no
            c.fieldReturnKey = .search
            
            c.didDoneTap = { (t, c) -> () in
                
                if t.length == 0 {
                    return
                }
                
                self.executeSafeOnlySuccess(Actor.findUsersCommand(withQuery: t), successBlock: { (val) -> Void in
                    var user: ACUserVM? = nil
                    if let users = val as? IOSObjectArray {
                        if Int(users.length()) > 0 {
                            if let tempUser = users.object(at: 0) as? ACUserVM {
                                user = tempUser
                            }
                        }
                    }
                    
                    if user != nil {
                        c.execute(Actor.addContactCommand(withUid: user!.getId())!, successBlock: { (val) -> Void in
                            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(user!.getId())) {
                                self.navigateDetail(customController)
                            } else {
                                self.navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(user!.getId())))
                            }
                            c.dismissController()
                        }, failureBlock: { (val) -> Void in
                            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(user!.getId())) {
                                self.navigateDetail(customController)
                            } else {
                                self.navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(user!.getId())))
                            }
                            c.dismissController()
                        })
                    } else {
                        c.alertUser("FindNotFound")
                    }
                })
            }
        }
    }
    
    open func alertView(_ alertView: UIAlertView, clickedButtonAt buttonIndex: Int) {
        if buttonIndex == 1 {
            let textField = alertView.textField(at: 0)!
            if textField.text?.length > 0 {
                execute(Actor.findUsersCommand(withQuery: textField.text), successBlock: { (val) -> () in
                    var user: ACUserVM?
                    user = val as? ACUserVM
                    if user == nil {
                        if let users = val as? IOSObjectArray {
                            if Int(users.length()) > 0 {
                                if let tempUser = users.object(at: 0) as? ACUserVM {
                                    user = tempUser
                                }
                            }
                        }
                    }
                    if user != nil {
                        self.execute(Actor.addContactCommand(withUid: user!.getId())!, successBlock: { (val) -> () in
                                if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(user!.getId())) {
                                    self.navigateDetail(customController)
                                } else {
                                    self.navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(user!.getId())))
                                }
                            }, failureBlock: { (val) -> () in
                                self.showSmsInvitation([textField.text!])
                        })
                    } else {
                        self.showSmsInvitation([textField.text!])
                    }
                    }, failureBlock: { (val) -> () in
                        self.showSmsInvitation([textField.text!])
                })
            }
        }
    }
    
    // Email Invitation
    
    open func showEmailInvitation(_ recipients: [String]?) {
        if MFMailComposeViewController.canSendMail() {
            
            let messageComposeController = MFMailComposeViewController()
            messageComposeController.mailComposeDelegate = self
            
            // Replace
            messageComposeController.setSubject(inviteText)
            
            // TODO: Replace with bigger text
            messageComposeController.setMessageBody(inviteText, isHTML: false)
            messageComposeController.setToRecipients(recipients)
            present(messageComposeController, animated: true, completion: nil)
        }
    }
    
    open func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }
    
    // SMS Invitation
    open func showSmsInvitation() {
        self.showSmsInvitation(nil)
    }
    
    open func showSmsInvitation(_ recipients: [String]?) {
        if MFMessageComposeViewController.canSendText() {
            let messageComposeController = MFMessageComposeViewController()
            messageComposeController.messageComposeDelegate = self
            messageComposeController.body = inviteText
            messageComposeController.recipients = recipients
            present(messageComposeController, animated: true, completion: nil)
        }
    }
    
    @objc open func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        controller.dismiss(animated: true, completion: nil)
    }
}
