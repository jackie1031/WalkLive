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
        backEndClient.getUsers(success: { (users) in
            
        }) { (error) in
            
        }
        
    }
    
    
    /// Private functions
    
    
    /*
     Checks if username is valid, if passwords match, if password is valid, and if phone
     number is valid.
     - Return: Bool indicating whether sign up info is valid
     */
    private func isValidSignUp() -> Bool {
        if (!validUser()) {
            let errorView = warnigSignFactory.makeSignUpValidityWarningSign(status: 0)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        if (self.passwordTextField.text != self.confirmPasswordTextField.text) {
            let errorView = warnigSignFactory.makeSignUpValidityWarningSign(status: 1)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        if (self.passwordTextField.text!.count <= 7) {
            let errorView = warnigSignFactory.makeSignUpValidityWarningSign(status: 2)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        if (!validPhone()) {
            let errorView = warnigSignFactory.makeSignUpValidityWarningSign(status: 3)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        return true
    }
    
    /*
     Checks if phone number is valid.
     - Returns: bool indicating if phone number format is valid
     */
    private func validPhone() -> Bool {
        let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", PHONE_REGEX)
        return phoneTest.evaluate(with: self.phoneNumberTextField.text)
    }
    
    /*
     Checks if username is empty or contain illegal characters
     - Returns: bool indicating if username format is valid
     */
    private func validUser() -> Bool {
        if (self.userNameTextField.text == "") {
            return false
        }
        let characterset = CharacterSet(charactersIn: "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
        if self.userNameTextField.text!.rangeOfCharacter(from: characterset.inverted) != nil {
            return false
        }
        return true
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
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     If user cancels signup, brings the user back to login/signup view
     */
    @IBAction func onCancelButton(_ sender: Any) {
        self.dismiss(animated: false, completion: nil)
    }
    
    /*
     Create new account for the user if user info is valid.
     */
    @IBAction func onSignUpButton(_ sender: Any) {
        if (!isValidSignUp()) {
            return
        }
        let userSignUp = UserLogin(username: userNameTextField.text!, password: passwordTextField.text!, contact: phoneNumberTextField.text!)
        backEndClient.signUpAttempt(success: {(userinfo) in
            OperationQueue.main.addOperation {
                self.performSegue(withIdentifier: "signupSegue", sender: nil)
            }
        }, failure: { (error) in
            OperationQueue.main.addOperation {
                let errorView = warnigSignFactory.makeSignUpBackEndWarningSign(signupError: error)
                errorView.center = self.view.center
                self.view.addSubview(errorView)
            }
        }, userLogin: userSignUp)
    }
    
    
    /*
     hides keyboard when user finish editing and tap on other places on the screen
     - Parameters:
     - recoginizer: object to recognize motion when user tap on the screen
     */
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
