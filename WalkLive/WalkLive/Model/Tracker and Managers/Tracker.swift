//
//  Tracker.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/17/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class Tracker: NSObject {
    
    var locationManager = CLLocationManager()
    var mapView: MKMapView!
    var annotations = [MKAnnotation]()
    private var timer = Timer()
    var tripId: Int?
    
    /*
     Sets up map view.
     */
    func setMapView(mapView: MKMapView){
        self.mapView = mapView
    }
    
    /*
     Gets user current location
     */
    func setCurrentLocation(){
        let sourceLocation = getSourceLocation()
        centerMapOnLocation(location: sourceLocation)
    }
    
    /*
     Gets source location.
     */
    func getSourceLocation() -> CLLocation {
        return (self.locationManager.location)!
    }
    
    /*
     Centers map on specific location
     - Parameters:
     - location: CLLocation object indicating location
     */
    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate, 50, 50)
        self.mapView.setRegion(coordinateRegion, animated: true)
    }
    
    /*
     Removes a time point
     */
    func removeTimePoint() {
        mapView.removeAnnotations(annotations)
        self.annotations.removeAll(keepingCapacity: true)
    }
    
}
