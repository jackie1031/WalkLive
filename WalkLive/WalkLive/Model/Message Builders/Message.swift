//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

// Message model keeps track of user's customized text messages (one for texting without
// an ongoing trip, the other for texting with an ongoing trip where the user can send
// their current location and time spent on this trip)
// Message customization is also saved locally.
class Message: NSObject, NSCoding {
    
    // Store message segments in 2 arrays
    private var messageSegmentsWithoutTrip : Array<String>
    private var messageSegmentsWithTrip: Array<String>

    // For saving message customizaiton locally
    struct Keys {
        static let messageSegsWithoutTrip = "messageSegmentsWithoutTrip"
        static let messageSegsWithTrip = "messageSegmentWithTrip"
    }
    
    /*
     Default init
     */
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
    
    /*
     Encoding selected info (2 message segments arrays) for local storage
     */
    func encode(with aCoder: NSCoder) {
        aCoder.encode(messageSegmentsWithoutTrip, forKey: Keys.messageSegsWithoutTrip)
        aCoder.encode(messageSegmentsWithTrip, forKey: Keys.messageSegsWithTrip)
    }
    
    /*
     For decoding locally stored data
     */
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
    
    /*
     Build message without ongoing trip, using MessageBuilder to concatenate message segments,
     and plug in current location and phone number as needed
     */
    func buildMessageWithoutTrip() -> String {
        let messageBuilder = MessageBuilder()
        return messageBuilder.messageWithoutTripBuilder(messageSegments: messageSegmentsWithoutTrip)
    }
    
    /*
     Build message with an ongoing trip, using MessageBuilder to concatenate message segments,
     and plug in current location, phone number, time spent on trip, and destination as needed
     */
    func buildMessageWithTrip(timeManager: TimeManager) -> String {
        let messageBuilder = MessageBuilder()
        return messageBuilder.messageWithTripBuilder(messageSegments: messageSegmentsWithTrip, timeManager: timeManager)
    }
    
    /*
     Since the settingsVC can show the user the preview of current customization for both messages
     but it's possible that the user doesn't have an ongoing trip when they enter settingsVC, this
     function builds message with ongoing trip without plugging in time spent on trip and destination
     */
    func buildMessageWithTripPreview() -> String {
        let messageBuilder = MessageBuilder()
        return messageBuilder.messageWithTripPreviewBuilder(messageSegments: messageSegmentsWithTrip)
    }
    
    /*
     Returns the array storing message segments without trip
     */
    func getMessagesWithoutTrip() -> Array<String> {
        return self.messageSegmentsWithoutTrip
    }
    
    /*
     Updates the array storing message segments without trip
     */
    func updateMessagesWithoutTrip(updatedMessages : Array<String>) {
        self.messageSegmentsWithoutTrip = updatedMessages
    }
    
    /*
     Returns the array storing message segments with an ongoing trip
     */
    func getMessagesWithTrip() -> Array<String> {
        return self.messageSegmentsWithTrip
    }
    
    /*
     Updates the array storing message segments with an ongoing trip
     */
    func updateMessagesWithTrip(updatedMessages : Array<String>) {
        self.messageSegmentsWithTrip = updatedMessages
    }
    
}
