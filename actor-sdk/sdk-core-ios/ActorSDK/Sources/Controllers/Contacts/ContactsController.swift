//
//  ContactsController.swift
//  ActorSDK
//
//  Created by dingjinming on 2018/1/11.
//  Copyright © 2018年 Steve Kite. All rights reserved.
//

import UIKit

class ContactsController: AAContactsListContentController,UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate,UISearchResultsUpdating {
    
    func updateSearchResults(for searchController: UISearchController) {
        let nav_VC = searchController.searchResultsController as! UINavigationController
        let resultVC = nav_VC.topViewController as! ContactsSearchController
        searchArray.removeAll()
        
        for contact in contactsArray
        {
            if contact.name.contains(searchController.searchBar.text!){
                searchArray.append(contact)
            }
        }

        resultVC.searchArray = searchArray
        resultVC.table.reloadData()
    }

   func numberOfSections(in tableView: UITableView) -> Int {
        return displayList.count + 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 3
        }
        let dic = displayList[section-1] as? NSDictionary
        let arr = dic?.object(forKey: "sectionArray") as! Array<ACContact>
        return arr.count
    }
    
   func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "tableCell", for: indexPath) as! UITableViewCell
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
        if section == 0 {
            return "联系人"
        }
        let dic = displayList[section-1] as! NSDictionary
        let text = dic["sectionTitle"] as! String
        return text
    }
   func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        var list = Array<String>()
        for dic in displayList {
            let text = (dic as! NSDictionary)["sectionTitle"] as! String
            list.append(text)
        }
        return list
    }
   func tableView(_ tableView: UITableView, sectionForSectionIndexTitle title: String, at index: Int) -> Int {
        return index
    }
   func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
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
   func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 56
    }
    
    let table = UITableView()
    var displayList = NSMutableArray()
    let sortList = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"]
    
    var searchController = UISearchController()
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
        createSearchBar()
    }

    func tableReload()
    {
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
        table.sectionIndexTrackingBackgroundColor = .green
        table.sectionIndexBackgroundColor = .clear
        table.autoresizingMask = [.flexibleHeight,.flexibleWidth]
    }
    
    func createSearchBar(){
            let searchResultController = UINavigationController(rootViewController: ContactsSearchController())
            searchController = UISearchController(searchResultsController: searchResultController)
            searchController.searchResultsUpdater = self
            searchController.searchBar.delegate = self
            searchController.hidesNavigationBarDuringPresentation = true
            searchController.dimsBackgroundDuringPresentation = true
            searchController.definesPresentationContext = true
            searchController.searchBar.sizeToFit()
            let headerView = UIView(frame:CGRect(x:0,y:0,width:table.frame.size.width,height:searchController.searchBar.frame.size.height))
            headerView.addSubview(searchController.searchBar)
            self.table.tableHeaderView = headerView
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
