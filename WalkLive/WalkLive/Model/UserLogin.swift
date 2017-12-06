//
//  UserLogin.swift
//  WalkLive
//
//  Created by Yang Cao on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct UserLogin: Codable {
    var username: String
    var password: String
    var phoneNum: String
    init(username : String, password : String) {
        self.username = username as! String
        self.password = password as! String
    }
    
    init(username : String, password : String, phoneNum : String) {
        self.username = username as! String
        self.password = password as! String
        self.phoneNum = phoneNum as! String
    }
//    static func endpointForUserLogin() -> String {
//        return "https://localhost:8080/users/login"
//    }
}
