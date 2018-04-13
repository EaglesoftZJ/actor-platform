//
//  ContactsGroupController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/10.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit
class Group: NSObject, ACGroupAllGetCallback {
    
    typealias reload = (Array<ACGroupVM>) -> ()
    var ContactsReload:reload?
    
    func responseCallBack(_ groupVMS: JavaUtilList!) {

        let arr = groupVMS.toArray()
        
        var displayList = Array<ACGroupVM>()
        
        if groupVMS.size() != 0 {
            
            for i in 0...Int(groupVMS.size())-1 {
                
                let info = arr?.object(at: UInt(i)) as! ACGroupVM
                displayList.append(info)
                
            }
            
            ContactsReload!(displayList)
        }
    }
}

class ContactsGroupController: UIViewController,UITableViewDelegate,UITableViewDataSource{

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.displayList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell") as! ContactsGroupCell
        let data = self.displayList[indexPath.row]
        cell.bind(data)
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let dialog = self.displayList[indexPath.row]
        let peer:ACPeer = ACPeer.group(with: dialog.getId()) as ACPeer
        if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(peer) {
            self.navigateDetail(customController)
        } else {
            self.navigateDetail(ConversationViewController(peer: peer))
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }

    
    var displayList = Array<ACGroupVM>()
    let table = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "我的群组"
        view.backgroundColor = .white
        
        createList()
        
        let group = Group()
        
        Actor.getGroupAll(withIp: ActorSDK.sharedActor().serviceIP, withUid: jlong(Actor.myUid()), with: group)
        
        group.ContactsReload = { (arr) in
            self.displayList = arr
            dispatchOnUi {
                self.table.reloadData()
            }
        }
        
    }
    
    func createList(){
        table.frame = view.frame
        table.delegate = self
        table.dataSource = self
        table.tableFooterView = UIView()
        view.addSubview(table)
        table.register(ContactsGroupCell.self, forCellReuseIdentifier: "cell")
    }
    

}
