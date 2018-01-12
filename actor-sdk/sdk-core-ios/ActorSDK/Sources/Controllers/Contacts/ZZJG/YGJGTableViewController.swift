//
//  YGJGTableViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/20.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//  员工结构
//  "IGIMID": "1180656211", "id": "0067DABF-7A51-4A8C-B9BC-F160F904CF67", "xm": "虞舒然", "zh": "529698", "bmid": "bm292", "fid": "-1 ", "dwid": "dw019", "bmmc": "船务二部", "dwmc": "中国舟山外轮代理有限公司", "zwid": " ", "zwmc": "", "sjh": " ", "duh": "", "dh": "", "email": "", "wzh": 240, "szk": "ZGGF"

import UIKit

class YGJGTableViewController: UITableViewController {
    
    var dwid:String = ""
    var bmid:String = ""
    var mc:String = ""
    var szk:String = ""//所在库
    var dict = Dictionary<String, Any>()
    var yg_arr = Array<Dictionary<String, Any>>()//用户arr
    var bm_arr = Array<Dictionary<String, Any>>()//部门arr
    override func viewDidLoad() {
        super.viewDidLoad()

        self.tableView.tableFooterView = UIView()
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        getYhAndBm()
        self.tableView.reloadData()
    }

    private func getYhAndBm(){
        self.title = mc
        
        //#用户
        let yhArr:Array = dict["yh_data"] as! [Dictionary<String, Any>]
        var yh_wzh_arr = Array<Dictionary<String, Any>>()
        for ygInfo in yhArr {
            if ygInfo["dwid"] as! String == dwid && ygInfo["bmid"] as! String == bmid && ygInfo["szk"] as! String == szk
            {
                yh_wzh_arr.append(ygInfo)
            }
        }
        yg_arr = yh_wzh_arr.sorted(by: { (s1, s2) -> Bool in
            return (s1["wzh"] as! Int) < (s2["wzh"] as! Int) ? true:false
        })
        
        //#部门
        let bmArr:Array = dict["bm_data"] as! [Dictionary<String, Any>]
        var bm_wzh_arr = Array<Dictionary<String, Any>>()
        for bmInfo in bmArr {
            if bmInfo["dwid"] as! String == dwid && bmInfo["szk"] as! String == szk {
                if bmInfo["fid"] as! String == bmid{
                    bm_wzh_arr.append(bmInfo)
                }
            }
        }
        bm_arr = bm_wzh_arr.sorted(by: { (s1, s2) -> Bool in
            return (s1["wzh"] as! Int) < (s2["wzh"] as! Int) ? true:false
        })
    }
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if section == 0 {
            return yg_arr.count
        }
        return bm_arr.count
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        if indexPath.section == 0 {
            if yg_arr[indexPath.row]["zwmc"] as? String == ""{
                cell.textLabel?.text = yg_arr[indexPath.row]["xm"] as? String
            }
            else{
                cell.textLabel?.text = (yg_arr[indexPath.row]["xm"] as? String)! + "(" + (yg_arr[indexPath.row]["zwmc"] as? String)! + ")"
            }
        }
        else
        {
            cell.textLabel?.text = bm_arr[indexPath.row]["mc"] as? String
        }
        return cell
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if yg_arr.count != 0 && bm_arr.count != 0{return 10}
        return 0
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let headerView = UIView(frame:CGRect(x:0,y:0,width:self.tableView.bounds.size.width,height:10))
        headerView.backgroundColor = UIColor(red:238/255,green:233/255,blue:233/255,alpha:1)
        if yg_arr.count != 0 && bm_arr.count != 0{
            if section == 0{return headerView}
            if section == 1{return headerView}
            return UIView()
        }
        return UIView()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0{
            let uid:String = yg_arr[indexPath.row]["IGIMID"] as! String
            navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(jint(uid)!)))
        }
        else{
            bmid = (bm_arr[indexPath.row]["id"] as? String)!
            mc = (bm_arr[indexPath.row]["mc"] as? String)!
            getYhAndBm()
            self.tableView.reloadData()
        }
    }
    
    
}
