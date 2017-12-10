//
//  UserLogin.swift
//  WalkLive
//
//  Created by Yang Cao on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct UserLogin: Codable {
    var username: String?
    var password: String?
    var contact: String?
    var emergency_id: String?
    var emergency_number: String?
    
    init(username : String, password : String) {
        self.username = username
        self.password = password
    }
    
    init(username : String, password : String, contact : String) {
        self.username = username
        self.password = password
        self.contact = contact
    }
    
    init(dictionary: NSDictionary) {
        self.username = dictionary["username"] as? String
        self.contact = dictionary["contact"] as? String
        self.emergency_id = dictionary["emergencyId"] as? String
        self.emergency_number = dictionary["emergencyNumber"] as? String
    }

}
