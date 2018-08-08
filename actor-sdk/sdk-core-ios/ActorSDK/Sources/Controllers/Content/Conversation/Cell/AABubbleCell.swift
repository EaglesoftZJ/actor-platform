//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import Foundation
import UIKit

/**
    Bubble types
*/
public enum BubbleType {
    // Outcome text bubble
    case textOut
    // Income text bubble
    case textIn
    // Outcome media bubble
    case mediaOut
    // Income media bubble
    case mediaIn
    // Service bubble
    case service
    // Sticker bubble
    case sticker
}

/**
    Root class for bubble layouter. Used for preprocessing bubble layout in background.
*/
public protocol AABubbleLayouter  {
    
    func isSuitable(_ message: ACMessage) -> Bool
    
    func buildLayout(_ peer: ACPeer, message: ACMessage) -> AACellLayout
    
    func cellClass() -> AnyClass
}

extension AABubbleLayouter {
    func cellReuseId() -> String {
        return "cell_\(cellClass())"
    }
}

/**
    Root class for bubble cells
*/
open class AABubbleCell: UICollectionViewCell {
    
    open static let bubbleContentTop: CGFloat = 6
    open static let bubbleContentBottom: CGFloat = 6
    open static let bubbleTop: CGFloat = 3
    open static let bubbleTopCompact: CGFloat = 1
    open static let bubbleBottom: CGFloat = 3
    open static let bubbleBottomCompact: CGFloat = 1
    open static let avatarPadding: CGFloat = 39
    open static let dateSize: CGFloat = 30
    open static let newMessageSize: CGFloat = 30
    
    //
    // Cached text bubble images
    //
    
    fileprivate static var cachedOutTextBg = UIImage.tinted("BubbleOutgoingFull", color: ActorSDK.sharedActor().style.chatTextBubbleOutColor)
    
    fileprivate static var cachedOutTextBgShadow = ActorSDK.sharedActor().style.bubbleShadowEnabled ? UIImage.tinted("BubbleOutgoingFull", color: ActorSDK.sharedActor().style.chatTextBubbleShadowColor) : UIImage()
    
