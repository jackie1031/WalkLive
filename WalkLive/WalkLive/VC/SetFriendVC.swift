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
    
    
    // Private Functions
    
    
    /*
     Builds emergency contact number.
     Returns: emergency contact number
     */
    private func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil) {
            return "None"
        }
        return (currentUserInfo?.emergency_number)!
    }
    
    /*
     Buils emergency contact name
     Returns: emergency contact name
     */
    private func emerIdStringBuilder() -> String{
            if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
                return "None"
            }
            return (currentUserInfo?.emergency_id)!
    }
    
    /*
     Updates friend list
     */
    private func updateFriends() {
        backEndClient.getFriendList(success: { (friends) in
            OperationQueue.main.addOperation {
                self.friends = friends
                self.friendListTable.reloadData()
            }
        }) { (error) in
            
        }
    }
    
    
    // Public functions
    
    
    /*
     Refreshes table view showing friends
     - Parameters:
       - tableView: UITableView that shows friends
       - section: number of rows
     - Returns: number of rows
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.friends != nil {
            return self.friends.count
        }
        return 0
    }
    
    /*
     Sets up a single table cell containing friend info
     - Parameters:
       - tableView: UITableView that shows friends
       - indexPath: index of cell in table
     - Returns: a single UITableViewCell object
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
     Selects friend as emergency contact
     */
    @IBAction func onSelectButton(_ sender: Any) {
        let button = sender as! UIButton
        self.currentFriendNameLabel.text = friends[button.tag].username
        self.currentEmergencyNumberLabel.text = friends[button.tag].contact
        
    }
    
    /*
     Saves settings
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
