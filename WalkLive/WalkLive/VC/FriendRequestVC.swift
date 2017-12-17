//
//  FriendRequestVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

// Friend/Request Manager View
class FriendRequestVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var segmentControl: UISegmentedControl!
    @IBOutlet weak var requestTable: UITableView!
    var receivedFriendRequests: [FriendRequest]!
    var sentFriendRequests: [FriendRequest]!
    var friends: [Friend]!

    
    let RECEIVED = 0
    let SENT = 1
    let FRIENDS = 2
    
    override func viewDidLoad() {
        super.viewDidLoad()
        requestTable.delegate = self
        requestTable.dataSource = self
        self.updateReceivedRequests()
    }
    
    // Private functions
    
    /*
     Reloads the table view.
     */
    private func getRequests(){
        self.requestTable.reloadData()
    }
    
    /*
     Calculates the number of rows needed for the table view, depending on which view the
     user selects.
     */
    private func numCount() -> Int{
        if (segmentControl.selectedSegmentIndex == RECEIVED){
            if self.receivedFriendRequests != nil {
                return self.receivedFriendRequests.count
            }
        } else if (segmentControl.selectedSegmentIndex == SENT){
            if self.sentFriendRequests != nil {
                return self.sentFriendRequests.count
            }
        }
        else {
            if self.friends != nil {
                return self.friends.count
            }
        }
        return 0
    }
    
    //Public Functions
    
    /*
     When user switches to the "Sent" view, the function gets and shows a list
     of sent friend requests.
    */
    func updateSentRequests(){
        backEndClient.getSentFriendRequests(success: { (updatedFriendRequests) in
            OperationQueue.main.addOperation {
            self.sentFriendRequests = updatedFriendRequests
            self.requestTable.reloadData()
            }
        }) { (error) in
            print(error)
        }
    }
    
    /*
     When user switches to the "Received" view, the function gets and shows a list
     of received friend requests.
    */
    func updateReceivedRequests(){
        backEndClient.getReceivedFriendRequests(success: { (receivedFriendRequests) in
            OperationQueue.main.addOperation {
            self.receivedFriendRequests = receivedFriendRequests
            self.requestTable.reloadData()
            }
        }) { (error) in
            print(error)
        }
    }
    
    /*
     When user switches to the "Friends" view, the fucntion gets and shows a list of
     friends of the user.
    */
    func updateFriendList(){
        backEndClient.getFriendList(success: { (friends) in
            OperationQueue.main.addOperation {
                self.friends = friends
                self.requestTable.reloadData()
            }
        }) { (error) in
            
        }
    }

    /*
     Checks which view the user selects and calls functions accordingly.
    */
    @IBAction func switchSegmentControl(_ sender: Any) {
        self.requestTable.reloadData()
        if (segmentControl.selectedSegmentIndex == RECEIVED){
            self.updateReceivedRequests()
        } else if (segmentControl.selectedSegmentIndex == SENT){
            self.updateSentRequests()
        }
        else {
            self.updateFriendList()
        }
    }
    
    /*
     Returns number of rows needed for the table view.
    */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.numCount()
    }
    
    /*
     Creates a single cell for the table view, depending on which view the user selects.
    */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = requestTable.dequeueReusableCell(withIdentifier: "FriendRequestCell", for: indexPath) as! FriendRequestTableViewCell
        //mark buttons
        cell.selectionStyle = .none
        cell.acceptButton.tag = indexPath.row
        cell.declineButton.tag = indexPath.row
        
        if (segmentControl.selectedSegmentIndex == RECEIVED) {
            let friendRequest = self.receivedFriendRequests[indexPath.row]
            cell.usernameLabel.text = friendRequest.sender
            cell.dateLabel.text = friendRequest.sent_on
            cell.acceptButton.isHidden = false
            cell.declineButton.isHidden = false
        }
        else if (segmentControl.selectedSegmentIndex == SENT){
            let friendRequest = self.sentFriendRequests[indexPath.row]
            cell.dateLabel.text = friendRequest.sent_on
            cell.usernameLabel.text = friendRequest.recipient
            cell.acceptButton.isHidden = true
            cell.declineButton.isHidden = true
        }
        else {
            let friend = self.friends[indexPath.row]
            cell.usernameLabel.text = friend.username
            cell.dateLabel.text = friend.contact
            cell.acceptButton.isHidden = true
            cell.declineButton.isHidden = true
        }
        return cell
    }

    /*
     Allows the user to accept a friend request, and calls backEndClient to add
     the friend to the user's friend list.
    */
    @IBAction func onAcceptButton(_ sender: Any) {
        let button = sender as! UIButton
        backEndClient.acceptFriendRequest(success: {
            OperationQueue.main.addOperation {
            self.receivedFriendRequests.remove(at: button.tag)
            self.requestTable.reloadData()
            }
        }, failure: { (error) in
            
        }, friendRequest: receivedFriendRequests[button.tag])
    }
    
    /*
     Allows the user to decline a friend request, and calls backEndClient to
     remove the request from sent friend request list.
     */
    @IBAction func onDeclineButton(_ sender: Any) {
        let button = sender as! UIButton
        backEndClient.rejectFriendRequest(success: {
            OperationQueue.main.addOperation {
                self.receivedFriendRequests.remove(at: button.tag)
                self.requestTable.reloadData()
            }
        }, failure: { (error) in
            
        }, friendRequest: receivedFriendRequests[button.tag])
    }
    
    /*
     Allows user to send a friend request.
    */
    @IBAction func onAddButton(_ sender: Any) {
        var fv = AddFriendView()
        fv = fv.loadNib()
        self.view.addSubview(fv)
        fv.center = self.view.center
        self.updateSentRequests()
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
