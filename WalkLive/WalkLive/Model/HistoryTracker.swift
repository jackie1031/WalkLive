//
//  HistoryTracker.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/17/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import CoreLocation
import MapKit

class HistoryTracker: Tracker {

    func mapTimePoint(timePoint: TimePoint) {
        if (!self.annotations.isEmpty){
            removeTimePoint()
        }
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.startLat!, longitude: timePoint.startLong!)
        let currentAnnotation = MKPointAnnotation()
        currentAnnotation.title = "Ended Here!"
        currentAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.curLat!, longitude: timePoint.curLong!)
        let destinationAnnotation = MKPointAnnotation()
        destinationAnnotation.title = "Destination: " + timePoint.destination!
        destinationAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.endLat!, longitude: timePoint.endLong!)
        annotations.append(initialAnnotation)
        annotations.append(currentAnnotation)
        annotations.append(destinationAnnotation)
        mapView.showAnnotations(annotations, animated: true)
    }
}
