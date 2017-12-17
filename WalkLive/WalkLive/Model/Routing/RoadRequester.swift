//
//  RoadRequester.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class RoadRequester: NSObject {
    var locationManager = CLLocationManager()
    var mapView: MKMapView!
    var matchingItems = [MKMapItem]()
    var overlay: MKOverlay!
    var destinationAnnotation: MKAnnotation!
    var sourceAnnotation: MKAnnotation!
    var trip: Trip!
    
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
    
    func getCurrentDefinedRegion() -> MKCoordinateRegion {
        return MKCoordinateRegionMakeWithDistance(getSourceLocation().coordinate, 50, 50)
    }
    
    func getcurrentLocationInAnnotation() -> MKPointAnnotation {
        let currentLocationCLL = self.getSourceLocation()
        let currentLocationAnnotation = MKPointAnnotation()
        currentLocationAnnotation.coordinate = currentLocationCLL.coordinate
        currentLocationAnnotation.title = "Currently Here!"
        return currentLocationAnnotation
    }
    
    func getSearchResults(success: @escaping ([MKMapItem]) -> (), failure: @escaping (Error) -> (), query: String){
        let request = MKLocalSearchRequest()
        print(query)
        request.naturalLanguageQuery = query
        request.region = mapView.region
        let search = MKLocalSearch(request: request)
        search.start { (response, error) in
            if (error != nil) {
                failure(error!)
            }
            success((response?.mapItems)!)
        }
    }
    
    func getMatchingItems() -> [MKMapItem] {
        return self.matchingItems
    }
    
    func drawRouteFromCurrentLocation(success: @escaping (Trip) -> (), failure: @escaping (Error) -> (), destinationMapItem: MKMapItem) {
        self.setInitialLocation()
        self.setDestinationLocation(destinationMapItem: destinationMapItem)
        self.getRouteFromCurrentLocation(success: { (route) in
            self.overlay = route.polyline
            self.mapView.add(route.polyline, level: MKOverlayLevel.aboveRoads)
            let rect = route.polyline.boundingMapRect
            self.mapView.setRegion(MKCoordinateRegionForMapRect(rect), animated: true)
            self.trip = Trip(mapItem: destinationMapItem, route: route)
            success(self.trip)
        }, failure: { (error) in
            failure(error)
        }, destinationMapItem: destinationMapItem)
    }
    
    func getRouteFromCurrentLocation(success: @escaping (MKRoute) -> (), failure: @escaping(Error) -> (), destinationMapItem: MKMapItem) {
        let sourceMapItem = convertCLLocationToMapItem(cllocation: self.getSourceLocation())
        let directionRequest = MKDirectionsRequest()
        directionRequest.source = sourceMapItem
        directionRequest.destination = destinationMapItem
        directionRequest.transportType = .walking
        
        let directions = MKDirections(request: directionRequest)
        directions.calculate { (response, error) in
            if (error != nil) {
                failure(error!)
            }
            if (response == nil) {
                failure(error!)
            }
            if ((response?.routes[0]) == nil) {
                failure(LoginError(status: 0))
            } else{
                success((response?.routes[0])!)}
            }
        }
    
    func drawRouteFromTimePoint(success: @escaping (Trip) -> (), failure: @escaping (Error) -> (), timePoint: TimePoint) {
        self.getRouteFromTimePoint(success: { (route) in
            OperationQueue.main.addOperation {
            self.setInitialLocation(mapItem: timePoint.getStartMapItem())
            self.setDestinationLocation(destinationMapItem: timePoint.getDestinationMapItem())
            self.trip = Trip(mapItem: timePoint.getDestinationMapItem(), route: route)
            self.overlay = route.polyline
            self.mapView.add(route.polyline, level: MKOverlayLevel.aboveRoads)
            let rect = route.polyline.boundingMapRect
            self.mapView.setRegion(MKCoordinateRegionForMapRect(rect), animated: true)
            success(Trip(mapItem: timePoint.getDestinationMapItem(), route: route))
            }
        }, failure: { (error) in
            failure(error)
        }, timePoint: timePoint)
    }
    
    func getRouteFromTimePoint(success: @escaping (MKRoute) -> (), failure: @escaping(Error) -> (), timePoint: TimePoint) {
        let directionRequest = MKDirectionsRequest()
        directionRequest.source = timePoint.getStartMapItem()
        directionRequest.destination = timePoint.getDestinationMapItem()
        directionRequest.transportType = .walking
        
        let directions = MKDirections(request: directionRequest)
        directions.calculate { (response, error) in
            if (error != nil) {
                failure(error!)
            }
            if (response == nil) {
                failure(error!)
            }
            if ((response?.routes[0]) == nil) {
                failure(LoginError(status: 0))
            } else{
                success((response?.routes[0])!)}
        }
    }
    
    func setDestinationLocation(destinationMapItem: MKMapItem){
        let destinationAnnotation = MKPointAnnotation()
        destinationAnnotation.title = destinationMapItem.name
        destinationAnnotation.coordinate = destinationMapItem.placemark.coordinate
        self.mapView.showAnnotations([destinationAnnotation], animated: true )
        self.destinationAnnotation = destinationAnnotation
    }
    
    func setInitialLocation() {
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = getSourceLocation().coordinate
        self.sourceAnnotation = initialAnnotation
    }
    
    func setInitialLocation(mapItem: MKMapItem) {
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = mapItem.placemark.coordinate
        self.sourceAnnotation = initialAnnotation
    }

    
    func convertCLLocationToMapItem(cllocation: CLLocation) -> MKMapItem {
        let placemark = MKPlacemark(coordinate: cllocation.coordinate, addressDictionary: nil)
        return MKMapItem(placemark: placemark)
    }
    
    func removeRoute(){
        if (self.overlay != nil) {
            self.mapView.remove(self.overlay)}
        if (self.destinationAnnotation != nil) {
            self.mapView.removeAnnotation(self.destinationAnnotation)
        }
    }

    
}

