//
//  FriendRequest.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class FriendRequest: NSObject, Codable {
    var sender: String!
    var recipient: String!
    var _id: Int!
    var relationship: Int!
    var sent_on: String!
    
    init(dictionary: NSDictionary) {
        self.sender = dictionary["targetId"] as? String
    }
    
    override init(){
        self.sender = "admin"
        self.recipient = "teacher"
        self._id = 5
    }
    
    func respondFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> ()) {
        backEndClient.createFriendRequest(success: {
            success()
        }, failure: { (error) in
            failure(error)
        }, friendRequest: self)
    }
    
}
