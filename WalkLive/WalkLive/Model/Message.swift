//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import CoreLocation

class Message: NSObject, NSCoding {
    
    var messageSegments : Array<String>

    struct Keys {
        static let messageSegs = "messageSegments"
    }
    
    override init() {
        self.messageSegments = Array<String>()
        self.messageSegments.append("Hello I'm currently at:")
        self.messageSegments.append("Coordinate")
        self.messageSegments.append("Call me at:")
        self.messageSegments.append("Phone")
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
    
    func buildMessage() -> String {
        var text : String = ""
        for message in messageSegments {
            var realValue : String
            if (message == "Phone") {
                realValue = currentUserInfo.contact!
            } else if (message == "Coordinate") {
                let location = CLLocationManager().location?.coordinate
                let longtitude = String(format: "%.2f", (location?.longitude)!)
                let latitude = String(format: "%.2f", (location?.latitude)!)
                realValue = "(" + longtitude + ", " + latitude + ")"
            } else {
                realValue = message
            }
            text.append(realValue)
            text.append(" ")
        }
        return text
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
