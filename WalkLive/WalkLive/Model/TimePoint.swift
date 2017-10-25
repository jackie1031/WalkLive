//
//  TimePoint.swift
//  Walklive
//
//  Created by Michelle Shu on 10/16/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class TimePoint: NSObject {
    var dictionary: NSDictionary?
    var tripId: String?
    var userId: String?
    var timePointId: String?
    var location: String?
    var latitude: Float = 0
    var longitude: Float = 0
    
    init(dictionary: NSDictionary) {
        //deserialization code
        self.dictionary = dictionary
        tripId = dictionary["tripId"] as? String
        userId = dictionary["userId"] as? String
        timePointId = dictionary["timePointId"] as? String
        location = dictionary["location"] as? String
        latitude = (dictionary["latitude"] as? Float) ?? 0
        longitude = (dictionary["longitude"] as? Float) ?? 0
    }
    
}
