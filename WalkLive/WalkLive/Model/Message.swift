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
    
    private var messageSegmentsWithoutTrip : Array<String>
    private var messageSegmentsWithTrip: Array<String>

    struct Keys {
        static let messageSegsWithoutTrip = "messageSegmentsWithoutTrip"
        static let messageSegsWithTrip = "messageSegmentWithTrip"
    }
    
    override init() {
        self.messageSegmentsWithoutTrip = Array<String>()
        self.messageSegmentsWithoutTrip.append("Hello I'm currently at:")
        self.messageSegmentsWithoutTrip.append("Coordinate")
        self.messageSegmentsWithoutTrip.append("Call me at:")
        self.messageSegmentsWithoutTrip.append("Phone")
        self.messageSegmentsWithTrip = Array<String>()
        self.messageSegmentsWithTrip.append("Hello I'm currently at:")
        self.messageSegmentsWithTrip.append("Coordinate")
        self.messageSegmentsWithTrip.append("Call me at:")
        self.messageSegmentsWithTrip.append("Phone")
    }
    
//    init(messageSegments: Array<String>) {
//        self.messageSegmentsWithoutTrip = messageSegments
//    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(messageSegmentsWithoutTrip, forKey: Keys.messageSegsWithoutTrip)
        aCoder.encode(messageSegmentsWithTrip, forKey: Keys.messageSegsWithTrip)
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.messageSegmentsWithoutTrip = Array<String>()
        self.messageSegmentsWithTrip = Array<String>()
        if let decodedObject1 = aDecoder.decodeObject(forKey: Keys.messageSegsWithoutTrip) as? Array<String> {
            messageSegmentsWithoutTrip = decodedObject1
        }
        if let decodedObject2 = aDecoder.decodeObject(forKey: Keys.messageSegsWithTrip) as? Array<String> {
            messageSegmentsWithTrip = decodedObject2
        }
    }
    
    func buildMessage() -> String {
        var text : String = ""
        for message in messageSegmentsWithoutTrip {
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
    
    func getMessagesWithoutTrip() -> Array<String> {
        return self.messageSegmentsWithoutTrip
    }
    
    func updateMessagesWithoutTrip(updatedMessages : Array<String>) {
        self.messageSegmentsWithoutTrip = updatedMessages
    }
    
    func getMessagesWithTrip() -> Array<String> {
        return self.messageSegmentsWithTrip
    }
    
    func updateMessagesWithTrip(updatedMessages : Array<String>) {
        self.messageSegmentsWithTrip = updatedMessages
    }
    
}
