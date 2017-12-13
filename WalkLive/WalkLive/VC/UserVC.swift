//
//  UserVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class UserVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var usernameLabel: UILabel!
    
    @IBOutlet weak var userContactLabel: UILabel!
    
    @IBOutlet weak var emergencyContactLabel: UILabel!
    @IBOutlet weak var friendRequestButton: UIButton!
    
    @IBOutlet weak var friendTripTable: UITableView!
    
    
    var receivedFriendRequests: [FriendRequest]!
    var friendsTrip: [TimePoint]!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        friendTripTable.delegate = self
        friendTripTable.dataSource = self
        self.updateFriendTrips()
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.setUserVCInfo()
    }
    
    func updateFriendTrips(){
        backEndClient.getAllTrip(success: { (timePoints) in
            OperationQueue.main.addOperation {
                print(timePoints)
                self.friendsTrip = timePoints
                self.friendTripTable.reloadData()
            }
        }) { (error) in
            print("failed to get all friend trips")
        }
    }
    
    
    private func setUserVCInfo() {
        self.usernameLabel.text = currentUserInfo?.username
        self.userContactLabel.text = "My Contact: " + (currentUserInfo?.contact)!
        self.emergencyContactLabel.text = stringBuilder.emerStringBuilderWithUser()
    }
    
    @IBAction func onMyContactPencilButton(_ sender: Any) {
        segueToSettingVC()
    }
    
    @IBAction func onEmerContactPencilButton(_ sender: Any) {
        segueToSettingVC()
    }
    
    
    

    func segueToSettingVC(){
        self.performSegue(withIdentifier: "settingSegue", sender: self)
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.friendsTrip != nil {
            print("reached here")
            return self.friendsTrip.count
        } else {
            print("reached here for 0 ")
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendTripTable.dequeueReusableCell(withIdentifier: "FriendTripTableViewCell", for: indexPath) as! FriendTripTableViewCell
        let friendTrip = self.friendsTrip[indexPath.row]
        cell.friendNameLabel.text = friendTrip.username
        cell.destinationLabel.text = "TO: " + friendTrip.destination!
        cell.timeSpentLabel.text = friendTrip.timeSpent
        cell.phoneButton.tag = indexPath.row
        cell.messageButton.tag = indexPath.row
        cell.selectionStyle = .none
        return cell
    }
    
    
    func updateFriendRequest(){
        backEndClient.getReceivedFriendRequests(success: { (receivedFriendRequests) in
            OperationQueue.main.addOperation {
            self.updateFriendMangerTitle(receivedFriendRequests: receivedFriendRequests)
            }
        }) { (error) in
            print(error)
        }
    }
    
    private func updateFriendMangerTitle(receivedFriendRequests: [FriendRequest]?){
        if (receivedFriendRequests == nil || receivedFriendRequests?.count == 0){
            return
        } else {
            self.receivedFriendRequests = receivedFriendRequests!
            self.friendRequestButton.titleLabel?.text = "Friend Manager: " + String(receivedFriendRequests!.count) + " request(s)"
            self.friendRequestButton.sizeToFit()
            

        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    

     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let backItem = UIBarButtonItem()
        backItem.title = "Back"
        backItem.tintColor = primaryColor
        navigationItem.backBarButtonItem = backItem
        if (segue.identifier == "friendRequestSegue"){
            let vc = segue.destination as! FriendRequestVC
            vc.receivedFriendRequests = self.receivedFriendRequests
        } else if (segue.identifier == "settingSegue"){
        } else if (segue.identifier == "tripRequestSegue"){
        }
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
    
}

