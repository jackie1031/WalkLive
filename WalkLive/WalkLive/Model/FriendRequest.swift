//
//  FriendRequest.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class FriendRequest: NSObject {
    var targetId: String!
    var requestId: String!
    init(dictionary: NSDictionary) {
        self.targetId = dictionary["targetId"] as? String
    }
    
    func submitRequest(){
        if (User.currentUser != nil){
        }
    }
    
}
