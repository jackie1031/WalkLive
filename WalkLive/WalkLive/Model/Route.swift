//
//  Route.swift
//  Walklive
//
//  Created by Michelle Shu on 12/1/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class Route: NSObject {
    var destinationName: String?
    var address: String?
    var phoneNumber: String?
    
    init(mapItem: MKMapItem) {
        self.destinationName = mapItem.name
        self.address = mapItem.placemark.title
        self.phoneNumber = mapItem.phoneNumber
    }
    
}
