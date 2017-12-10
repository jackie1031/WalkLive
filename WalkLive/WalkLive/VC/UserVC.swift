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
        if (currentUserInfo?.emergencyNumber == nil) {
            self.emergencyContactLabel.text = "Emer. Contact: None"
        } else {
            self.emergencyContactLabel.text = "Emer. Contact: " + (currentUserInfo?.emergencyNumber)!
        }
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    

     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "friendRequestSegue"){
            let backItem = UIBarButtonItem()
            backItem.tintColor = primaryColor
            navigationItem.backBarButtonItem = backItem
        } else if (segue.identifier == "settingSegue"){
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            backItem.tintColor = primaryColor
            navigationItem.backBarButtonItem = backItem
        }
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
    
}

