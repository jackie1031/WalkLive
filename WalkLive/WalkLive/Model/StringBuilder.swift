//
//  StringBuilder.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class StringBuilder: NSObject {
    func emerStringBuilder() -> String{
        if (currentUserInfo?.emergencyNumber == nil) {
            return "Emer. Num: None"
        }
        return (currentUserInfo?.emergencyNumber)!
    }
    
    func emerIdStringBuilder() -> String{
        if (currentUserInfo?.emergencyId == nil || currentUserInfo?.emergencyId == "") {
            return "Emer. Id: None"
        }
        return (currentUserInfo?.emergencyId)!
    }
}
