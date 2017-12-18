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
    
    /*
     Builds info about emergency number
     - Returns: string containing emergency number info
     */
    func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil || currentUserInfo?.emergency_number == "") {
            return "Emer. Num: None"
        }
        return (currentUserInfo?.emergency_number)!
    }
    
    /*
     Builds info about emergency ID
     - Returns: string containing emergency ID info
     */
    func emerIdStringBuilder() -> String{
        if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
            return "Emer. Id: None"
        }
        return (currentUserInfo?.emergency_id)!
    }
    
    /*
     Builds info about emergency contact number of user
     - Returns: string containing emergency contact number info
     */
    func emerStringBuilderWithUser() -> String{
        if (currentUserInfo?.emergency_number == nil || currentUserInfo?.emergency_number == "") {
            return "Emer. Num: None"
        }
        return "Emer. Num: " + (currentUserInfo?.emergency_number)!
    }
    
    /*
     Builds info about emergency contact ID of user
     - Returns: string containing emergency contact ID info
     */
    func emerIdStringBuilderWithUser() -> String{
        if (currentUserInfo?.emergency_id == nil || currentUserInfo?.emergency_id == "") {
            return "Emer. Id: None"
        }
        return "Emer. Contact: " + (currentUserInfo?.emergency_id)!
    }
}
