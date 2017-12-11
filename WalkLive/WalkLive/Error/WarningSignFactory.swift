//
//  WarningSignMaker.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class WarningSignFactory: NSObject{
    
    private func makeGenericWarningSign() -> WarningSignView {
        let errorView = WarningSignView()
        return errorView.loadNib()
    }
    
    private func makeGenericSuccessSign() -> WarningSignView {
        var successView = self.makeGenericWarningSign()
        successView = successView.loadNib()
        successView.headerLabel.text = "Yes!"
        return successView
    }
    
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
    
    func makeSignUpBackEndWarningSign(signupError: SignUpError) -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = signupError.returnReason()
        return errorView
    }
    
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
    
    func makeLoginBackEndWarningSign(loginError: LoginError) -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = loginError.returnReason()
        return errorView
    }
    
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
    
    func makeSaveSuccessSign() -> WarningSignView {
        var successView = self.makeGenericWarningSign()
        successView = successView.loadNib()
        successView.headerLabel.text = "Settings Saved!"
        successView.warningContentView.text = "You can keep editing or press \"Settings\" to leave."
        return successView
    }
    
    func makeSaveSettingsSuccessWarningSign() -> WarningSignView {
        let successView = self.makeGenericSuccessSign()
        successView.warningContentView.text = "Success!👊"
        return successView
    }
    
    func cannotBuildRouteWarningSign() -> WarningSignView {
        let errorView = self.makeGenericWarningSign()
        errorView.warningContentView.text = "The place you chose is too far to walk to 😢"
        return errorView
    }
}
