//
//  UserVC.swift
//  Walklive
//
//  User profile view
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MessageUI


class UserVC: UIViewController, UITableViewDelegate, UITableViewDataSource, MFMessageComposeViewControllerDelegate {
    
    @IBOutlet weak var usernameLabel: UILabel!
    
    @IBOutlet weak var userContactLabel: UILabel!
    
    @IBOutlet weak var emergencyContactLabel: UILabel!
    @IBOutlet weak var friendRequestButton: UIButton!
    
    @IBOutlet weak var friendTripTable: UITableView!
    
    
    var receivedFriendRequests: [FriendRequest]!
    var friendsTrip: [TimePoint]?
    
    var timeInterval = TimeInterval(20)
    var tripUpdater: TripUpdater!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        friendTripTable.delegate = self
        friendTripTable.dataSource = self
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.updateFriendTrips()
        self.setUserVCInfo()
        self.initializeUpdates()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        print("closed successfully")
        self.endTimers()
        
    }
    
    
    /// Private functions
    
    
    /*
     Initializes an updator for the current trip (TripUpdater object)
     */
    private func initializeUpdates(){
        self.tripUpdater = TripUpdater(tripTableDelegate: self)
        self.tripUpdater.startTimer(timeInterval: self.timeInterval)
    }
    
    /*
     Stops timer (in tripUpdater).
     */
    private func endTimers(){
        self.tripUpdater.endTimer()
    }
    
    /*
     Updates friend trips
     */
    func updateFriendTrips(){
        backEndClient.getAllTrip(success: { (timePoints) in
            OperationQueue.main.addOperation {
                self.friendsTrip = timePoints
                self.friendTripTable.reloadData()
            }
        }) { (error) in
            print("failed to get all friend trips")
        }
    }
    
    /*
     Refreshes user profile, such as username, phone number, and emergency contact
     */
    private func setUserVCInfo() {
        self.usernameLabel.text = currentUserInfo?.username
        self.userContactLabel.text = "My Contact: " + (currentUserInfo?.contact)!
        self.emergencyContactLabel.text = stringBuilder.emerStringBuilderWithUser()
    }
    
    /*
     Updates number of friend request
     */
    private func updateFriendMangerTitle(receivedFriendRequests: [FriendRequest]?){
        if (receivedFriendRequests == nil || receivedFriendRequests?.count == 0){
            return
        } else {
            self.receivedFriendRequests = receivedFriendRequests!
            self.friendRequestButton.titleLabel?.text = "Friend Manager: " + String(receivedFriendRequests!.count) + " request(s)"
            self.friendRequestButton.sizeToFit()
            
            
        }
    }
    
    /*
     Performs segue to settingsVC
     */
    private func segueToSettingVC(){
        self.performSegue(withIdentifier: "settingSegue", sender: self)
    }
    
    /*
     Updates friend requests and the number of them.
     */
    private func updateFriendRequest(){
        backEndClient.getReceivedFriendRequests(success: { (receivedFriendRequests) in
            OperationQueue.main.addOperation {
                self.updateFriendMangerTitle(receivedFriendRequests: receivedFriendRequests)
            }
        }) { (error) in
            print(error)
        }
    }
    
    
    /// Public functions
    
    
    /*
     Brings to settingsVC
     */
    @IBAction func onMyContactPencilButton(_ sender: Any) {
        segueToSettingVC()
    }
    
    /*
     Brings to settingsVC
     */
    @IBAction func onEmerContactPencilButton(_ sender: Any) {
        segueToSettingVC()
    }
    
    /*
     Shows table view for friends.
     - Parameters:
     - tableView: UITableView showing friends
     - section: number of rows
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.friendsTrip != nil {
            print("reached here")
            return self.friendsTrip!.count
        } else {
            print("reached here for 0 ")
            return 0
        }
    }
    
    /*
     Sets up a single cell showing friend info
     - Parameters:
     - tableView: UITableView that shows friend list
     - indexPath: index of this cell
     - Returns:
     - a UITableViewCell containing friend info
     */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendTripTable.dequeueReusableCell(withIdentifier: "FriendTripTableViewCell", for: indexPath) as! FriendTripTableViewCell
        let friendTrip = self.friendsTrip![indexPath.row]
        cell.friendNameLabel.text = friendTrip.username
        cell.destinationLabel.text = "To: " + friendTrip.destination!
        cell.sourceLabel.text = "At: (" + String(format: "%.3f", (friendTrip.curLat)!) + ", " + String(format: "%.3f", (friendTrip.curLong)!) + ")"
        cell.timeSpentLabel.text = friendTrip.timeSpent
        cell.phoneButton.tag = indexPath.row
        cell.messageButton.tag = indexPath.row
        cell.mapButton.tag = indexPath.row
       // cell.messageButton.tag = indexPath.row
        cell.selectionStyle = .none
        return cell
    }
    
    /*
     Send text message to a friend.
     */
    @IBAction func onMessageButton(_ sender: UIButton) {
        let phoneNum = friendsTrip![sender.tag].emergencyNum
        if (MFMessageComposeViewController.canSendText()) {
            let controller = MFMessageComposeViewController()
            controller.body = "Hey! Where are you?" + "From: \(currentUserInfo.username!)"
            controller.recipients = [phoneNum!]
            controller.messageComposeDelegate = self
            self.present(controller, animated: true, completion: nil)
        }
    }
    
    /*
     Call a friend.
     */
    @IBAction func onPhoneButton(_ sender: UIButton) {
        let phoneNum = friendsTrip![sender.tag].emergencyNum
        //Does not work in Simulator
        if let url = URL(string: "tel://\(phoneNum!)"), UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    /*
     Show message compose view controller for user to edit and send message.
     */
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        //... handle sms screen actions
        self.dismiss(animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     MARK: - Navigation
     Prepare for different segues differently.
     */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let backItem = UIBarButtonItem()
        backItem.title = currentUserInfo.username
        backItem.tintColor = primaryColor
        navigationItem.backBarButtonItem = backItem
        if (segue.identifier == "friendRequestSegue"){
            let vc = segue.destination as! FriendRequestVC
            vc.receivedFriendRequests = self.receivedFriendRequests
        } else if (segue.identifier == "settingSegue"){
        } else if (segue.identifier == "tripRequestSegue"){
        } else if (segue.identifier == "friendTrackerSegue"){
            let vc = segue.destination as! FriendTrackVC
            let button = sender as! UIButton
            vc.currentFriendTrip = friendsTrip?[button.tag]
            vc.friendTrips = self.friendsTrip
        }
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
    
}

extension UserVC: TripTableUpdateDelegate{
    /*
     Updates table that shows all friends' trips
     */
    func updateTable() {
        backEndClient.getAllTrip(success: { (friendTrips) in
            OperationQueue.main.addOperation {
                self.friendsTrip = friendTrips
                self.friendTripTable.reloadData()
                print("reloaded table!")
            }
        }) { (error) in
            print("failed to update")
        }
    }
}

