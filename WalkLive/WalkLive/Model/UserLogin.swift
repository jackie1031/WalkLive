//
//  UserLogin.swift
//  Walklive
//
//  Created by Yang Cao on 12/4/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct UserLogin: Codable {
    var userId: String
    var password: String
    static func endpointForUserLogin() -> String {
        return "https://localhost:8080/users/login"
    }
}
