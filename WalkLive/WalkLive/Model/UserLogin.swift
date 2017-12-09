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
    var emergencyId: String?
    var emergencyNumber: String?
    
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
        self.emergencyId = dictionary["emergencyId"] as? String
        self.emergencyNumber = dictionary["emergencyNumber"] as? String
    }

}
