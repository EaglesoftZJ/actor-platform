//
//  AtGroupMemberController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/12/28.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

import UIKit
protocol AtMemberDelegate:NSObjectProtocol
{
    func AtMemberId(memberId:String)
}
class AtGroupMemberController: UIViewController,UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate,UISearchResultsUpdating,AtSearchMemberDelegate {
    
    func AtMemberId(memberId: String) {
        if delegate != nil{
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.01) {
                self.delegate?.AtMemberId(memberId: memberId)
                self.navigateBack()
            }
        }
    }
    
    func updateSearchResults(for searchController: UISearchController) {
        let nav_VC = searchController.searchResultsController as! UINavigationController
        let resultVC = nav_VC.topViewController as! SearchResultController
        searchArray.removeAll()
        for memberInfo in groupMembers{
            if memberInfo.mentionString.contains(searchController.searchBar.text!) || memberInfo.originalString.contains(searchController.searchBar.text!)
            {
                searchArray.append(memberInfo)
            }
        }
        resultVC.delegate = self
        resultVC.searchArray = searchArray
        resultVC.table.reloadData()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return groupMembers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: MemberSearchCell? = nil
        cell = table.dequeueReusableCell(withIdentifier: "cell") as? MemberSearchCell
        if cell == nil {
            cell = MemberSearchCell(style:.subtitle, reuseIdentifier:"cell")
        }
        let memberInfo = groupMembers[indexPath.row]
        cell?.bind(memberInfo)
        return cell!
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if delegate != nil
        {
            let memberInfo = groupMembers[indexPath.row]
            let str = memberInfo.mentionString
            let index = str?.index((str?.startIndex)!, offsetBy: 1)
            let res = str?.substring(from: index!)
            delegate?.AtMemberId(memberId: res!)
            self.navigateBack()
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }

    var groupMembers = [ACMentionFilterResult]()
    var searchArray = [ACMentionFilterResult]()
    
    var searchController = UISearchController()
    var table = UITableView()
    weak var delegate:AtMemberDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "选择提醒的人"
        view.backgroundColor = UIColor.white
        
        view.addSubview(table)
        table.register(MemberSearchCell.self, forCellReuseIdentifier: "cell")
        
        searchController = ({
            let searchResultController = UINavigationController(rootViewController: SearchResultController())
            let controller = UISearchController(searchResultsController: searchResultController)
            controller.searchResultsUpdater = self
            controller.searchBar.delegate = self
            controller.hidesNavigationBarDuringPresentation = true
            controller.dimsBackgroundDuringPresentation = true
            controller.searchBar.searchBarStyle = .minimal
            controller.definesPresentationContext = true
            controller.searchBar.sizeToFit()
            self.table.tableHeaderView = controller.searchBar
            return controller
        })()
        
    }
    override func viewDidLayoutSubviews() {
        table.frame = CGRect(x:0,y:0,width:view.frame.width,height:view.frame.height)
        table.delegate = self
        table.dataSource = self
        
    }


}
