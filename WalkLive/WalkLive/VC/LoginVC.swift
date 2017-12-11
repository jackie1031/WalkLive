//
//  LoginVC.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class LoginVC: UIViewController{

    
    
    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setKeyboard()
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onLoginButton(_ sender: Any) {
        let userLogin = UserLogin(username: userNameTextField.text!, password: passwordTextField.text!)
        backEndClient.loginAttempt(success: { (userInfo) in
            OperationQueue.main.addOperation {
                self.performSegue(withIdentifier: "loginSegue", sender: nil)
            }
        }, failure: { (error) in
            OperationQueue.main.addOperation {
                let errorSign = warnigSignFactory.makeLoginBackEndWarningSign(loginError: error)
                errorSign.center = self.view.center
                self.view.addSubview(errorSign)
            }
        }, userLogin: userLogin)
    }
    
    private func isValidLogin() -> Bool{
        if (self.userNameTextField.text == "") {
            let errorView = warnigSignFactory.makeLoginValidityWarningSign(status: 0)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        if (self.passwordTextField.text == ""){
            let errorView = warnigSignFactory.makeLoginValidityWarningSign(status: 1)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return false
        }
        return true
    }

    @IBAction func onCancelButton(_ sender: Any) {
        self.dismiss(animated: false, completion: nil)
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
