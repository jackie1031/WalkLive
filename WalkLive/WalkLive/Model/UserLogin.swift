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
    
    // For NSKeyedArchiver encoding and decoding
    struct Keys {
        static let username = "username"
        static let password = "password"
    }
    
    /*
     Used when the user logs in
    */
    init(username : String, password : String) {
        self.username = username
        self.password = password
    }
    
    /*
     Used when the user signs up
    */
    init(username : String, password : String, contact : String) {
        self.username = username
        self.password = password
        self.contact = contact
    }
    
    /*
     General initialization, by passing in an NSDictionary containing user info
    */
    init(dictionary: NSDictionary) {
        self.username = dictionary["username"] as? String
        self.contact = dictionary["contact"] as? String
        self.emergency_id = dictionary["emergencyId"] as? String
        self.emergency_number = dictionary["emergencyNumber"] as? String
    }
    
    /*
     Encode UserLogin object in order for selected info to be stored locally
     In this case, only username and password is stored to ensure successful
     automatic login (if the user didn't log out the last time they used the
     app)
    */
    func encode(with aCoder: NSCoder) {
        aCoder.encode(self.username, forKey: Keys.username)
        aCoder.encode(self.password, forKey: Keys.password)
    }
    
    /*
     Required init function for NSKeyedArchiver to decode locally stored data
    */
    required init?(coder aDecoder: NSCoder) {
        if let decodedUsername = aDecoder.decodeObject(forKey: Keys.username) as? String {
            self.username = decodedUsername
        }
        if let decodedPassword = aDecoder.decodeObject(forKey: Keys.password) as? String {
            self.password = decodedPassword
        }
    }

}
