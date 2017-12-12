//
//  UserVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class UserVC: UIViewController {
    @IBOutlet weak var usernameLabel: UILabel!
    
    @IBOutlet weak var userContactLabel: UILabel!
    
    @IBOutlet weak var emergencyContactLabel: UILabel!
    @IBOutlet weak var friendRequestButton: UIButton!
    var receivedFriendRequests: [FriendRequest]!
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.setUserVCInfo()
    }
    
    
    private func setUserVCInfo() {
        self.usernameLabel.text = currentUserInfo?.username
        self.userContactLabel.text = "My Contact: " + (currentUserInfo?.contact)!
        self.emergencyContactLabel.text = stringBuilder.emerStringBuilder()
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

