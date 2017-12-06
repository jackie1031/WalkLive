//
//  User.swift
//  Walklive
//
//  Created by Yang Cao on 10/14/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class User: NSObject {
    var userId: Int?
    var name: String?
    var userName: String?
    var selfContact: String?
    var emergencyNumber: String?
    var password: String?
    var email: String? //how to use facebook API to get their email in AppDelegate
    //var friendList:
    var dictionary: NSDictionary?
    
    init(dictionary: NSDictionary) {
        self.dictionary = dictionary
        userId = dictionary["userId"] as? Int
        name = dictionary["name"] as? String
        password = dictionary["password"] as? String
        userName = dictionary["user_name"] as? String
        selfContact = dictionary["phone_number"] as? String
        //email
    }
    
    static var _currentUser: User?
    class var currentUser: User? {
        get {
            if _currentUser == nil {
                let defaults = UserDefaults.standard
                let userData = defaults.object(forKey: "currentUserData") as? Data
            
                if let userData = userData {
                    let dictionary = try! JSONSerialization.jsonObject(with: userData, options: []) as! NSDictionary
                    _currentUser = User(dictionary: dictionary)
                }
            }
            return _currentUser
        }
        
        set (user) {
            _currentUser = user
            let defaults = UserDefaults.standard
            print("reach set user")
            if let user = user {
                let data = try! JSONSerialization.data(withJSONObject: user.dictionary!)//, options [])
                defaults.set(data, forKey: "currentUserData")
            } else {
                defaults.removeObject(forKey: "currentUserData")
            }
        }
        
    }
}
