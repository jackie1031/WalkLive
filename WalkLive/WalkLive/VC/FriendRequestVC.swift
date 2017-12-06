//
//  FriendRequestVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FriendRequestVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var segmentControl: UISegmentedControl!
    @IBOutlet weak var requestTable: UITableView!
    var receivedFriendRequests: [FriendRequest]!
    var sentFriendRequests: [FriendRequest]!
    
    let RECEIVED = 0
    let SENT = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        requestTable.delegate = self
        requestTable.dataSource = self
        // Do any additional setup after loading the view.
    }
    
    @IBAction func switchSegmentControl(_ sender: Any) {
        self.requestTable.reloadData()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.numCount()
    }
    
    private func numCount() -> Int{
        if (segmentControl.selectedSegmentIndex == RECEIVED){
            if self.receivedFriendRequests != nil {
                return self.receivedFriendRequests.count
            }
        } else {
            if self.sentFriendRequests != nil {
                return self.sentFriendRequests.count
            }
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = requestTable.dequeueReusableCell(withIdentifier: "FriendRequestCell", for: indexPath) as! FriendRequestTableViewCell
        //mark buttons
        cell.acceptButton.tag = indexPath.row
        cell.declineButton.tag = indexPath.row
        
        if (segmentControl.selectedSegmentIndex == RECEIVED) {
            let friendRequest = self.receivedFriendRequests[indexPath.row]
            cell.usernameLabel.text = friendRequest.sender
            
            cell.acceptButton.isHidden = false
            cell.declineButton.isHidden = false
        }
        else {
            cell.acceptButton.isHidden = true
            cell.declineButton.isHidden = true
        }
        return cell
    }

    
    @IBAction func onAcceptButton(_ sender: Any) {
        let button = sender as! UIButton
        self.receivedFriendRequests.remove(at: button.tag)
    }
    
    @IBAction func onDeclineButton(_ sender: Any) {
        let button = sender as! UIButton
        self.receivedFriendRequests.remove(at: button.tag)
    }
    
    
    
//    override func viewWillAppear(_ animated: Bool) {
//        self.setBackButtons()
//    }
//
//    func setBackButtons(){
//        let backItem = UIBarButtonItem()
//        backItem.tintColor = primaryColor
//        self.navigationItem.backBarButtonItem = backItem
//    }

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
