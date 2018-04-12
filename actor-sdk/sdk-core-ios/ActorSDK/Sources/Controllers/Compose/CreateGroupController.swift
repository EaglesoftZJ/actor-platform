//
//  GroupMembersController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/4/3.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit
class CreateGroupController: AAContactsListContentController, CLTokenInputViewDelegate, UITableViewDelegate, UITableViewDataSource, CommonServiceDelegate {
    func serviceSuccess(_ svc: CommonService!, object obj: Any!) {
        if svc == companyService {
            guard (obj as? NSDictionary) != nil else{return}
            dict = obj as! NSDictionary
            UserDefaults.standard.set(dict, forKey: "ZZJG")
            getTableList()
        }
    }
    
    func serviceFail(_ svc: CommonService!, info: String!) {
        self.confirmAlertUser("加载失败,点击确定重新加载", action: "确定", tapYes: {
            self.companyService.chooseCompany()
        })
    }
    
    
    fileprivate var groupTitle: String! = ""
    fileprivate var groupImage: UIImage?
    
    var memberUid:jint! = 0//创建群组的uid
    
    fileprivate var tokenView = CLTokenInputView()
    fileprivate var tokenViewHeight: CGFloat = 48
    
    fileprivate var selected = [TokenRef]()
    
    fileprivate var tableview = UITableView()
    
    fileprivate var tableArr = Array<Dictionary<String, Any>>()
    fileprivate var personArr = Array<Dictionary<String, Any>>()
    fileprivate var allPersonArr = Array<Dictionary<String, Any>>()
    fileprivate var dict = NSDictionary()
    fileprivate var indexRow:Int = 0
    fileprivate var dwid:String = ""
    fileprivate var bmid:String = ""
    fileprivate var szk:String = ""
    fileprivate var list:ARBindedDisplayList!
    fileprivate var jsonList = Dictionary<String, ACContact>()//flychat数组和组织架构数组印射
    fileprivate let companyService = CompanyService()
    
