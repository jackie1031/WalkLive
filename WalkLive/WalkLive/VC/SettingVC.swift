//
//  SettingVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/30/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SettingVC: UIViewController {
    
    @IBOutlet weak var userPhone: UITextField!
    @IBOutlet weak var emergencyContactPhone: UITextField!
    
    var user : User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onSaveButton(_ sender: Any) { //How to get current user?
        if (!validPhone()){
            return
            }
            saveAttempt(success: {
                self.performSegue(withIdentifier: "saveSettingToProfileSegue", sender: nil) //?
            }, failure: { (error) in
                print(error)
            })
    }
    
    func saveAttempt(success: @escaping () -> (), failure: @escaping (Error) -> ()) {
        
        // need
        let keys = ["phoneNum", "emergencyContact"]
        let values = [userPhone, emergencyContactPhone]
        var userDict = NSDictionary.init(objects: keys, forKeys: values)
        self.user = User(userDict)

        let encoder = JSONEncoder()
        do {
            let newUserLoginAsJSON = try encoder.encode(userLogin)
            url.httpBody = newUserLoginAsJSON
        } catch {
            failure(error!)
        }

        URLSession.shared.dataTask(with: url, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            if let httpResponse = response as? HTTPURRLResponse { //?
                print("status code: \(httpResponse.statusCode)")
                failure(error!)
            }
            // if success, log in
            success()
        }).resume()
    }
    
    func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        let userPhoneResult =  phoneTest.evaluate(with: self.userPhone.text)
        let emergencyPhoneResult = phoneTest.evaluate(with: self.emergencyContactPhone.text)
        return (userPhoneResult && emergencyPhoneResult)
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
