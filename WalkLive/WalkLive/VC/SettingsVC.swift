//
//  SettingsVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SettingsVC: UITableViewController {

    //filePath for storing and loading saved message data
    var filePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("Data").path)
    }
    
    private var defaultMessage : Array<String> {
        var messageSegments = Array<String>()
        messageSegments.append("Hello, I'm currently at:")
        messageSegments.append("Coordinate")
        messageSegments.append("Call me at:")
        messageSegments.append("Phone")
        return messageSegments
    }
    
    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    @IBOutlet weak var textLabel: UILabel!
    @IBOutlet weak var emergencyContactIdTextField: UITextField!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    
    let WITHOUT_TRIP = 0
    let WITH_TRIP = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setKeyboard()
        
        //load message setting data from local
        self.loadData()

        
        // fill in phone numbers
        userPhone.text = currentUserInfo.contact
        if let emergencyContact = currentUserInfo.emergency_number {
            emergencyContactPhone.text = emergencyContact
        }
        
        // if no local data has been saved, set to default
        if (messages.getMessagesWithoutTrip().count == 0) {
            messages.updateMessagesWithoutTrip(updatedMessages: defaultMessage)
        }
        if (messages.getMessagesWithTrip().count == 0) {
            messages.updateMessagesWithTrip(updatedMessages: defaultMessage)
        }
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
    
    
    /// Private functions
    
    
    /*
     Checks if emergency contact info is empty
     Returns: boolean indicating whether emergency contact info is empty
     */
    private func emergencyIsDifferent() -> Bool {
        return (emergencyContactPhone.text != "" ||
            emergencyContactIdTextField.text != "" )
    }
    
    /*
     Loads message setting from local directory
     */
    private func loadData() {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            messages = data
        }
    }
    
    /*
     Path for locally saved message setting
     */
    private func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    /*
     Checks if phone number/syntax is correct
     - Returns: boolean indicating whether phone number format is valid
     */
    private func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        let userPhoneResult =  phoneTest.evaluate(with: self.userPhone.text)
        let emergencyPhoneResult = phoneTest.evaluate(with: self.emergencyContactPhone.text)
        return (userPhoneResult && emergencyPhoneResult)
    }
    
    /*
     Refreshes text preview label
     */
    private func refreshTextLabel() {
        if (segmentedControl.selectedSegmentIndex == WITHOUT_TRIP) {
            textLabel.text = messages.buildMessageWithoutTrip()
        } else if (segmentedControl.selectedSegmentIndex == WITH_TRIP) {
            textLabel.text = messages.buildMessageWithTripPreview()
        }
    }
    
    /*
     Sets keyboard
     */
    private func setKeyboard(){
        let hideTap = UITapGestureRecognizer(target: self, action: #selector(MainMapVC.hideKeyboardTap(_:)))
        hideTap.numberOfTapsRequired = 1
        self.view.isUserInteractionEnabled = true
        self.view.addGestureRecognizer(hideTap)
    }
    
    
    /// Public functions
    
    
    
    /*
     This will save phone number settings, but not message setting, since message setting is
     already saved in MessageVC
     */
    @IBAction func onSaveButton(_ sender: Any) {
        if (emergencyIsDifferent()){
            let emergencyContact = EmergencyContact(emergency_id: emergencyContactIdTextField.text!, emergency_number: emergencyContactPhone.text!)
            backEndClient.updateEmergencyContact(success: { (updatedEmergencyContact) in
                 OperationQueue.main.addOperation {
                    let successView = warnigSignFactory.makeSaveSettingsSuccessWarningSign()
                successView.center = self.view.center
                self.view.addSubview(successView)
                }
            }, failure: { (error) in
            }, emergencyContact: emergencyContact)
        }
    }
    
    /*
     Refreshes label when user selects a message to view
     */
    @IBAction func onSegmentedControl(_ sender: Any) {
        refreshTextLabel()
    }
    
    /*
     Passes messages to MessageVC
     */
    @IBAction func onEditButton(_ sender: Any) {
        self.performSegue(withIdentifier: "messageSegue", sender: nil)
    }
    
    /*
     Hides keyboard
     */
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }

    /*
     Sets bar button.
     */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let backItem = UIBarButtonItem()
        backItem.title = "Setting"
        backItem.tintColor = primaryColor
        navigationItem.backBarButtonItem = backItem
    }

}
