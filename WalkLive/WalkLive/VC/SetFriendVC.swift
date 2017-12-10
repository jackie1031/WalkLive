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

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.currentFriendNameLabel.text = emerIdStringBuilder()
        self.currentEmergencyNumberLabel.text = emerStringBuilder()
    }
    
    private func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil) {
            return "None"
        }
        return (currentUserInfo?.emergency_number)!
    }
    
    private func emerIdStringBuilder() -> String{
            if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
                return "None"
            }
            return (currentUserInfo?.emergency_id)!
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.friends != nil {
            return self.friends.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendListTable.dequeueReusableCell(withIdentifier: "SetFriendTableViewCell", for: indexPath) as! SetFriendTableViewCell
        
        cell.selectionStyle = .none
        return cell
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
