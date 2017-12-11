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
    var requestId: String!
    
    init(dictionary: NSDictionary) {
        self.sender = dictionary["targetId"] as? String
    }
    
    override init(){
        self.sender = "admin"
        self.recipient = "teacher"
        self.requestId = "admin"
    }
    
    func respondFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> ()) {
        backEndClient.makeFriendRequest(success: {
            success()
        }, failure: { (error) in
            failure(error)
        }, friendRequest: self)
    }
    
}
