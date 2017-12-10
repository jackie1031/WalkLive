//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class Message: NSObject, NSCoding {
    
    var messages : Array<String>
    private var _name = ""

    struct Keys {
        static let name = "message"
    }
    
    override init() {
        self.messages = Array<String>()
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(_name, forKey: Keys.name)
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.messages = Array<String>()
        if let nameObject = aDecoder.decodeObject(forKey: Keys.name) as? String {
            _name = nameObject
        }
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
    
//    var name: String {
//        get {
//            return _name
//        }
//        set {
//            _name = newValue
//        }
//    }
    
}
