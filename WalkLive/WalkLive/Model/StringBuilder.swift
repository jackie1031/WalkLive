//
//  StringBuilder.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

// A string builder that sets up info about emergency contact name and number
class StringBuilder: NSObject {
    func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil || currentUserInfo?.emergency_number == "") {
            return "Emer. Num: None"
        }
        return (currentUserInfo?.emergency_number)!
    }
    
    func emerIdStringBuilder() -> String{
        if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
            return "Emer. Id: None"
        }
        return (currentUserInfo?.emergency_id)!
    }
    
    func emerStringBuilderWithUser() -> String{
        if (currentUserInfo?.emergency_number == nil || currentUserInfo?.emergency_number == "") {
            return "Emer. Num: None"
        }
        return "Emer. Num: " + (currentUserInfo?.emergency_number)!
    }
    
    func emerIdStringBuilderWithUser() -> String{
        if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
            return "Emer. Id: None"
        }
        return "Emer. Contact: " + (currentUserInfo?.emergency_id)!
    }
}
