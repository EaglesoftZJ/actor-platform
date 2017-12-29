//
//  SearchResultController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/12/28.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
protocol AtSearchMemberDelegate:NSObjectProtocol
{
    func AtMemberId(memberId:String)
}
class SearchResultController: UIViewController,UITableViewDelegate,UITableViewDataSource {
    //#delegate datasource
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return searchArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: MemberSearchCell? = nil
        cell = table.dequeueReusableCell(withIdentifier: "cell") as? MemberSearchCell
        if cell == nil {
            cell = MemberSearchCell(style:.subtitle, reuseIdentifier:"cell")
        }
        let memberInfo = searchArray[indexPath.row]
        cell?.bind(memberInfo)
        return cell!
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if delegate != nil {
            let memberInfo = searchArray[indexPath.row]
            let str = memberInfo.mentionString
            let index = str?.index((str?.startIndex)!, offsetBy: 1)
            let res = str?.substring(from: index!)
            delegate?.AtMemberId(memberId: res!)
            self.dismiss(animated: true, completion: nil)
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }

    var searchArray = [ACMentionFilterResult]()
    var table = UITableView()
    weak var delegate:AtSearchMemberDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.automaticallyAdjustsScrollViewInsets = false
        table.frame = CGRect(x:0,y:-44,width:view.frame.width,height:view.frame.height+44)
        table.delegate = self
        table.dataSource = self
        table.tableFooterView = UIView()
        view.addSubview(table)
        table.register(MemberSearchCell.self, forCellReuseIdentifier: "cell")
        
    }
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    

}
