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

// RoadRequester model basically keeps track of the source location and destination of a trip request,
// the user's current location, and thus searches for corresponding locations near the user, and once
// destination is set, a route will be drawn
class RoadRequester: NSObject {
    var locationManager = CLLocationManager()
    var mapView: MKMapView!
    var matchingItems = [MKMapItem]()
    var overlay: MKOverlay!
    var dangerOverlay: [MKOverlay]!
    var destinationAnnotation: MKAnnotation!
    var sourceAnnotation: MKAnnotation!
    var trip: Trip!
    var CURRENTCOLOR = UIColor.red
    /*
     Sets map view.
     */
    func setMapView(mapView: MKMapView){
        self.mapView = mapView
    }
    
    /*
     Gets current location and sets map view region to that location.
     */
    func setCurrentLocation(){
        let sourceLocation = getSourceLocation()
        centerMapOnLocation(location: sourceLocation)
    }
    
    /*
     Gets source location
     Returns: source location
     */
    func getSourceLocation() -> CLLocation {
        return (self.locationManager.location)!
    }
    
    /*
     Centers map on a location.
     - Parameters:
     - location: a CLLocation object
     */
    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate, 1000, 1000)
        self.mapView.setRegion(coordinateRegion, animated: true)
    }
    
    /*
     Gets current region and sets the distance.
     */
    func getCurrentDefinedRegion() -> MKCoordinateRegion {
        return MKCoordinateRegionMakeWithDistance(getSourceLocation().coordinate, 50, 50)
    }
    
    /*
     Gets current location in custom annotation
     - Returns: MKPointAnnotation with custom annotation
     */
    func getcurrentLocationInAnnotation() -> MKPointAnnotation {
        let currentLocationCLL = self.getSourceLocation()
        let currentLocationAnnotation = MKPointAnnotation()
        currentLocationAnnotation.coordinate = currentLocationCLL.coordinate
        currentLocationAnnotation.title = "Currently Here!"
        return currentLocationAnnotation
    }
    
    /*
     Gets search results.
     - Parameters:
     - success: if success, return a list of MKMapItems
     - failure: if failure, show error
     - query: the search entry the user enters
     */
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
    
    /*
     Gets matchingItems.
     - Returns: a list of MKMapItems
     */
    func getMatchingItems() -> [MKMapItem] {
        return self.matchingItems
    }
    
    /*
     Draws route from current location to destination
     - Parameters:
     - success: creates trip if success
     - failure: show error if failure
     - destinationMapItem: destination MapItem object
     */
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
    
    /*
     Gets route from current location to destination
     - Parameters:
     - success: creates trip if success
     - failure: show error if failure
     - destinationMapItem: destination MapItem object
     */
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
    
    /*
     Draws route from a timepoint to destination
     - Parameters:
     - success: creates trip if success
     - failure: show error if failure
     - timePoint: TimePoint object
     */
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
    
    
    
    /*
     Gets route from a timepoint to destination
     - Parameters:
     - success: creates trip if success
     - failure: show error if failure
     - timePoint: TimePoint object
     */
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
    
    func drawDangerZones(clusters: [DangerCluster]){
        for (_, cluster) in clusters.enumerated() {
            self.CURRENTCOLOR = cluster.getDangerColor()
            self.mapView.add(cluster.getOverlay())
            
        }
//        self.mapView.addOverlays(dangerOverlay)
    }
    
    func removeDangerZones(){
        self.mapView.removeOverlays(self.dangerOverlay)
    }
    
    func getDangerZone(clusters: [DangerCluster]) -> [MKOverlay]{
        var dangerOverlay = [MKOverlay]()
        for (_, cluster) in clusters.enumerated() {
            dangerOverlay.append(cluster.getOverlay())
        }
        return dangerOverlay
    }
    
    /*
     Sets destination location
     - Parameters:
     - destinationMapItem: contains destination info
     */
    func setDestinationLocation(destinationMapItem: MKMapItem){
        let destinationAnnotation = MKPointAnnotation()
        destinationAnnotation.title = destinationMapItem.name
        destinationAnnotation.coordinate = destinationMapItem.placemark.coordinate
        self.mapView.showAnnotations([destinationAnnotation], animated: true )
        self.destinationAnnotation = destinationAnnotation
    }
    
    /*
     Sets inital location with annotation
     */
    func setInitialLocation() {
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = getSourceLocation().coordinate
        self.sourceAnnotation = initialAnnotation
    }
    
    /*
     Sets inital location with annotation (if initial location is not
     user current location
     - Parameters:
     - mapItem: indicating starting location
     */
    func setInitialLocation(mapItem: MKMapItem) {
        let initialAnnotation = MKPointAnnotation()
        initialAnnotation.title = "Started Here!"
        initialAnnotation.coordinate = mapItem.placemark.coordinate
        self.sourceAnnotation = initialAnnotation
    }

    /*
     Converts CLLocation object to MapItem object
     - Parameters:
     - cllocation: CLLocation object
     Returns: MKMapItem object
     */
    func convertCLLocationToMapItem(cllocation: CLLocation) -> MKMapItem {
        let placemark = MKPlacemark(coordinate: cllocation.coordinate, addressDictionary: nil)
        return MKMapItem(placemark: placemark)
    }
    
    /*
     Removes route.
     */
    func removeRoute(){
        if (self.overlay != nil) {
            self.mapView.remove(self.overlay)}
        if (self.destinationAnnotation != nil) {
            self.mapView.removeAnnotation(self.destinationAnnotation)
        }
    }
    
    func drawSingleCluster(cluster: DangerCluster) {
        self.mapView.add(cluster.getOverlay())

    }

    
}

