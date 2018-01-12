//
//  ContactsSearchController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/12.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class ContactsSearchController: UIViewController,UITableViewDelegate,UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return searchArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = table.dequeueReusableCell(withIdentifier: "cell") as! ContactsGroupCell
        let contact = searchArray[indexPath.row]
        cell.nameLabel.text = contact.name
        cell.avatarView.bind(contact.name, id: Int(contact.uid), avatar: contact.avatar)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }

    var searchArray = [ACContact]()
    var table = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        self.automaticallyAdjustsScrollViewInsets = false
        table.frame = CGRect(x:0,y:-44,width:view.frame.width,height:view.frame.height+44)
        table.delegate = self
        table.dataSource = self
        table.tableFooterView = UIView()
        view.addSubview(table)
        table.register(ContactsGroupCell.self, forCellReuseIdentifier: "cell")
        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }


}
