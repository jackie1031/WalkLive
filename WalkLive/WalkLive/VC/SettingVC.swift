//
//  SettingVC.swift
//  Walklive
//
//  Created by Yang Cao on 12/2/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit


class SettingVC: UIViewController {
    
    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onSaveButton(_ sender: Any) {
        if (self.userPhone.text == "") {
            return
        }
        if (self.emergencyContactPhone.text == "") {
            return
        }
//        loginAttempt(success: {
//            self.performSegue(withIdentifier: "loginToMainMapSegue", sender: nil) //?
//        }, failure: { (error) in
//            print(error)
//        })
    }
    
    @IBAction func onCancelButton(_ sender: Any) {
        self.dismiss(animated: false, completion: nil)
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
