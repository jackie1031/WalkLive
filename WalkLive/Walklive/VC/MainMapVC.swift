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
    var timeManager: TimeManager!
    var tripView: OnGoingTripView!
    var mapItems: [MKMapItem]!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.authorizeLocationUpdate()
        
        self.setDelegate()
        self.setKeyboard()
        self.setupRoadRequester()
        
        self.hidePanels()
    }
    
    /// Private functiosn for general logistics
    private func setDelegate() {
        self.mapView.delegate = self
        self.locationManager.delegate = self
    }
    

    private func hidePanels(){
        self.startTripPanelView.isHidden = true
        self.contactMessagePanel.isHidden = true
    }
    
    private func setupRoadRequester(){
        self.roadRequester.setMapView(mapView: self.mapView)
        self.roadRequester.setCurrentLocation()
    }
    
    private func setKeyboard(){
        let hideTap = UITapGestureRecognizer(target: self, action: #selector(MainMapVC.hideKeyboardTap(_:)))
        hideTap.numberOfTapsRequired = 1
        self.view.isUserInteractionEnabled = true
        self.view.addGestureRecognizer(hideTap)
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
    
    //Public functions

    /// hides keyboard when user finish editing and tap on other places on the screen
    ///
    /// - Parameters:
    ///   - recoginizer: object to recognize motion when user tap on the screen
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.view.endEditing(true)
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
    
    /// Show/hide start trip panel when start trip button is clicked
    ///
    /// - Parameters:
    ///  Sender: the button which receives this action
    @IBAction func onStartTripBottomButton(_ sender: Any) {
        if (startTripPanelView.isHidden == false) {
            startTripPanelView.isHidden = true
            return
        }
        startTripPanelView.isHidden = false
        UIView.animate(withDuration: 0.6) {
            self.startTripPanelView.alpha = 0.8
        }
    }
    
    /// Show/hide message panel when start trip button is clicked
    ///
    /// - Parameters:
    ///  Sender: the button which receives this action
    @IBAction func onContactBottomButton(_ sender: Any) {
        if (contactMessagePanel.isHidden == false) {
            contactMessagePanel.isHidden = true
            return
        }
        
        let message = buildMessage(location: roadRequester.getSourceLocation())
        self.contactMessagePanelTextView.text = message
        self.contactMessagePanel.isHidden = false
    }
    
    /// boost emergency call(911) when police button is clicked
    ///
    /// - Parameters:
    ///  Sender: the button which receives this action
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
    
    /// send message to user when send button is clicked
    ///
    /// - Parameters:
    ///  Sender: the button which receives this action
    @IBAction func onContactPanelSendButton(_ sender: Any) {
        self.contactMessagePanel.isHidden = true
    }
    
    /// build messages based on user location
    ///
    /// - Parameters:
    ///  location: location needed to form this message
    func buildMessage(location: CLLocation) -> String {
        if (self.tripView == nil) {
            return "I am currently at Time Square(latitude:" +  String(location.coordinate.latitude) + ", longitude: " + String(location.coordinate.longitude) + ")." + "From Admin."
        }
        return "I am currently at Time Square(latitude:" +  String(location.coordinate.latitude) + ", longitude: " + String(location.coordinate.longitude) + "), and heading to Empire State Building(). Need ~15 minutes, I have walked 0 minutes, 5 seconds. From Admin."
    }
    
    /// request route + location when start button is clicked on start trip panel
    ///
    /// - Parameters:
    ///  sender: the button which is function is triggered
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
    func updateRoute(index: Int) {
        let targetItem = self.mapItems[index]
        self.roadRequester.drawRouteFromCurrentLocation(success: { (trip) in
            self.startTrip(trip: trip)
        }, failure: { (error) in
            
        }, destinationMapItem: targetItem)
    }
    
    private func startTrip(trip: Trip){
        self.createTripView(trip: trip)
        self.createTimeManager(trip: trip)
    }
    
    private func createTripView(trip: Trip){
        let tv = OnGoingTripView()
        self.tripView = tv.loadNib()
        self.tripView.destinationLabel.text = trip.destinationName
        self.tripView.addressLabel.text = trip.address
        self.tripView.estimatedTimeLabel.text = "Estimated Time: " + String(Int((trip.timeInterval!))/60) + " min(s)"
        
        self.tripView.center = self.startTripPanelView.center
        
        self.tripView.routeDelegate = self
        self.startTripPanelView.addSubview(tripView)
    }
    
    private func createTimeManager(trip: Trip){
        self.timeManager = TimeManager(timeInterval: trip.timeInterval!)
        self.timeManager.tripPanelDelegate = self
        self.timeManager.startTimer(timeInterval: 60)
    }
    
    
    func cancelTrip(){
        self.roadRequester.removeRoute()
        self.timeManager.endTimer()
        self.timeManager = nil
        self.tripView = nil
    }
}

extension MainMapVC: TripPanelDelegate{
    func updateTripPanel(timeInfo: String) {
        self.tripView.timeUsedLabel.text = timeInfo
    }
}


