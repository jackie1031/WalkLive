//
//  User.swift
//  Walklive
//
//  Created by Yang Cao on 10/14/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class User: NSObject {
    var name: NNString?
    var userName: NNString?
    var dictionary: NSDictionary?
    var email: NNString? //how to use facebook API to get their email
    
    init(dictionary: NSDictionary) {
        self.dictionary = dictionary
        name = dictionary["name"] as? String as NSString?
        userName = dictionary["user_name"] as? String as NSString?
        
        
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
        }
        
        set (user) {
            _currentUser = user
            let defaults = UserDefaults.standard
            print("reach set user")
            if let user = user {
                let data = try! JSONSerialization.data(withJSONObject: user.dictionary!, options [])
            }
        }
        
    }
}
