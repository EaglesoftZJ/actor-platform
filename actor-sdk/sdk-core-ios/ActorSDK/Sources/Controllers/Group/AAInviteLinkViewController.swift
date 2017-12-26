//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import Foundation

open class AAInviteLinkViewController: AAContentTableController {

    // Data
    
    open var currentUrl: String?
    
    // Rows
    
    open var urlRow: AACommonRow!
    
    public init(gid: Int) {
        super.init(style: AAContentTableStyle.settingsGrouped)
        
        self.gid = gid
        
        self.title = AALocalized("GroupInviteLinkPageTitle")
    }

    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override func tableDidLoad() {
        
        tableView.isHidden = true
        
        _ = section { (s) -> () in
            s.headerText = AALocalized("GroupInviteLinkTitle")
            s.footerText = AALocalized("GroupInviteLinkHint")
            
            self.urlRow = s.common { (r) -> () in
                r.bindAction = { (r) -> () in
                    r.content = self.currentUrl
                }
            }
        }
        
        _ = section { (s) -> () in
            _ = s.action("ActionCopyLink") { (r) -> () in
                r.selectAction = { () -> Bool in
                    UIPasteboard.general.string = self.currentUrl
                    self.alertUser("AlertLinkCopied")
                    return true
                }
            }
            _ = s.action("ActionShareLink") { (r) -> () in
                r.selectAction = { () -> Bool in
                    var sharingItems = [AnyObject]()
                    sharingItems.append(self.currentUrl! as AnyObject)
                    let activityViewController = UIActivityViewController(activityItems: sharingItems, applicationActivities: nil)
                    self.present(activityViewController, animated: true, completion: nil)
                    return true
                }
            }
        }
        
        _ = section { (s) -> () in
            _ = s.danger("ActionRevokeLink") { (r) -> () in
                r.selectAction = { () -> Bool in
                    self.confirmDestructive(AALocalized("GroupInviteLinkRevokeMessage"), action: AALocalized("GroupInviteLinkRevokeAction"), yes: { () -> () in
                        self.reloadLink()
                    })
                    return true
                }
            }
        }
        
        executeSafe(Actor.requestInviteLinkCommand(withGid: jint(gid))) { (val) -> Void in
            self.currentUrl = val as? String
            self.urlRow.reload()
            self.tableView.isHidden = false
        }
    }
    
    open func reloadLink() {
        executeSafe(Actor.requestRevokeLinkCommand(withGid: jint(gid))) { (val) -> Void in
            self.currentUrl = val as? String
            self.urlRow.reload()
            self.tableView.isHidden = false
        }
    }
}
