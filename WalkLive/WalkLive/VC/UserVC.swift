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
    @IBOutlet weak var tripRequestButton: UIButton!
    
    @IBOutlet weak var addFriendTextField: UITextField!
    
    @IBOutlet weak var addFriendAcceptButton: UIButton!
    
    @IBOutlet weak var addFriendCancelButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        hideAddFriend()
    }
    
    func hideAddFriend() {
        self.addFriendTextField.isHidden = true
        self.addFriendAcceptButton.isHidden = true
        self.addFriendCancelButton.isHidden = true
    }
    
    func unhideAddFriend() {
        self.addFriendTextField.isHidden = false
        self.addFriendAcceptButton.isHidden = false
        self.addFriendCancelButton.isHidden = false
    }
    @IBAction func onAddFriendButton(_ sender: Any) {
        unhideAddFriend()
    }

    @IBAction func onAddFriendCancelButton(_ sender: Any) {
        hideAddFriend()
    }
    
    @IBAction func onAddFriendAcceptButton(_ sender: Any) {
        //end points here
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