    public override init() {
        super.init()
        
        self.searchEnabled = false
        
        navigationItem.title = AALocalized("CreateGroupMembersTitle")
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "返回", style: .plain, target: self, action: #selector(back))
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: AALocalized("NavigationDone"), style: UIBarButtonItemStyle.done, target: self, action: #selector(doNext))
        
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func back() {
        if self.indexRow == 0 {
            self.navigateBack()
        }
        else {
            if self.indexRow == 3 {
                self.indexRow -= 2
            }
            else {
                self.indexRow -= 1
            }
            CellAction(index: self.indexRow, dwid: self.dwid, bmid: self.bmid, szk: self.szk)
        }
    }
    
    func doNext() {
        
        let res = IOSIntArray(length: UInt(selected.count))
        self.groupTitle = Actor.getUserWithUid(Actor.myUid()).getNameModel().get() + ","
        for i in 0..<selected.count {
            if (i == selected.count - 1) {
                self.groupTitle = self.groupTitle + selected[i].contact.name
            }
            else {
                self.groupTitle = self.groupTitle + selected[i].contact.name + ","
            }
            res?.replaceInt(at: UInt(i), with: selected[i].contact.uid)
        }

        let promise:ARPromise = Actor.createGroup(withTitle: self.groupTitle, withAvatar: nil, withUids: res)
        promise.then{ (res: JavaLangInteger!) in
            let gid = res.int32Value
            if self.groupImage != nil {
                _ = Actor.changeGroupAvatar(gid, image: self.groupImage!)
            }
            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer.group(with: gid)) {
                self.navigateDetail(customController)
            } else {
                self.navigateDetail(ConversationViewController(peer: ACPeer.group(with: gid)))
            }
            self.dismissController()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.removeAllSubviews()
        
        view.backgroundColor = .white
        
        tokenView.delegate = self
        tokenView.fieldColor = appStyle.vcTokenFieldTextColor
        tokenView.fieldTextColor = appStyle.vcTokenFieldTextColor
        tokenView.backgroundColor = appStyle.vcTokenFieldBgColor
        tokenView.tintColor = appStyle.vcTokenTintColor
        tokenView.fieldName = ""
        tokenView.drawBottomBorder = true

        let placeholder = AALocalized("CreateGroupMembersPlaceholders")
        let attributedPlaceholder = NSMutableAttributedString(string: placeholder)
        attributedPlaceholder.addAttribute(NSForegroundColorAttributeName, value: appStyle.vcHintColor, range: NSRange(location: 0, length: placeholder.length))
        tokenView.placeholderAttributedText = attributedPlaceholder

        self.view.addSubview(tokenView)
        
        createList()
    }
    
    //通知
    func loadDisplayList() {
        list = ActorSDK.sharedActor().contactsList
        
        for i in 0...Int(list.size())-1 {
            let contactP:ACContact = list.item(with: jint(i)) as! ACContact
            let contactUid:String = String(contactP.uid)
            jsonList[contactUid] = contactP
        }
        
        selectContact(uidP: String(memberUid))
    }
    
    func selectContact(uidP: String) {
        let contactP:ACContact = jsonList[uidP]!
        let token = CLToken(displayText: contactP.name, context: nil)
        tokenView.add(token)
        selected.append(TokenRef(contact: contactP,token: token))
    }
        
    func createList() {
        
        self.automaticallyAdjustsScrollViewInsets = false
        
        tableview.delegate = self
        tableview.dataSource = self
        tableview.tableFooterView = UIView()
        view.addSubview(tableview)
        tableview.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        tableview.keyboardDismissMode = UIScrollViewKeyboardDismissMode.onDrag
        
        if UserDefaults.standard.object(forKey: "ZZJG") != nil {
            dict = UserDefaults.standard.object(forKey: "ZZJG") as! NSDictionary
            if dict.allKeys.contains("dw_data") {
                getTableList()
            }
            else {
                companyService.delegate = self
                companyService.chooseCompany()
            }
        }
        else {
            companyService.delegate = self
            companyService.chooseCompany()
        }
    }
    
    func getTableList() {
        tableArr = dict["dw_data"] as! [Dictionary<String, Any>]
        
        //获取所有人员信息
        let arr = dict["yh_data"] as! [Dictionary<String, Any>]
        for d in arr {
            let dic:Dictionary = d
            if dic.keys.contains("IGIMID") {
                if jint(dic["IGIMID"] as! String) != Actor.myUid() {
                    allPersonArr.append(dic)
                }
            }
        }
        
        tableview.reloadData()
    }
    //tableView delegate datasource
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return personArr.count
        }
        return tableArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        if indexPath.section == 0 {//人员
            let CellRow = personArr[indexPath.row] as NSDictionary
            if CellRow["zwmc"] as? String == ""{
                cell.textLabel?.text = CellRow["xm"] as? String
            }
            else{
                cell.textLabel?.text = (CellRow["xm"] as? String)! + "(" + (CellRow["zwmc"] as? String)! + ")"
            }
        }
        else {//单位,部门
            let CellRow = tableArr[indexPath.row] as NSDictionary
            cell.textLabel?.text = CellRow["mc"] as? String
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0 {//添加人员
            let person = personArr[indexPath.row] as NSDictionary
            let uid:jint = jint(person["IGIMID"] as! String)!
            var isSelected:Bool = false
            for i in 0..<selected.count {
                let n = selected[i]
                if (n.contact.uid == uid) {
                    isSelected = true
                    selected.remove(at: i)
                    tokenView.remove(n.token)
                    break
                }
            }
            
            if isSelected == false {
                selectContact(uidP: String(uid))
            }
            
        }
        else {//进入部门
            let CellRow = tableArr[indexPath.row] as NSDictionary
            szk = CellRow["szk"] as! String
            self.indexRow += 1
            if indexRow == 1 {
                dwid = CellRow["id"] as! String
            }
            else {
                dwid = CellRow["dwid"] as! String
                bmid = CellRow["id"] as! String
            }
            CellAction(index: indexRow, dwid: dwid, bmid: bmid, szk: szk)
        }
    }
    
