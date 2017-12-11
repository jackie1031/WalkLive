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
    
    
}
