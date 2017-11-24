//
//  BMJGTableViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/20.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//  部门结构
//  "id": "bm002", "mc": "液体化工/船配产业部", "fid": "-1 ", "wzh": 14, "dwid": "dw001", "szk": "ZSGM"
//  wzh:根据这个排序 fid -1为第一层 为bmxxx时为bmid的子节点

import UIKit

class BMJGTableViewController: UITableViewController {
    var dwid:String = ""
    var szk:String = ""//所在库
    var mc:String = ""
    var dict = Dictionary<String, Any>()
    var bm_arr = Array<Dictionary<String, Any>>()
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = mc
        self.tableView.tableFooterView = UIView()
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        let arr:Array = dict["bm_data"] as! [Dictionary<String, Any>]
        var wzh_arr = Array<Dictionary<String, Any>>()
        for bmInfo in arr {
            if bmInfo["dwid"] as! String == dwid && bmInfo["szk"] as! String == szk{
                if bmInfo["fid"] as! String == "-1   "{
                    wzh_arr.append(bmInfo)
                }
            }
        }
        bm_arr = wzh_arr.sorted(by: { (s1, s2) -> Bool in
            return (s1["wzh"] as! Int) < (s2["wzh"] as! Int) ? true:false
        })
        self.tableView.reloadData()
    }

    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return bm_arr.count
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        cell.textLabel?.text = (bm_arr[indexPath.row]["mc"] as! String)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        let bmjg:Dictionary = bm_arr[indexPath.row]
        let ygjg:YGJGTableViewController = YGJGTableViewController()
        ygjg.dwid = dwid
        ygjg.mc = bmjg["mc"] as! String
        ygjg.bmid = bmjg["id"] as! String
        ygjg.szk = szk
        ygjg.dict = dict
        self.navigateDetail(ygjg)
    }
}