    //点击cell
    func CellAction(index: Int, dwid: String, bmid: String, szk: String) {
        if index == 0 {//单位
            personArr = []
            tableArr = dict["dw_data"] as! [Dictionary<String, Any>]
        }
        else if index == 1 {//部门
            personArr = []
            let arr:Array = dict["bm_data"] as! [Dictionary<String, Any>]
            var wzh_arr = Array<Dictionary<String, Any>>()
            for bm in arr {
                if bm["dwid"] as! String == dwid && bm["szk"] as! String == szk {
                    if bm["fid"] as! String == "-1   "
                    {
                        wzh_arr.append(bm)
                    }
                }
            }
            tableArr = wzh_arr
        }
        else {//人员和部门
            let yhArr:Array = dict["yh_data"] as! [Dictionary<String, Any>]
            var yh_arr = Array<Dictionary<String, Any>>()
            //人员
            for ygInfo in yhArr {
                if ygInfo["dwid"] as! String == dwid && ygInfo["bmid"] as! String == bmid && ygInfo["szk"] as! String == szk && jint(ygInfo["IGIMID"] as! String) != Actor.myUid()
                {
                    yh_arr.append(ygInfo)
                }
            }
            personArr = yh_arr
            //部门
            let bmArr:Array = dict["bm_data"] as! [Dictionary<String, Any>]
            var bm_arr = Array<Dictionary<String, Any>>()
            for bmInfo in bmArr {
                if bmInfo["dwid"] as! String == dwid && bmInfo["szk"] as! String == szk {
                    if bmInfo["fid"] as! String == bmid {
                        bm_arr.append(bmInfo)
                    }
                }
            }
            tableArr = bm_arr
        }
        tableview.reloadData()
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if personArr.count != 0{return 10}
        return 0
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView(frame:CGRect(x:0,y:0,width:self.tableView.bounds.size.width,height:10))
        headerView.backgroundColor = UIColor(red:238/255,green:233/255,blue:233/255,alpha:1)
        if personArr.count != 0 {
            if section == 0{return headerView}
            if section == 1{return headerView}
            return UIView()
        }
        return UIView()
    }
    //tokenView delegate
    open func tokenInputView(_ view: CLTokenInputView, didChangeText text: String?) {
        if text == "" && dict.allKeys.contains("dw_data") {
            CellAction(index: indexRow, dwid: dwid, bmid: bmid, szk: szk)
        }
        else {
            personArr = []
            tableArr = []
            for person in allPersonArr {
                let name:String = person["xm"] as! String
                if name.contains(text!) {
                    personArr.append(person)
                }
            }
            tableview.reloadData()
        }
    }
    
    open func tokenInputView(_ view: CLTokenInputView, didChangeHeightTo height: CGFloat) {
        tokenViewHeight = height
        
        self.view.setNeedsLayout()
    }
    
    open func tokenInputView(_ view: CLTokenInputView, didRemove token: CLToken) {
        for i in 0..<selected.count {
            let n = selected[i]
            if (n.token == token) {
                selected.remove(at: i)
                return
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        //收到displayList通知
        NotificationCenter.default.addObserver(self, selector: #selector(loadDisplayList), name: NSNotification.Name(rawValue: "displayList"), object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name(rawValue: "displayList"), object: nil)
    }
    
    open override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        tokenView.frame = CGRect(x: 0, y: 64, width: view.frame.width, height: tokenViewHeight)
        
        tableview.frame = CGRect(x: 0, y: tokenViewHeight+64, width: view.frame.width, height: view.frame.height - tokenViewHeight-64)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
}

private class TokenRef {
    var contact: ACContact
    var token: CLToken
    
    init(contact: ACContact, token: CLToken) {
        self.contact = contact
        self.token = token
    }
}
