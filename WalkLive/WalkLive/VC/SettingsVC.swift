//
//  SettingsVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SettingsVC: UITableViewController, MessageVCDelegate {

    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    
    var messages : Message!
    var fullPath : URL?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if (messages == nil) {
            if let loadedData = NSKeyedUnarchiver.unarchiveObject(withFile: (fullPath?.absoluteString)!) as? Message {
                self.messages = loadedData
            }
            if (messages.getMessages().count == 0) {
                var messageSegments = Array<String>()
                messageSegments.append("Hello, I'm currently at:")
                messageSegments.append("Coordinate")
                messageSegments.append("Call me at:")
                messageSegments.append("Phone")
                self.messages.updateMessages(updatedMessages: messageSegments)
                //self.messages = Message(messageSegments: messageSegments)
            }
        }
        // Do any additional setup after loading the view.
        // need to load user phone number
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onSaveButton(_ sender: Any) { //How to get current user?
        if (!validPhone()) {
            return
        }
        backEndClient.saveAttempt(success: {
            self.performSegue(withIdentifier: "saveSettingToProfileSegue", sender: nil) //?
        }, failure: { (error) in
            print(error)
        }, phoneNum: userPhone.text!, emergencyContact: emergencyContactPhone.text!)
        // save message locally
        let savedMessageFile = UUID().uuidString
        let data = NSKeyedArchiver.archivedData(withRootObject: self.messages)
        let fullPath = getDocumentsDirectory().appendingPathComponent(savedMessageFile)
        do {
            try data.write(to: fullPath)
        } catch {
            print("Couldn't write file")
        }
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        let userPhoneResult =  phoneTest.evaluate(with: self.userPhone.text)
        let emergencyPhoneResult = phoneTest.evaluate(with: self.emergencyContactPhone.text)
        return (userPhoneResult && emergencyPhoneResult)
    }
    
    
    // Pass messages to MessageVC!!!
    @IBAction func onEditButton(_ sender: Any) {
        self.performSegue(withIdentifier: "messageSegue", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? MessageVC {
            destinationVC.messages = self.messages
            destinationVC.delegate = self as MessageVCDelegate
        }
    }
    
    func messagesSaved(messages: Message?) {
        self.messages = messages
        print("messages saved.")
        for message in self.messages.getMessages() {
            print(message)
        }
    }
    
//    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
//        // Create a new variable to store the instance of PlayerTableViewController
//        if let destinationVC = segue.destination as? MessageVC {
//            destinationVC.messages = messages
//        }
//    }
    
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
