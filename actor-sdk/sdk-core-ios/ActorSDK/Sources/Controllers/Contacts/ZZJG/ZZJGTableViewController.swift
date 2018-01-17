//
//  ZZJGTableViewController.swift
//  ActorSDK
//
//  Created by dingjinming on 2017/11/16.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//  组织结构

import UIKit
class ZZJGTableViewController: UITableViewController,CommonServiceDelegate {
//    func serviceStart(_ svc: CommonService!) {
//        hud.show(view:self.tableView)
//    }
//    func serviceEnd(_ svc: CommonService!) {
//        hud.hide()
//    }
    func serviceSuccess(_ svc: CommonService!, object obj: Any!) {
        if svc == companyService {
            guard (obj as? NSDictionary) != nil else{return}
            result_dict = obj as! NSDictionary
            UserDefaults.standard.set(result_dict, forKey: "ZZJG")
            getResultDict()
        }
    }
    
    func serviceFail(_ svc: CommonService!, info: String!) {
        hud.text(text: "加载失败", view: self.tableView)
    }
    
    
    var dw_arr = Array<Any>()
    var result_dict = NSDictionary()
    let hud = WaitMBProgress()
    let companyService = CompanyService()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "组织架构"
        self.tableView.tableFooterView = UIView()
        self.tableView?.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        
        if UserDefaults.standard.object(forKey: "ZZJG") != nil{
            result_dict = UserDefaults.standard.object(forKey: "ZZJG") as! NSDictionary
            getResultDict()
        }
        
        companyService.delegate = self
        companyService.chooseCompany()
    }
    
    //# 🉐组织结构
    private func getResultDict(){
        let arr:Array<NSDictionary> = result_dict["dw_data"] as! Array<NSDictionary>
        var wzh_arr = Array<Any>()
        for companyInfo in arr{
            let dic = companyInfo as! [String:AnyObject]
            wzh_arr.append(ZZJGModel(dict:dic))
        }
        dw_arr = wzh_arr.sorted(by: { (s1, s2) -> Bool in
            let zzjg1:ZZJGModel = s1 as! ZZJGModel
            let zzjg2:ZZJGModel = s2 as! ZZJGModel
            return (zzjg1.wzh) < (zzjg2.wzh) ? true:false
        })
        self.tableView.reloadData()
    }
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return dw_arr.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as UITableViewCell
        let zzjg:ZZJGModel = dw_arr[indexPath.row] as! ZZJGModel
        cell.textLabel?.text = zzjg.mc
        return cell
    }

    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let zzjg:ZZJGModel = dw_arr[indexPath.row] as! ZZJGModel
        let bmjg:BMJGTableViewController = BMJGTableViewController()
        bmjg.dwid = zzjg.id
        bmjg.szk = zzjg.szk
        bmjg.mc = zzjg.mc
        bmjg.dict = result_dict as! [String : Any]
        self.navigateDetail(bmjg)
    }

}
