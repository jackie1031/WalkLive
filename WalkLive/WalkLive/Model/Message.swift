//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class Message: NSObject {
    var messages : Array<String>
    
    override init() {
        self.messages = Array()
    }
    
    func addMessage(m : String) {
        self.messages.append(m)
    }
    
    func deleteLastMessage() {
        self.messages.popLast()
    }
    
    func updateMessages(updatedMessages : Array<String>) {
        self.messages = updatedMessages
    }
    
}
