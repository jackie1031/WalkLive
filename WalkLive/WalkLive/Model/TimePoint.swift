//
//  TimePoint.swift
//  Walklive
//
//  Created by Michelle Shu on 10/16/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class TimePoint: NSObject, Codable {
//    { username:[String], startTime: [string], destination: [string],startLong:[double,],startLat:[double],curLong:[double],curLat:[double],endLat:[double],timeSpent[String] }
    var username: String?
    var startTime: String?
    var destination: String?
    var startLong: Double?
    var startLat: Double?
    var curLong: Double?
    var curLat: Double?
    var endLong: Double?
    var endLat: Double?
    var timeSpent: String?
    var emergencyNum: String?
    var tripId: Int?
    var address: String?
    
    func getStartMapItem() -> MKMapItem{
        let pl = MKPlacemark(coordinate: CLLocationCoordinate2D(latitude: self.startLat!, longitude: self.startLong!), addressDictionary: nil)
        return MKMapItem(placemark: pl)
    }
    
    func getDestinationMapItem() -> MKMapItem{
        var pl = MKPlacemark(coordinate: CLLocationCoordinate2D(latitude: self.endLat!, longitude: self.endLong!), addressDictionary: nil)
        return MKMapItem(placemark: pl)
    }
    
}