    fileprivate static var cachedOutTextBgBorder = UIImage.tinted("BubbleOutgoingFullBorder", color: ActorSDK.sharedActor().style.chatTextBubbleOutBorderColor)
    fileprivate static var cachedOutTextCompactBg = UIImage.tinted("BubbleOutgoingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleOutColor)
    fileprivate static var cachedOutTextCompactBgShadow = ActorSDK.sharedActor().style.bubbleShadowEnabled ? UIImage.tinted("BubbleOutgoingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleShadowColor) : UIImage()
    fileprivate static var cachedOutTextCompactSelectedBg = UIImage.tinted("BubbleOutgoingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleOutSelectedColor)
    fileprivate static var cachedOutTextCompactBgBorder = UIImage.tinted("BubbleOutgoingPartialBorder", color: ActorSDK.sharedActor().style.chatTextBubbleOutBorderColor)

    fileprivate static var cachedInTextBg = UIImage.tinted("BubbleIncomingFull", color: ActorSDK.sharedActor().style.chatTextBubbleInColor)
    fileprivate static var cachedInTextBgShadow = ActorSDK.sharedActor().style.bubbleShadowEnabled ? UIImage.tinted("BubbleIncomingFull", color: ActorSDK.sharedActor().style.chatTextBubbleShadowColor) : UIImage()
    fileprivate static var cachedInTextBgBorder = UIImage.tinted("BubbleIncomingFullBorder", color: ActorSDK.sharedActor().style.chatTextBubbleInBorderColor)
    fileprivate static var cachedInTextCompactBg = UIImage.tinted("BubbleIncomingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleInColor)
    fileprivate static var cachedInTextCompactBgShadow = ActorSDK.sharedActor().style.bubbleShadowEnabled ?UIImage.tinted("BubbleIncomingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleShadowColor) : UIImage()
    fileprivate static var cachedInTextCompactSelectedBg = UIImage.tinted("BubbleIncomingPartial", color: ActorSDK.sharedActor().style.chatTextBubbleInSelectedColor)
    fileprivate static var cachedInTextCompactBgBorder = UIImage.tinted("BubbleIncomingPartialBorder", color: ActorSDK.sharedActor().style.chatTextBubbleInBorderColor)
    
    //
    // Cached media bubble images
    //
    
    fileprivate static let cachedMediaBg = UIImage.tinted("BubbleOutgoingPartial", color: ActorSDK.sharedActor().style.chatMediaBubbleColor)
    fileprivate static var cachedMediaBgBorder = UIImage.tinted("BubbleOutgoingPartialBorder", color: ActorSDK.sharedActor().style.chatMediaBubbleBorderColor)

    //
    // Cached Service bubble images
    //
    
    fileprivate static var cachedServiceBg:UIImage = Imaging.roundedImage(ActorSDK.sharedActor().style.chatServiceBubbleColor, size: CGSize(width: 18, height: 18), radius: 9)

    //
    // Cached Date bubble images
    //
    
//    private static var dateBgImage = Imaging.roundedImage(ActorSDK.sharedActor().style.chatDateBubbleColor, size: CGSizeMake(18, 18), radius: 9)
    
    fileprivate static var dateBgImage = ActorSDK.sharedActor().style.statusBackgroundImage
    
    // MARK: -
    // MARK: Public vars
    
    // Views
    open let avatarView = AAAvatarView()
    open var avatarAdded: Bool = false
    
    open let bubble = UIImageView()
    open let bubbleShadow = UIImageView()
    open let bubbleBorder = UIImageView()
    
    fileprivate let dateText = UILabel()
    fileprivate let dateBg = UIImageView()
    
    fileprivate let newMessage = UILabel()
    
    // Layout
    open var contentInsets : UIEdgeInsets = UIEdgeInsets()
    open var bubbleInsets : UIEdgeInsets = UIEdgeInsets()
    open var fullContentInsets : UIEdgeInsets {
        get {
            return UIEdgeInsets(
                top: contentInsets.top + bubbleInsets.top + (isShowDate ? AABubbleCell.dateSize : 0) + (isShowNewMessages ? AABubbleCell.newMessageSize : 0),
                left: contentInsets.left + bubbleInsets.left + (isGroup && !isOut ? AABubbleCell.avatarPadding : 0),
                bottom: contentInsets.bottom + bubbleInsets.bottom,
                right: contentInsets.right + bubbleInsets.right)
        }
    }
    open var needLayout: Bool = true
    
    open let groupContentInsetY = 20.0
    open let groupContentInsetX = 40.0
    open var bubbleVerticalSpacing: CGFloat = 6.0
    open let bubblePadding: CGFloat = 6;
    open let bubbleMediaPadding: CGFloat = 10;
    
    // Binded data
    open var peer: ACPeer!
    open weak var controller: AAConversationContentController!
    open var isGroup: Bool = false
    open var isFullSize: Bool!
    open var bindedSetting: AACellSetting?
    
    open var bindedMessage: ACMessage? = nil
    open var bubbleType:BubbleType? = nil
    open var isOut: Bool = false
    open var isShowDate: Bool = false
    open var isShowNewMessages: Bool = false
    
    var appStyle: ActorStyle {
        get {
            return ActorSDK.sharedActor().style
        }
    }
    
    // MARK: -
    // MARK: Constructors

    public init(frame: CGRect, isFullSize: Bool) {
        super.init(frame: frame)
        
        self.isFullSize = isFullSize
  
        dateBg.image = AABubbleCell.dateBgImage
        dateText.font = UIFont.mediumSystemFontOfSize(12)
        dateText.textColor = appStyle.chatDateTextColor
        dateText.contentMode = UIViewContentMode.center
        dateText.textAlignment = NSTextAlignment.center
        
        newMessage.font = UIFont.mediumSystemFontOfSize(14)
        newMessage.textColor = appStyle.chatUnreadTextColor
        newMessage.contentMode = UIViewContentMode.center
        newMessage.textAlignment = NSTextAlignment.center
        newMessage.backgroundColor = appStyle.chatUnreadBgColor
        newMessage.text = AALocalized("ChatNewMessages")
        
        //"New Messages"
        
        contentView.transform = CGAffineTransform(a: 1, b: 0, c: 0, d: -1, tx: 0, ty: 0)
        
        if appStyle.bubbleShadowEnabled {
            contentView.addSubview(bubbleShadow)
        }
        contentView.addSubview(bubble)
        contentView.addSubview(bubbleBorder)
        contentView.addSubview(newMessage)
        contentView.addSubview(dateBg)
        contentView.addSubview(dateText)
        
        avatarView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(AABubbleCell.avatarDidTap)))
        
        avatarView.isUserInteractionEnabled = true
        
        backgroundColor = UIColor.clear
        
        // Speed up animations
        self.layer.speed = 1.5
        
        //self.layer.shouldRasterize = true
        //self.layer.rasterizationScale = UIScreen.mainScreen().scale
        //self.layer.drawsAsynchronously = true
        //self.contentView.layer.drawsAsynchronously = true
    }
    open func longTap(tap:UILongPressGestureRecognizer)
    {
        if tap.state == .began
        {
            self.becomeFirstResponder()
            let menuController = UIMenuController.shared
            let sendMenu = UIMenuItem(title:"转发",action:#selector(send))
            if self.bubbleType == .textIn || self.bubbleType == .textOut {
                let copyMenu = UIMenuItem(title:"复制",action:#selector(copyText))
                menuController.menuItems = [copyMenu,sendMenu]
            }
            else {
                menuController.menuItems = [sendMenu]
            }
            if self.isOut {
                menuController.menuItems?.append(UIMenuItem(title:"撤回",action:#selector(revoke)))
            }
            menuController.setTargetRect(bubble.frame, in: bubble)
            menuController.setMenuVisible(true, animated: true)
        }
    }

    override open var canBecomeFirstResponder : Bool {
        return true
    }
    override open func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        if [#selector(copyText), #selector(send), #selector(revoke)].contains(action) {
            return true
        }
        return false
    }
    func copyText(){
        if self.bubbleType == .textIn || self.bubbleType == .textOut//文字
        {
            UIPasteboard.general.string = (bindedMessage!.content as! ACTextContent).text
        }
    }
    
    func revoke() {
        let currentTime = Date().timeIntervalSince1970
        let sendTime = TimeInterval((bindedMessage?.date)!/1000)
        let reduceTime:TimeInterval = currentTime - sendTime
        if reduceTime < 300 {
            let name = Actor.getUserWithUid((bindedMessage?.senderId)!).getNameModel().get()
            let ridDesc:String = "\(bindedMessage?.rid ?? 0)"
            let dic = ["data": ["rid": ridDesc, "text": (name! + "撤回了一条消息")], "dataType": "revert"] as [String : Any]
            if !JSONSerialization.isValidJSONObject(dic) {
                print("无法解析出JSONString")
            } else {
                let data:Data = try! JSONSerialization.data(withJSONObject: dic, options: []) as Data
                let str = String(data: data, encoding: String.Encoding.utf8)
                Actor.sendCustomStringMessagePeer(peer, withContent: str!)
            }
            
        } else {
            getSuperController().alertUser("超过5分钟无法撤回")
        }
    }
    
    func send(){
        //self.bindedMessage?.content
        let sendController = SendController()
        if self.bindedMessage != nil
        {
            sendController.message = self.bindedMessage
            getSuperController().navigateNext(sendController)
        }
    }
    func getSuperController () -> (UIViewController){
        //1.通过响应者链关系，取得此视图的下一个响应者
        var next:UIResponder?
        next = self.next!
        repeat {
            //2.判断响应者对象是否是视图控制器类型
            if ((next as?UIViewController) != nil) {
                return (next as! UIViewController)
            }else {
                next = next?.next
            }
        } while next != nil
        return UIViewController()
    }
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setConfig(_ peer: ACPeer, controller: AAConversationContentController) {
        self.peer = peer
        self.controller = controller
        if (peer.isGroup && !isFullSize) {
            self.isGroup = true
        }
    }
        
    func delete() {
        let rids = IOSLongArray(length: 1)
        rids?.replaceLong(at: 0, withLong: bindedMessage!.rid)
        Actor.deleteMessages(with: self.peer, withRids: rids)
    }
    
    func avatarDidTap() {
        if bindedMessage != nil {
            controller.onBubbleAvatarTap(self.avatarView, uid: bindedMessage!.senderId)
        }
    }
    
    open func performBind(_ message: ACMessage, receiveDate: jlong, readDate: jlong, setting: AACellSetting, isShowNewMessages: Bool, layout: AACellLayout) {
        
        var id = 0
        let content = message.content as? ACJsonContent
        if (content != nil) && content?.getRawJson() != nil {
            let str = content?.getRawJson().replacingOccurrences(of: "", with: "\"")
        
            if let data = str?.data(using: String.Encoding.utf8) {
                do {
                    let dic = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? NSDictionary
                    let textid = (dic!["data"] as! NSDictionary)["rid"]
                    if textid != nil {
                        if textid is String {
                            id = Int(textid as! String)!
                        }
                    }
                } catch let error as NSError {
                    print(error)
                }
            }
        }
        if (id != 0)
        {
            let rids = IOSLongArray(length: 1)
            rids?.replaceLong(at: 0, withLong: jlong(id))
            Actor.deleteMessages(with: self.peer, withRids: rids)
            print("超级没中奖", id)
        }
        var reuse = false
        if (bindedMessage != nil && bindedMessage?.rid == message.rid) {
            reuse = true
        }
        isOut = message.senderId == Actor.myUid();
        bindedMessage = message
        self.isShowNewMessages = isShowNewMessages
        if !reuse && !isFullSize {
            if (!isOut && isGroup) {
                let user = Actor.getUserWithUid(message.senderId)
                
                // Small hack for replacing senter name and title
                // with current group title
                if user.isBot() && user.getNameModel().get() == "Bot" {
                    let group = Actor.getGroupWithGid(self.peer.peerId)
                    let avatar: ACAvatar? = group.getAvatarModel().get()
                    let name = group.getNameModel().get()
                    avatarView.bind(name!, id: Int(user.getId()), avatar: avatar)
                } else {
                    let avatar: ACAvatar? = user.getAvatarModel().get()
                    let name = user.getNameModel().get()
                    avatarView.bind(name!, id: Int(user.getId()), avatar: avatar)
                }
                if !avatarAdded {
                    contentView.addSubview(avatarView)
                    avatarAdded = true
                }
            } else {
                if avatarAdded {
                    avatarView.removeFromSuperview()
                    avatarAdded = false
                }
            }
        }
        
        self.isShowDate = setting.showDate
        if (isShowDate) {
            self.dateText.text = layout.anchorDate
        }
        
        self.bindedSetting = setting
        
        bind(message, receiveDate: receiveDate, readDate: readDate, reuse: reuse, cellLayout: layout, setting: setting)
        
        if (!reuse) {
            needLayout = true
            super.setNeedsLayout()
        }
    }
    
    open func bind(_ message: ACMessage, receiveDate: jlong, readDate: jlong, reuse: Bool, cellLayout: AACellLayout, setting: AACellSetting) {
        fatalError("bind(message:) has not been implemented")
    }
    
    open func bindBubbleType(_ type: BubbleType, isCompact: Bool) {
        self.bubbleType = type
        
        // Update Bubble background images
        switch(type) {
            case BubbleType.textIn:
                if (isCompact) {
                    bubble.image = AABubbleCell.cachedInTextCompactBg
                    bubbleBorder.image = AABubbleCell.cachedInTextCompactBgBorder
                    bubbleShadow.image = AABubbleCell.cachedInTextCompactBgShadow
                } else {
                    bubble.image = AABubbleCell.cachedInTextBg
                    bubbleBorder.image = AABubbleCell.cachedInTextBgBorder
                    bubbleShadow.image = AABubbleCell.cachedInTextBgShadow
                }
            break
            case BubbleType.textOut:
                if (isCompact) {
                    bubble.image =  AABubbleCell.cachedOutTextCompactBg
                    bubbleBorder.image =  AABubbleCell.cachedOutTextCompactBgBorder
                    bubbleShadow.image = AABubbleCell.cachedOutTextCompactBgShadow
                } else {
                    bubble.image =  AABubbleCell.cachedOutTextBg
                    bubbleBorder.image =  AABubbleCell.cachedOutTextBgBorder
                    bubbleShadow.image = AABubbleCell.cachedOutTextBgShadow
                }
            break
            case BubbleType.mediaIn:
                bubble.image =  AABubbleCell.cachedMediaBg
                bubbleBorder.image =  AABubbleCell.cachedMediaBgBorder
                bubbleShadow.image = nil
            break
            case BubbleType.mediaOut:
                bubble.image =  AABubbleCell.cachedMediaBg
                bubbleBorder.image =  AABubbleCell.cachedMediaBgBorder
                bubbleShadow.image = nil
            break
            case BubbleType.service:
                bubble.image = AABubbleCell.cachedServiceBg
                bubbleBorder.image = nil
                bubbleShadow.image = nil
            break
            case BubbleType.sticker:
                bubble.image = nil;
                bubbleBorder.image = nil
                bubbleShadow.image = nil
            break
        }
    }
    
    func updateView() {
        let type = self.bubbleType! as BubbleType
        switch (type) {
        case BubbleType.textIn:
            if (!isFullSize!) {
                bubble.image = AABubbleCell.cachedInTextCompactBg
                bubbleBorder.image = AABubbleCell.cachedInTextCompactBgBorder
                bubbleShadow.image = AABubbleCell.cachedInTextCompactBgShadow
            } else {
                bubble.image = AABubbleCell.cachedInTextBg
                bubbleBorder.image = AABubbleCell.cachedInTextBgBorder
                bubbleShadow.image = AABubbleCell.cachedInTextBgShadow
            }
            break
        case BubbleType.textOut:
            if (!isFullSize!) {
                bubble.image =  AABubbleCell.cachedOutTextCompactBg
                bubbleBorder.image =  AABubbleCell.cachedOutTextCompactBgBorder
                bubbleShadow.image = AABubbleCell.cachedOutTextCompactBgShadow
            } else {
                bubble.image =  AABubbleCell.cachedOutTextBg
                bubbleBorder.image =  AABubbleCell.cachedOutTextBgBorder
                bubbleShadow.image = AABubbleCell.cachedOutTextBgShadow
            }
            break
        case BubbleType.mediaIn:
            bubble.image =  AABubbleCell.cachedMediaBg
            bubbleBorder.image =  AABubbleCell.cachedMediaBgBorder
            bubbleShadow.image = nil
            break
        case BubbleType.mediaOut:
            bubble.image =  AABubbleCell.cachedMediaBg
            bubbleBorder.image =  AABubbleCell.cachedMediaBgBorder
            bubbleShadow.image = nil
            break
        case BubbleType.service:
            bubble.image = AABubbleCell.cachedServiceBg
            bubbleBorder.image = nil
            bubbleShadow.image = nil
            break
        case BubbleType.sticker:
            bubble.image = nil;
            bubbleBorder.image = nil
            bubbleShadow.image = nil
            break
        }
    }
    
    // MARK: -
    // MARK: Layout
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        
        UIView.performWithoutAnimation { () -> Void in
            let endPadding: CGFloat = 32
            let startPadding: CGFloat = (!self.isOut && self.isGroup) ? AABubbleCell.avatarPadding : 0
            let cellMaxWidth = self.contentView.frame.size.width - endPadding - startPadding
            self.layoutContent(cellMaxWidth, offsetX: startPadding)
            self.layoutAnchor()
            if (!self.isOut && self.isGroup && !self.isFullSize) {
                self.layoutAvatar()
            }
        }
    }
    
    func layoutAnchor() {
        if (isShowDate) {
            dateText.frame = CGRect(x: 0, y: 0, width: 1000, height: 1000)
            dateText.sizeToFit()
            dateText.frame = CGRect(
                x: (self.contentView.frame.size.width-dateText.frame.width)/2, y: 8, width: dateText.frame.width, height: 18)
            dateBg.frame = CGRect(x: dateText.frame.minX - 8, y: dateText.frame.minY, width: dateText.frame.width + 16, height: 18)
            
            dateText.isHidden = false
            dateBg.isHidden = false
        } else {
            dateText.isHidden = true
            dateBg.isHidden = true
        }
        
        if (isShowNewMessages) {
            var top = CGFloat(0)
            if (isShowDate) {
                top += AABubbleCell.dateSize
            }
            newMessage.isHidden = false
            newMessage.frame = CGRect(x: 0, y: top + CGFloat(2), width: self.contentView.frame.width, height: AABubbleCell.newMessageSize - CGFloat(4))
        } else {
            newMessage.isHidden = true
        }
    }
    
    open func layoutContent(_ maxWidth: CGFloat, offsetX: CGFloat) {
        
    }
    
    func layoutAvatar() {
        let avatarSize = CGFloat(42)
        avatarView.frame = CGRect(x: 5 + (AADevice.isiPad ? 16 : 0), y: self.contentView.frame.size.height - avatarSize - 2 - bubbleInsets.bottom, width: avatarSize, height: avatarSize)
    }
    
    // Need to be called in child cells
    
    open func layoutBubble(_ contentWidth: CGFloat, contentHeight: CGFloat) {
        let fullWidth = contentView.bounds.width
        let bubbleW = contentWidth + contentInsets.left + contentInsets.right
        let bubbleH = contentHeight + contentInsets.top + contentInsets.bottom
        var topOffset = CGFloat(0)
        if (isShowDate) {
            topOffset += AABubbleCell.dateSize
        }
        if (isShowNewMessages) {
            topOffset += AABubbleCell.newMessageSize
        }
        var bubbleFrame : CGRect!
        if (!isFullSize) {
            if (isOut) {
                bubbleFrame = CGRect(
                    x: fullWidth - contentWidth - contentInsets.left - contentInsets.right - bubbleInsets.right,
                    y: bubbleInsets.top + topOffset,
                    width: bubbleW,
                    height: bubbleH)
            } else {
                let padding : CGFloat = isGroup ? AABubbleCell.avatarPadding : 0
                bubbleFrame = CGRect(
                    x: bubbleInsets.left + padding,
                    y: bubbleInsets.top + topOffset,
                    width: bubbleW,
                    height: bubbleH)
            }
        } else {
            bubbleFrame = CGRect(
                x: (fullWidth - contentWidth - contentInsets.left - contentInsets.right)/2,
                y: bubbleInsets.top + topOffset,
                width: bubbleW,
                height: bubbleH)
        }
        bubble.frame = bubbleFrame
        bubbleBorder.frame = bubbleFrame
        bubbleShadow.frame = CGRect(
            x: bubbleFrame.minX + 1,
            y: bubbleFrame.minY + 1,
            width: bubbleW,
            height: bubbleH)
    }
    
    open func layoutBubble(_ frame: CGRect) {
        bubble.frame = frame
        bubbleBorder.frame = frame
        bubbleShadow.frame = frame
    }
    
    open override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes) -> UICollectionViewLayoutAttributes {
        return layoutAttributes
    }
}
