//
//  SetFriendVC.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SetFriendVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var currentFriendNameLabel: UILabel!
    @IBOutlet weak var currentEmergencyNumberLabel: UILabel!
    @IBOutlet weak var friendListTable: UITableView!
    
    
    var friends: [Friend]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        friendListTable.delegate = self
        friendListTable.dataSource = self
        self.updateFriends()
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.currentFriendNameLabel.text = emerIdStringBuilder()
        self.currentEmergencyNumberLabel.text = emerStringBuilder()
    }
    
    
    /// Private functions
    
    /*
     Builds emergency contact number info
     - Returns: string containing emergency contact number
     */
    private func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil) {
            return "None"
        }
        return (currentUserInfo?.emergency_number)!
    }
    
    /*
     Builds emergency contact name info
     - Returns: string containing emergency contact name
     */
    private func emerIdStringBuilder() -> String{
            if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
                return "None"
            }
            return (currentUserInfo?.emergency_id)!
    }
    
    
    /// Public functions
    
    
    /*
     Sets up table view showing friends
     - Parameters:
     - tableView: UITableView that shows friends
     - sections: number of rows
     - Returns: number of rows needed
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.friends != nil {
            return self.friends.count
        }
        return 0
    }
    
    /*
     Creates a single table view cell that contains friend name and number
     - Parameters:
     - tableView: UITableView that shows friends
     - indexPath: index of cell
     - Returns: UITableViewCell object containing friend name number
     */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendListTable.dequeueReusableCell(withIdentifier: "SetFriendTableViewCell", for: indexPath) as! SetFriendTableViewCell
        let friend = friends[indexPath.row]
        cell.friendNameLabel.text = friend.username
        cell.friendNumberLabel.text = friend.contact
        cell.selectionStyle = .none
        return cell
    }

    /*
     Selects a friend as emergency contact
     */
    @IBAction func onSelectButton(_ sender: Any) {
        let button = sender as! UIButton
        self.currentFriendNameLabel.text = friends[button.tag].username
        self.currentEmergencyNumberLabel.text = friends[button.tag].contact
        
    }
    
    /*
     Saves the friend as emergency contact
     */
    @IBAction func onSaveButton(_ sender: Any) {
        if (self.currentFriendNameLabel.text! == currentUserInfo.emergency_id) {
            return
        }
        let emergencyContact = EmergencyContact(emergency_id: self.currentFriendNameLabel.text!, emergency_number: self.currentEmergencyNumberLabel.text!)
        backEndClient.updateEmergencyContact(success: { (updatedEmergencyContact) in

            OperationQueue.main.addOperation {
                currentUserInfo.emergency_id = self.currentFriendNameLabel.text!
                currentUserInfo.emergency_number = self.currentEmergencyNumberLabel.text!
                let successView = warnigSignFactory.makeSaveSettingsSuccessWarningSign()
                successView.center = self.view.center
                self.view.addSubview(successView)
            }
        }, failure: { (error) in
            
        }, emergencyContact: emergencyContact)
    }
    
    /*
     Update friend list
     */
    func updateFriends() {
        backEndClient.getFriendList(success: { (friends) in
            OperationQueue.main.addOperation {
            self.friends = friends
                self.friendListTable.reloadData()
            }
        }) { (error) in
            
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
