//
//  LoginVC.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class LoginVC: UIViewController, FBSDKLoginButtonDelegate  {

    
    
    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    
    @IBOutlet weak var facebookLoginButton: FBSDKLoginButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    // these 2 functions are new!!!
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        print("Did log out of facebook")
    }
    
    func loginButton(_ facebookloginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        if error != nil {
            print(error)
            return
        }
        
        print("Successfully logged in with facebook...")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onLoginButton(_ sender: Any) {
        if (self.userNameTextField.text == "") {
            return
        }
//        loginAttempt(success: {
//            self.performSegue(withIdentifier: "loginToMainMapSegue", sender: nil) //?
//        }, failure: { (error) in
//            print(error)
//        })
    }
    
//    func loginAttempt(success: @escaping () -> (), failure: @escaping (Error) -> ()) {
//        let endpoint = "."
//        guard let url = URL(string: endpoint) else {
//            print("Error: cannot create URL")
////            let error = BackendError.urlError(reason: "Could not construct URL")
////            failure(error!)
//        }
//        var userLoginUrlRequest = URLRequest(url: url)
//        userLoginUrlRequest.httpMethod = "POST"
//
//        let userLogin = UserLogin(username: userNameTextField.text!, password: passwordTextField.text!)
//        let encoder = JSONEncoder()
//        do {
//            let newUserLoginAsJSON = try encoder.encode(userLogin)
//            userLoginUrlRequest.httpBody = newUserLoginAsJSON
//        } catch {
//            failure(error)
//        }
//
    
//        URLSession.shared.dataTask(with: url, completionHandler: { //?
//            (data, response, error) in
//            // check for errors
//            if error != nil {
//                failure(error!)
//            }
//            if let httpResponse = response as? HTTPURLResponse { //?
//                print("status code: \(httpResponse.statusCode)")
//                failure(error!)
//            }
//            // if success, log in
//            success()
//        }).resume()
//    }
    
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
