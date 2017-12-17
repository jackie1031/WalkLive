//
//  FriendRequestVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import CoreLocation
import MessageUI

class FriendRequestVC: UIViewController, UITableViewDelegate, UITableViewDataSource, MFMessageComposeViewControllerDelegate {
    
    @IBOutlet weak var segmentControl: UISegmentedControl!
    @IBOutlet weak var requestTable: UITableView!
    var receivedFriendRequests: [FriendRequest]!
    var sentFriendRequests: [FriendRequest]!
    var friends: [Friend]!
    var locationManager = CLLocationManager()
    
    let RECEIVED = 0
    let SENT = 1
    let FRIENDS = 2
    
    override func viewDidLoad() {
        super.viewDidLoad()
        requestTable.delegate = self
        requestTable.dataSource = self
        self.updateReceivedRequests()
        // Do any additional setup after loading the view.
//        self.testFriendRequestTable()
    }
    
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
    
    func updateFriendList(){
        backEndClient.getFriendList(success: { (friends) in
            OperationQueue.main.addOperation {
                self.friends = friends
                self.requestTable.reloadData()
            }
        }) { (error) in
            
        }
    }
//    private func testFriendRequestTable(){
//        var list = [FriendRequest]()
//        let fr1 = FriendRequest()
//        let fr2 = FriendRequest()
//        list.append(fr1)
//        list.append(fr2)
//        self.receivedFriendRequests = list
//        self.sentFriendRequests = list
//        self.friends = list
//    }
    
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.numCount()
    }
    
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
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = requestTable.dequeueReusableCell(withIdentifier: "FriendRequestCell", for: indexPath) as! FriendRequestTableViewCell
        //mark buttons
        cell.selectionStyle = .none
        cell.acceptButton.tag = indexPath.row
        cell.declineButton.tag = indexPath.row
        cell.phoneButton.tag = indexPath.row
        cell.messageButton.tag = indexPath.row
        
        if (segmentControl.selectedSegmentIndex == RECEIVED) {
            let friendRequest = self.receivedFriendRequests[indexPath.row]
            cell.usernameLabel.text = friendRequest.sender
            cell.dateLabel.text = friendRequest.sent_on
            cell.phoneButton.isHidden = false
            cell.messageButton.isHidden = false
        }
        else if (segmentControl.selectedSegmentIndex == SENT){
            let friendRequest = self.sentFriendRequests[indexPath.row]
            cell.dateLabel.text = friendRequest.sent_on
            cell.usernameLabel.text = friendRequest.recipient
            cell.phoneButton.isHidden = false
            cell.messageButton.isHidden = false
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
    
    private func getRequests(){
        self.requestTable.reloadData()
    }
    
    @IBAction func onAddButton(_ sender: Any) {
        var fv = AddFriendView()
        fv = fv.loadNib()
        self.view.addSubview(fv)
        fv.center = self.view.center
        self.updateSentRequests()
    }
    
    @IBAction func onPhoneButton(_ sender: UIButton) {
        let busPhone = friends[sender.tag].contact
        //Does not work in Simulator
        if let url = URL(string: "tel://\(busPhone)"), UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    @IBAction func onMessageButton(_ sender: UIButton) {
        if (MFMessageComposeViewController.canSendText()) {
            let controller = MFMessageComposeViewController()
            controller.body = buildMessage(location: locationManager.location!)
            
            //FTOB needed here.
            controller.recipients = [friends[sender.tag].contact]
            controller.messageComposeDelegate = self
            self.present(controller, animated: true, completion: nil)
        }
    }
    
    func buildMessage(location: CLLocation) -> String {
        if (overarchTimeManager == nil) {
            return messages.buildMessageWithoutTrip()
        }
        
        return messages.buildMessageWithTrip(timeManager: overarchTimeManager)
    }
    
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        //... handle sms screen actions
        self.dismiss(animated: true, completion: nil)
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
