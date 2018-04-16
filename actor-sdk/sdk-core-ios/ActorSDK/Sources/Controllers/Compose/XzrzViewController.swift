//
//  XzrzViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/4/13.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class XzrzViewController: UITableViewController {

    var list:[ACMessageXzrz] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "下载日志"
        view.backgroundColor = .white
        self.tableView.tableFooterView = UIView()
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return list.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as UITableViewCell
        let msgXzrz:ACMessageXzrz = list[indexPath.row]
        cell.textLabel?.text = msgXzrz.getUserName() + "  " + msgXzrz.getTime()
        return cell
    }

}
