//
//  ContactsGroupCell.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/11.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class ContactsGroupCell: UITableViewCell {

    open var avatarView = AAAvatarView()
    open var nameLabel = UILabel()
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(nameLabel)
        contentView.addSubview(avatarView)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    open func bind(_ res: ACGroupVM){
        avatarView.bind(res.getNameModel().get(), id: Int(res.groupId), avatar: res.getAvatarModel().get())
        nameLabel.text = res.getNameModel().get()
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        let width = contentView.frame.width;
        avatarView.frame = CGRect(x: 30, y: 8, width: 40, height: 40);
        nameLabel.frame = CGRect(x: 80, y: 8, width: width - 80 - 14, height: 40);
    }
}
