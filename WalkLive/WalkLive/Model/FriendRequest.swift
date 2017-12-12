//
//  FriendRequest.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

struct FriendRequest: Codable {
    var sender: String?
    var recipient: String?
    var _id: Int?
    var relationship: Int?
    var sent_on: String?
    
}
