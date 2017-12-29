//
//  MemberSearchCell.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/12/28.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit

class MemberSearchCell: UITableViewCell {
    
    open var nameLabel = UILabel()
    open var idLabel = UILabel()
    open var avatarView = AAAvatarView()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        contentView.addSubview(avatarView)
        contentView.addSubview(idLabel)
        contentView.addSubview(nameLabel)
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open func bind(_ res: ACMentionFilterResult){
        let uid = res.uid
        let user:ACUserVM = Actor.getUserWithUid(uid)
        avatarView.bind(user.getNameModel().get()!, id: Int(user.getId()), avatar: user.getAvatarModel().get())
        nameLabel.text = res.originalString
        idLabel.text = res.mentionString
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        let userAvatarViewFrameSize: CGFloat = CGFloat(44)
        avatarView.frame = CGRect(x:14.0,y:(contentView.bounds.size.height - userAvatarViewFrameSize) / 2.0,width:userAvatarViewFrameSize,height:userAvatarViewFrameSize)
        
        nameLabel.frame = CGRect(x:65.0,y:5,width:contentView.bounds.size.width-65.0,height:contentView.bounds.size.height/2.0-5)
        nameLabel.font = UIFont.systemFont(ofSize: 18)
        
        idLabel.frame = CGRect(x:65.0,y:contentView.bounds.size.height/2.0,width:contentView.bounds.size.width-65.0,height:contentView.bounds.size.height/2.0)
        idLabel.font = UIFont.systemFont(ofSize: 14)
        idLabel.textColor = UIColor.lightGray
    }

}
