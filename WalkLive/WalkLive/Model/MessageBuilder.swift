//
//  MessageBuilder.swift
//  WalkLive
//
//  Created by Yang Cao on 12/12/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import CoreLocation

// A string builder that builds text messages
class MessageBuilder: NSObject {
    
    /*
     builds message without ongoing trip
    */
    func messageWithoutTripBuilder(messageSegments : Array<String>) -> String {
        var text : String = ""
        for message in messageSegments {
            var realValue : String
            if (message == "Username") {
                realValue = currentUserInfo.username!
            } else if (message == "Phone") {
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
    
    /*
     builds message with ongoing trip, able to plug in current time spent and trip location
    */
    func messageWithTripBuilder(messageSegments : Array<String>, timeManager: TimeManager, roadRequester: RoadRequester) -> String {
        var text : String = ""
        for message in messageSegments {
            var realValue : String
            if (message == "Username") {
                realValue = currentUserInfo.username!
            } else if (message == "Phone") {
                realValue = currentUserInfo.contact!
            } else if (message == "Coordinate") {
                let location = CLLocationManager().location?.coordinate
                let longtitude = String(format: "%.2f", (location?.longitude)!)
                let latitude = String(format: "%.2f", (location?.latitude)!)
                realValue = "(" + longtitude + ", " + latitude + ")"
            } else if (message == "Time") {
                realValue = timeManager.buildMessageOnCurrentTimer()
            } else if (message == "Destination") {
                realValue = roadRequester.destinationAnnotation.title as! String
            } else {
                realValue = message
            }
            text.append(realValue)
            text.append(" ")
        }
        return text
    }
    
    /*
     builds message model with ongoing trip in SettingsVC
     */
    func messageWithTripPreviewBuilder(messageSegments: Array<String>) -> String {
        var text : String = ""
        for message in messageSegments {
            var realValue : String
            if (message == "Username") {
                realValue = currentUserInfo.username!
            } else if (message == "Phone") {
                realValue = currentUserInfo.contact!
            } else if (message == "Coordinate") {
                let location = CLLocationManager().location?.coordinate
                let longtitude = String(format: "%.2f", (location?.longitude)!)
                let latitude = String(format: "%.2f", (location?.latitude)!)
                realValue = "(" + longtitude + ", " + latitude + ")"
            } else if (message == "Time") {
                realValue = "Time spent: [ ]min(s)"
            } else if (message == "Destination") {
                realValue = "[destination]"
            } else {
                realValue = message
            }
            text.append(realValue)
            text.append(" ")
        }
        return text
    }
}
