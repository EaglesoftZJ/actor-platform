//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import UIKit

open class AASettingsMediaViewController: AAContentTableController {
    
    fileprivate var sessionsCell: AAManagedArrayRows<ARApiAuthSession, AACommonCell>?
    
    public init() {
        super.init(style: AAContentTableStyle.settingsGrouped)
        
        navigationItem.title = AALocalized("MediaTitle")
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override func tableDidLoad() {
        
        _ = section { (s) -> () in
    
            s.headerText = AALocalized("MediaPhotoDownloadHeader")
            
            _ = s.common { (r) -> () in
                r.style = .switch
                r.content = AALocalized("SettingsPrivateChats")
                
                r.switchOn = ActorSDK.sharedActor().isPhotoAutoDownloadPrivate
                
                r.switchAction = { (v) -> () in
                    ActorSDK.sharedActor().isPhotoAutoDownloadPrivate = v
                }
            }
            
            _ = s.common { (r) -> () in
                r.style = .switch
                r.content = AALocalized("SettingsGroupChats")
                
                r.switchOn = ActorSDK.sharedActor().isPhotoAutoDownloadGroup
                
                r.switchAction = { (v) -> () in
                    ActorSDK.sharedActor().isPhotoAutoDownloadGroup = v
                }
            }
        }
        
        _ = section { (s) -> () in
            
            s.headerText = AALocalized("MediaAudioDownloadHeader")
            
            _ = s.common { (r) -> () in
                r.style = .switch
                r.content = AALocalized("SettingsPrivateChats")
                
                r.switchOn = ActorSDK.sharedActor().isAudioAutoDownloadPrivate
                
                r.switchAction = { (v) -> () in
                    ActorSDK.sharedActor().isAudioAutoDownloadPrivate = v
                }
            }
            
            _ = s.common { (r) -> () in
                r.style = .switch
                r.content = AALocalized("SettingsGroupChats")
                
                r.switchOn = ActorSDK.sharedActor().isAudioAutoDownloadGroup
                
                r.switchAction = { (v) -> () in
                    ActorSDK.sharedActor().isAudioAutoDownloadGroup = v
                }
            }
        }
        
        _ = section { (s) -> () in
            
            s.headerText = AALocalized("MediaOtherHeader")
            
           _ = s.common { (r) -> () in
                r.style = .switch
                r.content = AALocalized("MediaAutoplayGif")
                
                r.switchOn = ActorSDK.sharedActor().isGIFAutoplayEnabled
                
                r.switchAction = { (v) -> () in
                    ActorSDK.sharedActor().isGIFAutoplayEnabled = v
                }
            }
        }
    }
}
