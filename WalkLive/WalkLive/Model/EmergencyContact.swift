//
//  EmergencyContact.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class EmergencyContact: NSObject, Codable{
    var emergencyId: String?
    var emergencyNumber: String?
    init(dictionary: NSDictionary) {
        self.emergencyId = dictionary["emergencyId"] as? String
        self.emergencyNumber = dictionary["emergencyNumber"] as? String
    }
    
    override init(){
        self.emergencyId = ""
        self.emergencyNumber = ""
    }
    
    init(emergencyId: String, emergencyNumber: String){
        self.emergencyId = emergencyId
        self.emergencyNumber = emergencyNumber
    }
    
}
