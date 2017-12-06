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
        self.setDestinationLocation(destinationMapItem: destinationMapItem)
        self.getRouteFromCurrentLocation(success: { (route) in
            self.overlay = route.polyline
            self.mapView.add(route.polyline, level: MKOverlayLevel.aboveRoads)
            let rect = route.polyline.boundingMapRect
            self.mapView.setRegion(MKCoordinateRegionForMapRect(rect), animated: true)
            success(Trip(mapItem: destinationMapItem, route: route))
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
            success((response?.routes[0])!)
            }
        }
    
    func setDestinationLocation(destinationMapItem: MKMapItem){
        let destinationAnnotation = MKPointAnnotation()
        destinationAnnotation.title = destinationMapItem.name
        destinationAnnotation.coordinate = destinationMapItem.placemark.coordinate
    self.mapView.showAnnotations([destinationAnnotation], animated: true )
        self.destinationAnnotation = destinationAnnotation
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

    //  Inefficient algorithm; will not consider to use until talk to the TA.
//    func searchLandmarkNearCoordiantes(){
//        let request = MKLocalSearchRequest()
//        request.region = self.getCurrentDefinedRegion()
//        request.naturalLanguageQuery = "Forever 21"
//
//        let search = MKLocalSearch(request: request)
//        search.start { (response, error) in
//            if (error != nil) {
//                print(error!)
//                return
//            }
//            print(response!)
//        }
//    }
    
}

