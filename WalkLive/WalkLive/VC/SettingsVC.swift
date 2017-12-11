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
        
        //load data from local
        loadData()
        
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
    
    @IBAction func onSaveButton(_ sender: Any) { //How to get current user?
        if (!validPhone()) {
            return
        }
        backEndClient.saveAttempt(success: {
            self.performSegue(withIdentifier: "saveSettingToProfileSegue", sender: nil) //?
        }, failure: { (error) in
            print(error)
        }, phoneNum: userPhone.text!, emergencyContact: emergencyContactPhone.text!)
        //save locally
        saveData()
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
            destinationVC.unsavedMessages = messages
            destinationVC.delegate = self as MessageVCDelegate
        }
    }
    
    func messagesSaved(unsavedMessages: Message?) {
        messages = unsavedMessages
    }
    
    private func saveData() {
        print("saving data locally")
        NSKeyedArchiver.archiveRootObject(messages, toFile: filePath)
    }
    
    // when do we call this function?
    private func loadData() {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            print("local data exists:")
            messages = data
            for mes in messages.getMessages() {
                print(mes)
            }
        }
    }
    
    private func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
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
