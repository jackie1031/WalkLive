//
//  EmergencyContact.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class EmergencyContact: NSObject, Codable{
    var emergency_id: String?
    var emergency_number: String?
    init(dictionary: NSDictionary) {
        self.emergency_id = dictionary["emergencyId"] as? String
        self.emergency_number = dictionary["emergencyNumber"] as? String
    }
    
    override init(){
        self.emergency_id = ""
        self.emergency_number = ""
    }
    
    init(emergency_id: String, emergency_number: String){
        self.emergency_id = emergency_id
        self.emergency_number = emergency_number
    }
    
}
