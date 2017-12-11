//
//  SettingsVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import CoreLocation

class SettingsVC: UITableViewController, MessageVCDelegate {

    var filePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("Data").path)
    }
    
    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    @IBOutlet weak var textLabel: UILabel!
    @IBOutlet weak var emergencyContactIdTextField: UITextField!
    
    var user : User!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.setKeyboard()
        
        //load message setting data from local
        self.loadData()
        
        // refresh text label
        self.refreshTextLabel()
        
        // fill in phone numbers
        userPhone.text = currentUserInfo.contact
        if let emergencyContact = currentUserInfo.emergency_number {
            emergencyContactPhone.text = emergencyContact
        }
        
        // if no local data has been saved, set to default
        if (messages.getMessages().count == 0) {
            var messageSegments = Array<String>()
            messageSegments.append("Hello, I'm currently at:")
            messageSegments.append("Coordinate")
            messageSegments.append("Call me at:")
            messageSegments.append("Phone")
            messages.updateMessages(updatedMessages: messageSegments)
        }
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
        refreshTextLabel()
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    // Pass messages to MessageVC
    @IBAction func onEditButton(_ sender: Any) {
        self.performSegue(withIdentifier: "messageSegue", sender: nil)
    }
    
    // now MessageVC receives message setting and updates it variable unsavedMessages
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? MessageVC {
            let passingMessages : Message = Message(messageSegments: messages.getMessages())
            destinationVC.unsavedMessages = passingMessages
            destinationVC.delegate = self as MessageVCDelegate
        }
    }
    
    // updating global variable messages as the newly updated message setting from MessageVC
    func messagesSaved(unsavedMessages: Message?) {
        messages = unsavedMessages
    }
    
    // save message setting locally
    private func saveData() {
        NSKeyedArchiver.archiveRootObject(messages, toFile: filePath)
    }
    
    // load message setting from local directory
    private func loadData() {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            messages = data
        } else {
            messages = Message()
        }
    }
    
    // path for locally saved message setting
    private func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    // check if phone number/syntax is correct
    private func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        let userPhoneResult =  phoneTest.evaluate(with: self.userPhone.text)
        let emergencyPhoneResult = phoneTest.evaluate(with: self.emergencyContactPhone.text)
        return (userPhoneResult && emergencyPhoneResult)
    }
    
    private func refreshTextLabel() {
        var text : String = ""
        for message in messages.getMessages() {
            var realValue : String
            if (message == "Phone") {
                realValue = currentUserInfo.contact!
            } else if (message == "Coordinate") {
                let location = CLLocationManager().location?.coordinate
                let longtitude = String(format: "%.2f", (location?.longitude)!)
                let latitude = String(format: "%.2f", (location?.latitude)!)
                realValue = "(" + longtitude + ", " + latitude + ")"
            } else {
                realValue = message
            }
            text.append(realValue)
            text.append(" ")
        }
        textLabel.text = text
    }
    
    private func createAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in
            NSLog("The \"OK\" alert occured.")
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    private func setKeyboard(){
        let hideTap = UITapGestureRecognizer(target: self, action: #selector(MainMapVC.hideKeyboardTap(_:)))
        hideTap.numberOfTapsRequired = 1
        self.view.isUserInteractionEnabled = true
        self.view.addGestureRecognizer(hideTap)
    }
    
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.view.endEditing(true)
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
