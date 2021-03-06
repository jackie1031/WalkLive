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

class FriendTracker: Tracker {
    
    private var timer = Timer()
    
    /*
     Map time point, start location, and destination
     - Parameters:
     - timePoint: TimePoint object about a location, usually between start and end locations
     */
    func mapTimePoint(timePoint: TimePoint) {
        if (!self.annotations.isEmpty){
            removeTimePoint()
        }
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
    
    /*
     Tracks new trip.
     - Parameters:
     - trip: TimePoint object that indicates starting location
     - timeInterval: keeps track of time spent
     */
    func trackNewTripWithTimer(trip: TimePoint, timeInterval: TimeInterval) {
        self.endTimer()
        self.setNewTrip(trip: trip)
        self.startTimer(timeInterval: timeInterval)
    }
    
    /*
     Sets new trip.
     - Parameters:
     - trip: a TimePoint object that indicates starting location
     */
    func setNewTrip(trip: TimePoint) {
        self.tripId = trip.tripId
        self.mapTimePoint(timePoint: trip)
        print("set successfully!")
    }
    
    /*
     Starts counting time.
     */
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTrip), userInfo: nil, repeats: true);
    }
    
    /*
     Updates trip.
     */
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
    
    /*
     Stops counting time.
     */
    func endTimer() {
        self.timer.invalidate()
        self.timer = Timer()
    }
    
}
