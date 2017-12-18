//
//  WarningSignMaker.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class WarningSignFactory: NSObject{
    
    /*
     Makes generic warning sign
     */
    private func makeGenericWarningSign() -> WarningSignView {
        let errorView = WarningSignView()
        return errorView.loadNib()
    }
    
    /*
     Makes generic success sign
     */
    private func makeGenericSuccessSign() -> WarningSignView {
        var successView = self.makeGenericWarningSign()
        successView = successView.loadNib()
        successView.headerLabel.text = "Yes!"
        return successView
    }
    
    /*
     Makes Signup input warning sign
     */
    func makeSignUpValidityWarningSign(status: Int) -> WarningSignView{
        let errorView = self.makeGenericWarningSign()
        if (status == 0) {
            errorView.warningContentView.text = "Please enter a valid username😮"
        }
        if (status == 1) {
            errorView.warningContentView.text = "Password is inconsistent😮"
        }
        if (status == 2) {
            errorView.warningContentView.text = "Password needs to be at least 8 digits😮"
        }
        if (status == 3) {
            errorView.warningContentView.text = "Phone number needs to be format: xxx-xxx-xxxx😮"
        }
        return errorView
    }
    
    /*
     Makes signup backend warning sign
     */
    func makeSignUpBackEndWarningSign(signupError: SignUpError) -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = signupError.returnReason()
        return errorView
    }
    
    /*
     Makes login input warning sign
     */
    func makeLoginValidityWarningSign(status: Int) -> WarningSignView{
        let errorView = self.makeGenericWarningSign()
        if (status == 0) {
            errorView.warningContentView.text = "Please enter a valid username😮"
        }
        if (status == 1){
            errorView.warningContentView.text = "Please enter a valid password😮"
        }
        return errorView
    }
    
    /*
     Makes login backend warning sign
     */
    func makeLoginBackEndWarningSign(loginError: LoginError) -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = loginError.returnReason()
        return errorView
    }
    
    /*
     Makes auto login warning sign
     */
    func makeAutoLoginWarningSign(loginError: LoginError) -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = "Auto login failed."
        return errorView
    }
    
    /*
     Makes save message warning sign
     */
    func saveMessageWarningSign(status: Int) -> WarningSignView {
        let view = self.makeGenericWarningSign()
        if (status == 0) {
            view.warningContentView.text = "There should be at least 4 message segments."
        }
        if (status == 1) {
            view.warningContentView.text = "There should be at most 8 message segments."
        }
        return view
    }
    
    /*
     Makes save success sign
     */
    func makeSaveSuccessSign() -> WarningSignView {
        var successView = self.makeGenericWarningSign()
        successView = successView.loadNib()
        successView.headerLabel.text = "Settings Saved!"
        successView.warningContentView.text = "You can keep editing or press \"Settings\" to leave."
        return successView
    }
    
    /*
     Makes save settings warning sign
     */
    func makeSaveSettingsSuccessWarningSign() -> WarningSignView {
        let successView = self.makeGenericSuccessSign()
        successView.warningContentView.text = "Success!👊"
        return successView
    }
    
    /*
     Makes build route warning sign
     */
    func cannotBuildRouteWarningSign() -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = "The place you chose is too far to walk to 😢"
        return errorView
    }
}
