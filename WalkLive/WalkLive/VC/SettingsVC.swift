//
//  SettingsVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SettingsVC: UITableViewController, MessageVCDelegate {

    var filePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("Data").path)
    }
    
    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    
    var fullPath : URL?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //load message setting data from local
        loadData()
        
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func onSaveButton(_ sender: Any) {
        if (!validPhone()) {
            return
        }
        backEndClient.saveAttempt(success: {
            //save message setting locally
            self.saveData()
            self.performSegue(withIdentifier: "saveSettingToProfileSegue", sender: nil)
        }, failure: { (error) in
            print(error)
        }, phoneNum: userPhone.text!, emergencyContact: emergencyContactPhone.text!)
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
        print("saving data locally")
        NSKeyedArchiver.archiveRootObject(messages, toFile: filePath)
    }
    
    // load message setting from local directory
    private func loadData() {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            messages = data
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
