//
//  SettingsVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SettingsVC: UITableViewController {

    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    @IBOutlet weak var emergencyContactIdTextField: UITextField!
    
    var user : User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    
    @IBAction func onSaveButton(_ sender: Any) {
        if (emergencyIsDifferent()){
            let emergencyContact = EmergencyContact(emergency_id: emergencyContactIdTextField.text!, emergency_number: emergencyContactPhone.text!)
            backEndClient.updateEmergencyContact(success: { (updatedEmergencyContact) in
                print("updated!")
            }, failure: { (error) in
                
            }, emergencyContact: emergencyContact)
        }
    }
    
    private func emergencyIsDifferent() -> Bool {
        return (emergencyContactPhone.text != "" ||
                emergencyContactIdTextField.text != "" )
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.emergencyContactPhone.placeholder = stringBuilder.emerStringBuilder()
        self.emergencyContactIdTextField.placeholder = stringBuilder.emerIdStringBuilder()
    }
    

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    
    func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        let userPhoneResult =  phoneTest.evaluate(with: self.userPhone.text)
        let emergencyPhoneResult = phoneTest.evaluate(with: self.emergencyContactPhone.text)
        return (userPhoneResult && emergencyPhoneResult)
    }
    
    @IBAction func onEditButton(_ sender: Any) {
        self.performSegue(withIdentifier: "messageSegue", sender: nil)
    }
    
    
//    @IBAction func onCancelButton(_ sender: Any) {
//        self.dismiss(animated: false, completion: nil)
//    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
