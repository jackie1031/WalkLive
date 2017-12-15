//
//  FriendTracker.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class FriendTracker: NSObject {
    var locationManager = CLLocationManager()
    var mapView: MKMapView!
    var annotations = [MKAnnotation]()
    private var timer = Timer()
    var tripId: Int?
    
    
    func setMapView(mapView: MKMapView){
        self.mapView = mapView
    }
    
    func setCurrentLocation(){
        let sourceLocation = getSourceLocation()
        centerMapOnLocation(location: sourceLocation)
    }
    
    
    func getSourceLocation() -> CLLocation {
        return (self.locationManager.location)!
    }
    
    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate, 50, 50)
        self.mapView.setRegion(coordinateRegion, animated: true)
    }
    
    func mapTimePoint(timePoint: TimePoint) {
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.startLat!, longitude: timePoint.startLong!)
        let currentAnnotation = MKPointAnnotation()
        currentAnnotation.title = "Currently Here!"
        currentAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.curLat!, longitude: timePoint.curLong!)
        let destinationAnnotation = MKPointAnnotation()
        destinationAnnotation.title = "Destination: " + timePoint.destination!
        destinationAnnotation.coordinate = CLLocationCoordinate2D(latitude: timePoint.endLat!, longitude: timePoint.endLong!)
        annotations.append(initialAnnotation)
        annotations.append(currentAnnotation)
        annotations.append(destinationAnnotation)
        mapView.showAnnotations(annotations, animated: true)
    }
    
    func removeTimePoint() {
        mapView.removeAnnotations(annotations)
        self.annotations = []
    }
    
    func trackNewTripWithTimer(trip: TimePoint, timeInterval: TimeInterval) {
        self.endTimer()
        self.setNewTrip(trip: trip)
        self.startTimer(timeInterval: timeInterval)
    }
    
    func setNewTrip(trip: TimePoint) {
        if (!self.annotations.isEmpty){
            self.removeTimePoint()
            self.annotations = []
        }
        self.tripId = trip.tripId
        self.mapTimePoint(timePoint: trip)
        print("set successfully!")
    }
    
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTrip), userInfo: nil, repeats: true);
    }
    
    @objc func updateTrip() {
        backEndClient.getSingleTrip(success: { (timePoint) in
            OperationQueue.main.addOperation {
                self.mapTimePoint(timePoint: timePoint)
                print("updated successfully")
            }
        }, failure: { (error) in
            print(error)
        }, tripId: self.tripId!)
    }
    
    func endTimer() {
        self.timer.invalidate()
        self.timer = Timer()
    }
    
}
