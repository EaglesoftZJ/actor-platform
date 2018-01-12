//
//  ContactsGroupController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/10.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class ContactsGroupController: UIViewController,UITableViewDelegate,UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return displayList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell") as! ContactsGroupCell
        let data = displayList[indexPath.row]
        cell.bind(data)
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let dialog = displayList[indexPath.row]
        if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(dialog.peer) {
            self.navigateDetail(customController)
        } else {
            self.navigateDetail(ConversationViewController(peer: dialog.peer))
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }

    var displayList = Array<ACDialog>()
    let table = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "我的群组"
        view.backgroundColor = .white
        
        let app = ActorSDK.sharedActor()
        
        for i in 0...Int(app.displayList.size())-1
        {
            let data = app.displayList.item(with: jint(i)) as! ACDialog
            if data.peer.isGroup
            {
                displayList.append(data)
            }
        }
        
        createList()
        
        table.reloadData()
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
