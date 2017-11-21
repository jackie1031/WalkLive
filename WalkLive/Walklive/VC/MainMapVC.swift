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

class MainMapVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var startTripButton: UIButton!
    @IBOutlet weak var startTripPanelView: UIView!
    @IBOutlet weak var startTripDestinationTextLabel: UITextField!
    @IBOutlet weak var contactMessagePanel: UIView!
    var locationManager = CLLocationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        mapView.delegate = self
        locationManager.delegate = self
        // Do any additional setup after loading the view.
        startTripPanelView.isHidden = true
        contactMessagePanel.isHidden = true
        //setCurrentLocation()
        let roadRequester = RoadRequester(mapView: mapView)
        roadRequester.setCurrentLocation()
    }
    

    
    /// Check authrization status and start update locations
    private func authorizeLocationUpdate() {
        locationManager.delegate = self
        switch CLLocationManager.authorizationStatus() {
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse:
            locationManager.startUpdatingLocation()
        default:
            break
        }
    }
    
    /// Start update location if authorized
    ///
    /// - Parameters:
    ///   - manager: location manager
    ///   - status: new authorization status
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse {
            manager.startUpdatingLocation()
        }
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

