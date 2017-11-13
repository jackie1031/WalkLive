//
//  MainMapVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/13/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class MainMapVC: UIViewController, MKMapViewDelegate {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var startTripButton: UIButton!
    @IBOutlet weak var startTripPanelView: UIView!
    @IBOutlet weak var startTripDestinationTextLabel: UITextField!
    @IBOutlet weak var contactMessagePanel: UIView!
    var locationManager = CLLocationManager()

    override func viewDidLoad() {
        super.viewDidLoad()
        mapView.delegate = self
        // Do any additional setup after loading the view.
        startTripPanelView.isHidden = true
        contactMessagePanel.isHidden = true
        
        // One degree of latitude is approximately 111 kilometers (69 miles) at all times.
        // San Francisco Lat, Long = latitude: 37.783333, longitude: -122.416667
        let mapCenter = CLLocationCoordinate2D(latitude: 39.783333, longitude: 76.6205)
        let mapSpan = MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1)
        let region = MKCoordinateRegion(center: mapCenter, span: mapSpan)
        // Set animated property to true to animate the transition to the region
        mapView.setRegion(region, animated: false)
        
        let sourceLocation = CLLocationCoordinate2D(latitude: 40.759011, longitude: -73.984472)
        let sourcePlacemark = MKPlacemark(coordinate: sourceLocation, addressDictionary: nil)
        let sourceAnnotation = MKPointAnnotation()
        if let location = sourcePlacemark.location {
            sourceAnnotation.coordinate = location.coordinate
        }
        sourceAnnotation.title = "Times Square"
        self.mapView.showAnnotations([sourceAnnotation], animated: true )

    }

    @IBAction func onStartTripBottomButton(_ sender: Any) {
        startTripPanelView.isHidden = false
        UIView.animate(withDuration: 0.6) {
            self.startTripPanelView.alpha = 0.8
        }
    }
    
    @IBAction func onContactBottomButton(_ sender: Any) {
        self.contactMessagePanel.isHidden = false
    }
    
    @IBAction func onPoliceBottomButton(_ sender: Any) {
    }
    @IBAction func onContactPanelSendButton(_ sender: Any) {
        self.contactMessagePanel.isHidden = true
    }
    
    
    @IBAction func onStartTripTopButton(_ sender: Any) {
        if (startTripDestinationTextLabel.text == "Empire State Building"){
            // 2.
            let sourceLocation = CLLocationCoordinate2D(latitude: 40.759011, longitude: -73.984472)
            let destinationLocation = CLLocationCoordinate2D(latitude: 40.748441, longitude: -73.985564)
            
            // 3.
            let sourcePlacemark = MKPlacemark(coordinate: sourceLocation, addressDictionary: nil)
            let destinationPlacemark = MKPlacemark(coordinate: destinationLocation, addressDictionary: nil)
            
            // 4.
            let sourceMapItem = MKMapItem(placemark: sourcePlacemark)
            let destinationMapItem = MKMapItem(placemark: destinationPlacemark)
            
            // 5.
            let sourceAnnotation = MKPointAnnotation()
            sourceAnnotation.title = "Times Square"
            
            if let location = sourcePlacemark.location {
                sourceAnnotation.coordinate = location.coordinate
            }
            
            
            let destinationAnnotation = MKPointAnnotation()
            destinationAnnotation.title = "Empire State Building"
            
            if let location = destinationPlacemark.location {
                destinationAnnotation.coordinate = location.coordinate
            }
            
            // 6.
            self.mapView.showAnnotations([sourceAnnotation,destinationAnnotation], animated: true )
            
            // 7.
            let directionRequest = MKDirectionsRequest()
            directionRequest.source = sourceMapItem
            directionRequest.destination = destinationMapItem
            directionRequest.transportType = .automobile
            
            // Calculate the direction
            let directions = MKDirections(request: directionRequest)
            
            // 8.
            directions.calculate {
                (response, error) -> Void in
                
                guard let response = response else {
                    if let error = error {
                        print("Error: \(error)")
                    }
                    return
                }
                print("enters here")
                let route = response.routes[0]
                print(route)
                self.mapView.add(route.polyline, level: MKOverlayLevel.aboveRoads)
                
                let rect = route.polyline.boundingMapRect
                self.mapView.setRegion(MKCoordinateRegionForMapRect(rect), animated: true)
            }
            self.startTripPanelView.isHidden = true
        }
    }
    
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(overlay: overlay)
        renderer.strokeColor = UIColor.red
        renderer.lineWidth = 4.0
        
        return renderer
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
