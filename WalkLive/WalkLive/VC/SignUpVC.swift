//
//  SignUpVC.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SignUpVC: UIViewController {

    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var confirmPasswordTextField: UITextField!
    @IBOutlet weak var phoneNumberTextField: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setKeyboard()
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onCancelButton(_ sender: Any) {
        self.dismiss(animated: false, completion: nil)
    }
    
    @IBAction func onSignUpButton(_ sender: Any) {
        if (!isValidSignUp()) {
            return
        }

        signUpAttempt(success: {
            self.performSegue(withIdentifier: "signUpToMainMapSegue", sender: nil) //?
        }, failure: { (error) in
            print(error)
        })
    }
    
    func signUpAttempt(success: @escaping () -> (), failure: @escaping (Error) -> ()) {
        let endpoint = "."
        guard let url = URL(string: endpoint) else {
            print("Error: cannot create URL")
            let error = BackendError.urlError(reason: "Could not construct URL")
            failure(error!)
        }
        var userLoginUrlRequest = URLRequest(url: url)
        userLoginUrlRequest.httpMethod = "POST"
        
        let keys = ["userId", "password", "selfContact"]
        let values = [userNameTextField.text, passwordTextField.text, phoneNumberTextField.text]
        var userDict = NSDictionary.init(objects: keys, forKeys: values)
        let user = User(userDict)
        
        let encoder = JSONEncoder()
        do {
            let newUserSignUpAsJSON = try encoder.encode(user)
            userUrlRequest.httpBody = newUserSignUpAsJSON
        } catch {
            failure(error)
        }
    
        URLSession.shared.dataTask(with: url, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            if let httpResponse = response as? HTTPURLResponse { //?
                print("status code: \(httpResponse.statusCode)")
                failure(error!)
            }
            // if success, log in
            success()
        }).resume()
    }
    
    func isValidSignUp() -> Bool {
        if (self.userNameTextField.text == "") {
            return false
        }
        if (self.passwordTextField.text != self.confirmPasswordTextField.text) {
            return false
        }
        if (self.passwordTextField.text!.count <= 7) {
            return false
        }
        if (!validPhone()) {
            return false
        }
        return true
    }
    
    func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        return phoneTest.evaluate(with: self.phoneNumberTextField.text)
    }
    
    private func setKeyboard(){
        let hideTap = UITapGestureRecognizer(target: self, action: #selector(MainMapVC.hideKeyboardTap(_:)))
        hideTap.numberOfTapsRequired = 1
        self.view.isUserInteractionEnabled = true
        self.view.addGestureRecognizer(hideTap)
    }
    
    //Public functions
    
    /// hides keyboard when user finish editing and tap on other places on the screen
    ///
    /// - Parameters:
    ///   - recoginizer: object to recognize motion when user tap on the screen
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.view.endEditing(true)
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
