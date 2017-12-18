//
//  DangerCluster.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/18/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class DangerCluster: NSObject, Codable {
    var centerLat: Double?
    var centerLong: Double?
    var radius: Double?
    var dangerLevel: Int?
    var isDay: Int?
    
    func getOverlay() -> MKOverlay {
        let coordinates = CLLocationCoordinate2DMake(CLLocationDegrees(self.centerLat!), CLLocationDegrees(self.centerLong!))
        return MKCircle(center: coordinates, radius: convertDegreeToMeters(degree: self.radius!))
        
    }
    
    func getDangerColor() -> UIColor {
        switch self.dangerLevel!{
        case 0:
            return level0Color
        case 1:
            return level1Color
        case 2:
            return level2Color
        case 3:
            return level3Color
        case 4:
            return level4Color
        case 5:
            return level5Color
        default:
            return UIColor.red
        }
    }
        
}
