//
//  UserLogin.swift
//  Walklive
//
//  Created by Yang Cao on 11/30/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct UserLogin: Codable {
    var username: String
    var password: String
    static func endpointForUserLogin() -> String {
        return "https://localhost:8080/users/login"
    }
}
