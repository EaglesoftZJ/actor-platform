//
//  ContactsController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/11.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class ContactsController: AAContactsListContentController,UITableViewDelegate,UITableViewDataSource,UISearchDisplayDelegate,UISearchBarDelegate {
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        searchArray.removeAll()

        for contact in contactsArray
        {
            if contact.name.contains(searchText){
                searchArray.append(contact)
            }
        }
    }
    
   func numberOfSections(in tableView: UITableView) -> Int {
        if tableView == searchDisplay.searchResultsTableView {
            return 1
        }
        return displayList.count + 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == searchDisplay.searchResultsTableView {
            return searchArray.count
        }
        if section == 0 {
            return 3
        }
        let dic = displayList[section-1] as? NSDictionary
        let arr = dic?.object(forKey: "sectionArray") as! Array<ACContact>
        return arr.count
    }
    
   func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == searchDisplay.searchResultsTableView {
            let cell = tableView.dequeueReusableCell(withIdentifier: "cell") as! ContactsGroupCell
            let contact = searchArray[indexPath.row]
            cell.nameLabel.text = contact.name
            cell.avatarView.bind(contact.name, id: Int(contact.uid), avatar: contact.avatar)
            return cell
        }
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath)
            tableCell(row: indexPath.row, cell: cell)
            return cell
        }
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell") as! ContactsGroupCell
        let dic = displayList[indexPath.section-1] as! NSDictionary
        let arr = dic["sectionArray"] as! Array<ACContact>
        let data = arr[indexPath.row]
        cell.nameLabel.text = data.name
        cell.avatarView.bind(data.name, id: Int(data.uid), avatar: data.avatar)
        return cell
    }
    //创建三个自定义cell
    func tableCell(row: Int,cell: UITableViewCell){
        let imageList = ["ic_create_channel","ic_chats_outline","ic_add_user"]
        let textList = ["组织架构","群组","创建群组"]
        cell.imageView?.image = UIImage.bundled(imageList[row])
        cell.textLabel?.text = textList[row]
    }
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if tableView == searchDisplay.searchResultsTableView{
            return nil
        }
        if section == 0 {
            return "联系人"
        }
        let dic = displayList[section-1] as! NSDictionary
        let text = dic["sectionTitle"] as! String
        return text
    }
   func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        if tableView == table{
            var list = Array<String>()
            for dic in displayList {
                let text = (dic as! NSDictionary)["sectionTitle"] as! String
                list.append(text)
            }
            return list
        }
        return nil
    }
   func tableView(_ tableView: UITableView, sectionForSectionIndexTitle title: String, at index: Int) -> Int {
        if tableView == table{
            return index
        }
        return 0
    }
   func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if tableView == searchDisplay.searchResultsTableView {
            let contact = searchArray[indexPath.row]
            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(contact.uid)) {
                navigateDetail(customController)
            } else {
                navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(contact.uid)))
            }
        }
        else{
            if indexPath.section == 0 {
                if indexPath.row == 0{self.navigateDetail(ZZJGTableViewController())}
                if indexPath.row == 1{self.navigateNext(ContactsGroupController())}
                if indexPath.row == 2{self.navigateNext(AAGroupCreateViewController(isChannel: false), removeCurrent: false)}
            }
            else{
                let dic = displayList[indexPath.section-1] as! NSDictionary
                let arr = dic["sectionArray"] as! Array<ACContact>
                let contact = arr[indexPath.row]
                if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(contact.uid)) {
                    navigateDetail(customController)
                } else {
                    navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(contact.uid)))
                }
            }
        }
    }
    
   func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    let table = UITableView()
    var displayList = NSMutableArray()
    let sortList = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"]
    
    var searchDisplay = UISearchDisplayController()
    var searchArray = [ACContact]()
    var contactsArray = [ACContact]()
    
    public override init() {
        super.init()
        tabBarItem = UITabBarItem(title: "TabPeople", img: "TabIconContacts", selImage: "TabIconContactsHighlighted")
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.add, target: self, action: #selector(findContact))
    }
    
    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        removeAllFatherView()
    
        self.edgesForExtendedLayout = UIRectEdge(rawValue: 0)
        self.automaticallyAdjustsScrollViewInsets = false
    
        //收到displayList通知
        NotificationCenter.default.addObserver(self, selector: #selector(tableReload), name: NSNotification.Name(rawValue: "displayList"), object: nil)
        self.title = "联系人"
        view.backgroundColor = .white
        
        createList()
        createSearchDisplay()
    }

    func tableReload()
    {
        self.hidePlaceholder()
        self.table.showView()
        
        let list:ARBindedDisplayList! = ActorSDK.sharedActor().contactsList
        
        for charTmp in sortList {
            
            let array = NSMutableArray()
            
            for i in 0...Int(list.size())-1
            {
                let data = list.item(with: jint(i)) as! ACContact
                
                let pinyin = data.pyShort
                if charTmp == "#" && !pureLetters(pinyin!){
                    array.add(data)
                }
                else if charTmp == pinyin {
                    array.add(data)
                }
            }
            
            let dic = NSMutableDictionary()
            if array.count != 0
            {
                dic["sectionArray"] = array
                dic["sectionTitle"] = charTmp
                displayList.add(dic.copy())
            }
        }
        table.reloadData()
        
        for i in 0...Int(list.size())-1 {
            let data = list.item(with: jint(i)) as! ACContact
            contactsArray.append(data)
        }
    }
    
    func createList()
    {
        table.frame = view.frame
        table.delegate = self
        table.dataSource = self
        table.tableFooterView = UIView()
        view.addSubview(table)
        table.register(ContactsGroupCell.self, forCellReuseIdentifier: "cell")
        table.register(UITableViewCell.self, forCellReuseIdentifier: "tableCell")
        //索引
        table.sectionIndexColor = .gray
        table.sectionIndexTrackingBackgroundColor = .gray
        table.sectionIndexBackgroundColor = .clear
        table.autoresizingMask = [.flexibleHeight,.flexibleWidth]
    }
    
    func createSearchDisplay()
    {
        let style = ActorSDK.sharedActor().style
        let searchBar = UISearchBar()
        
        searchBar.searchBarStyle = .default
        searchBar.isTranslucent = false
        searchBar.placeholder = ""
        searchBar.barTintColor = style.searchBackgroundColor.forTransparentBar()
        searchBar.setBackgroundImage(Imaging.imageWithColor(style.searchBackgroundColor, size: CGSize(width: 1, height: 1)), for: .any, barMetrics: .default)
        searchBar.backgroundColor = style.searchBackgroundColor
        searchBar.tintColor = style.searchCancelColor
        searchBar.keyboardAppearance = style.isDarkApp ? UIKeyboardAppearance.dark : UIKeyboardAppearance.light
        let fieldBg = Imaging.imageWithColor(style.searchFieldBgColor, size: CGSize(width: 14,height: 28))
            .roundCorners(14, h: 28, roundSize: 4)
        searchBar.setSearchFieldBackgroundImage(fieldBg.stretchableImage(withLeftCapWidth: 7, topCapHeight: 0), for: UIControlState())
        for subView in searchBar.subviews {
            for secondLevelSubview in subView.subviews {
                if let tf = secondLevelSubview as? UITextField {
                    tf.textColor = style.searchFieldTextColor
                    break
                }
            }
        }
        
        searchDisplay = UISearchDisplayController(searchBar: searchBar, contentsController: self)
        searchDisplay.searchBar.delegate = self
        searchDisplay.searchResultsDataSource = self
        searchDisplay.searchResultsDelegate = self
        searchDisplay.delegate = self
        searchDisplay.searchResultsTableView.register(ContactsGroupCell.self, forCellReuseIdentifier: "cell")
        searchDisplay.searchResultsTableView.separatorStyle = UITableViewCellSeparatorStyle.none
        searchDisplay.searchResultsTableView.backgroundColor = ActorSDK.sharedActor().style.vcBgColor
        let header = AATableViewHeader(frame: CGRect(x: 0, y: 0, width: view.frame.size.width, height: 44))
        header.addSubview(searchDisplay.searchBar)
        table.tableHeaderView = header
    }
    
    //移除所有父类view
    func removeAllFatherView() {
        view.removeAllSubviews()
    }
    
    func pureLetters(_ str: String) -> Bool{
        if str.matches("(?i)[^a-z]*[a-z]+[^a-z]*")
        {
            return true
        }
        return false
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let searchBar = searchDisplay.searchBar
        
        let superView = searchBar.superview
        if !(superView is UITableView) {
            searchBar.removeFromSuperview()
            superView?.addSubview(searchBar)
        }
        
        table.tableHeaderView?.setNeedsLayout()
        
        binder.bind(Actor.getAppState().isContactsEmpty, closure: { (value: Any?) -> () in
            if let empty = value as? JavaLangBoolean {
                if Bool(empty.booleanValue()) == true {
                    self.table.hideView()
                    self.showPlaceholder()
                } else {
                    self.table.showView()
                    self.hidePlaceholder()
                }
            }
        })
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        searchDisplay.setActive(false, animated: false)
    }
    
    // Searching for contact
    
    func findContact() {
        
        startEditField { (c) -> () in
            c.title = "FindTitle"
            c.actionTitle = "NavigationFind"
            
            c.hint = "FindHint"
            c.fieldHint = "FindFieldHint"
            
            c.fieldAutocapitalizationType = .none
            c.fieldAutocorrectionType = .no
            c.fieldReturnKey = .search
            
            c.didDoneTap = { (t, c) -> () in
                
                if t.length == 0 {
                    return
                }
                
                self.executeSafeOnlySuccess(Actor.findUsersCommand(withQuery: t), successBlock: { (val) -> Void in
                    var user: ACUserVM? = nil
                    if let users = val as? IOSObjectArray {
                        if Int(users.length()) > 0 {
                            if let tempUser = users.object(at: 0) as? ACUserVM {
                                user = tempUser
                            }
                        }
                    }
                    
                    if user != nil {
                        c.execute(Actor.addContactCommand(withUid: user!.getId())!, successBlock: { (val) -> Void in
                            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(user!.getId())) {
                                self.navigateDetail(customController)
                            } else {
                                self.navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(user!.getId())))
                            }
                            c.dismissController()
                        }, failureBlock: { (val) -> Void in
                            if let customController = ActorSDK.sharedActor().delegate.actorControllerForConversation(ACPeer_userWithInt_(user!.getId())) {
                                self.navigateDetail(customController)
                            } else {
                                self.navigateDetail(ConversationViewController(peer: ACPeer_userWithInt_(user!.getId())))
                            }
                            c.dismissController()
                        })
                    } else {
                        c.alertUser("FindNotFound")
                    }
                })
            }
        }
    }
}
