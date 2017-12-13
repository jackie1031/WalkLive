//
//  ViewController.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FirstViewVC: UIViewController {
    
    private var userData : UserLogin?
    
    private var userFilePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("UserInfo").path)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        do {
            try FileManager.default.removeItem(atPath: userFilePath)
        } catch {
            print("No data found.")
        }
        //Do any additional setup after loading the view, typically from a nib.
        if (self.loadUserData()) {
            backEndClient.loginAttempt(success: { (userInfo) in
                print("hiiiii")
                OperationQueue.main.addOperation {
                    //TODO
                    self.performSegue(withIdentifier: "directLoginSegue", sender: nil)
                }
            }, failure: { (error) in
                OperationQueue.main.addOperation {
                    let errorSign = warnigSignFactory.makeLoginBackEndWarningSign(loginError: error)
                    errorSign.center = self.view.center
                    self.view.addSubview(errorSign)
                }
            }, userLogin: userData!)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // load user data when first opens app
    private func loadUserData() -> Bool {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: userFilePath) as? UserLogin {
            print("local data found:")
            print(data.username)
            print(data.password)
            self.userData = data
            //self.userData = UserLogin(username: data.username!, password: data.password!)
            return true
        } else {
            return false
        }
    }

}

