//
//  UserLogin.swift
//  WalkLive
//
//  Created by Yang Cao on 12/6/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct UserLogin: Codable {
    var userId: String
    var password: String
    var phoneNum: String
    init(userId : String, password : String) {
        self.userId = userId as! String
        self.password = password as! String
    }
    
    init(userId : String, password : String, phoneNum : String) {
        self.userId = userId as! String
        self.password = password as! String
        self.phoneNum = phoneNum as! String
    }
//    static func endpointForUserLogin() -> String {
//        return "https://localhost:8080/users/login"
//    }
}
