//
//  Trip.swift
//  Walklive
//
//  Created by Michelle Shu on 12/3/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class Trip: NSObject {
    var destinationName: String?
    var address: String?
    var phoneNumber: String?
    var timeInterval: TimeInterval?
    var timeSpentInt: Int?
    var tripId: Int?
    
    init(mapItem: MKMapItem, route: MKRoute) {
        self.destinationName = mapItem.name
        self.address = mapItem.placemark.title
        self.phoneNumber = mapItem.phoneNumber
        self.timeInterval = route.expectedTravelTime
    }
    
}
