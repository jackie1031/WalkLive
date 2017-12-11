//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class Message: NSObject, NSCoding {
    
    var messageSegments : Array<String>

    struct Keys {
        static let messageSegs = "messageSegments"
    }
    
    override init() {
        self.messageSegments = Array<String>()
    }
    
    init(messageSegments: Array<String>) {
        self.messageSegments = messageSegments
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(messageSegments, forKey: Keys.messageSegs)
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.messageSegments = Array<String>()
        if let decodedObject = aDecoder.decodeObject(forKey: Keys.messageSegs) as? Array<String> {
            messageSegments = decodedObject
        }
    }
    
    func addMessage(m : String) {
        self.messageSegments.append(m)
    }
    
    func deleteLastMessage() {
        self.messageSegments.popLast()
    }
    
    func getMessages() -> Array<String> {
        return self.messageSegments
    }
    
    func updateMessages(updatedMessages : Array<String>) {
        self.messageSegments = updatedMessages
    }
    
}
