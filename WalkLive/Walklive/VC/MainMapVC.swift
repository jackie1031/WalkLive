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

class MainMapVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate{

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var startTripButton: UIButton!
    @IBOutlet weak var startTripPanelView: UIView!
    @IBOutlet weak var startTripDestinationTextLabel: UITextField!
    @IBOutlet weak var contactMessagePanel: UIView!
    
    @IBOutlet weak var contactMessagePanelTextView: UITextView!
    
    var locationManager = CLLocationManager()
    var roadRequester = RoadRequester()
    var mapItems: [MKMapItem]!

    override func viewDidLoad() {
        super.viewDidLoad()
        mapView.delegate = self
        locationManager.delegate = self
        // Do any additional setup after loading the view.
        startTripPanelView.isHidden = true
        contactMessagePanel.isHidden = true
        //setCurrentLocation()
        roadRequester.setMapView(mapView: self.mapView)
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
        let message = buildMessage(location: roadRequester.getSourceLocation())
        
        self.contactMessagePanelTextView.text = message
        self.contactMessagePanel.isHidden = false
    }
    
    @IBAction func onPoliceBottomButton(_ sender: Any) {
        let busPhone = "911"
        //Does not work in Simulator
        if let url = URL(string: "tel://\(busPhone)"), UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    @IBAction func onContactPanelSendButton(_ sender: Any) {
        self.contactMessagePanel.isHidden = true
    }
    
    func buildMessage(location: CLLocation) -> String {
        return "I am currently at Time Square(latitude:" +  String(location.coordinate.latitude) + ", longitude: " + String(location.coordinate.longitude) + "), and heading to Empire State Building(). Need ~15 minutes, I have walked 0 minutes, 5 seconds. From Admin."
    }
    
    @IBAction func onStartTripTopButton(_ sender: Any) {
        //check destination emptiness
        if (self.startTripDestinationTextLabel.text == "") {
            return
        }
        print(startTripDestinationTextLabel.text!)
        roadRequester.getSearchResults(success: { (mapItems) in
            self.mapItems = mapItems
            self.performSegue(withIdentifier: "routeChoiceSegue", sender: nil)
        }, failure: { (error) in
            print(error)
        }, query: startTripDestinationTextLabel.text!)
    }
    
    @IBAction func onContactPanelCancelButton(_ sender: Any) {
        self.contactMessagePanel.isHidden = true
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
    

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "routeChoiceSegue"){
            let vc = segue.destination as! SearchVC
            vc.routeDelegate = self
            vc.mapItems = self.mapItems
            let backItem = UIBarButtonItem()
            backItem.title = "Cancel"
            backItem.tintColor = primaryColor
            navigationItem.backBarButtonItem = backItem
        }
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }

}

extension MainMapVC: RouteDelegate{
    func updateRoute() {
        
    }
}


