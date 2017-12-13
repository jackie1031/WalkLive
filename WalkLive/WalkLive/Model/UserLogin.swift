//
//  UserLogin.swift
//  WalkLive
//
//  Created by Yang Cao on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class UserLogin: NSObject, Codable, NSCoding {
    var username: String?
    var password: String?
    var contact: String?
    var emergency_id: String?
    var emergency_number: String?
    var created_on: String?
    
    // should only need to store username and password to log user in
    struct Keys {
        static let username = "username"
        static let password = "password"
    }
    
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
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(self.username, forKey: Keys.username)
        aCoder.encode(self.password, forKey: Keys.password)
    }
    
    required init?(coder aDecoder: NSCoder) {
        if let decodedUsername = aDecoder.decodeObject(forKey: Keys.username) as? String {
            self.username = decodedUsername
        }
        if let decodedPassword = aDecoder.decodeObject(forKey: Keys.password) as? String {
            self.password = decodedPassword
        }
    }

}
