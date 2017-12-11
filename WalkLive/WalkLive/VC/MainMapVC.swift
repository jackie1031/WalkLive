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
import MessageUI


class MainMapVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate,  MFMessageComposeViewControllerDelegate{

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var startTripButton: UIButton!
    @IBOutlet weak var startTripPanelView: UIView!
    @IBOutlet weak var startTripDestinationTextLabel: UITextField!
    @IBOutlet weak var contactMessagePanel: UIView!
    
    @IBOutlet weak var contactMessagePanelTextView: UITextView!
    @IBOutlet weak var emergencyContactLabel: UILabel!
    
    
    
    var locationManager =  CLLocationManager()
    var roadRequester = RoadRequester()
    var timeManager: TimeManager!
    var tripView: OnGoingTripView!
    var mapItems: [MKMapItem]!

    override func viewDidLoad() {
        super.viewDidLoad()
        print(currentUserInfo)
        self.initializeView()
    }
    
    /// Private functiosn for general logistics
    private func initializeView() {
        self.emergencyContactLabel.text = emerStringBuilder()
        self.authorizeLocationUpdate()
        self.setDelegate()
        self.setupRoadRequester()
        self.setKeyboard()
        self.hidePanels()
    }
    
    private func emerStringBuilder() -> String{
        if (currentUserInfo?.emergency_number == nil) {
             return "Emer. Contact: None"
        }
            return "Emer. Contact: " + (currentUserInfo?.emergency_number)!
    }
    
    private func setDelegate() {
        self.locationManager.delegate = self
        self.mapView.delegate = self
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
    
    private func hideKeyboard(){
        self.view.endEditing(true)
    }
    
    //Public functions

    /// hides keyboard when user finish editing and tap on other places on the screen
    ///
    /// - Parameters:
    ///   - recoginizer: object to recognize motion when user tap on the screen
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.hideKeyboard()
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
        self.hideKeyboard()
        if (MFMessageComposeViewController.canSendText()) {
            let controller = MFMessageComposeViewController()
            controller.body = buildMessage(location: roadRequester.getSourceLocation())
            
            //FTOB needed here.
            controller.recipients = ["123-456-789"]
            controller.messageComposeDelegate = self
            self.present(controller, animated: true, completion: nil)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    
    /// build messages based on user location
    ///
    /// - Parameters:
    ///  location: location needed to form this message
    func buildMessage(location: CLLocation) -> String {
        if (self.tripView == nil) {
            return messages.buildMessage()
            
//            return "I am currently at (latitude:" +  String(location.coordinate.latitude) + ", longitude: " + String(location.coordinate.longitude) + ")." + " From Admin."
        }
        
        // Need to split to multiple strings because the logic will be too compicated for xcode to handle. Also, the only way to concatenate strings is "a + b" in Swift. :)
        var message =  "I am currently at (latitude:" +  String(location.coordinate.latitude) +
            ", longitude: " + String(location.coordinate.longitude)
        
        message = message + "), and heading to " + self.tripView.addressLabel.text!
        
        message = message + ". " + self.tripView.estimatedTimeLabel.text!
        
        message = message + ". " + self.tripView.timeUsedLabel.text! + ". From Admin."
       
        return message
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
        self.hideKeyboard()
        self.contactMessagePanel.isHidden = true
    }
    
    @IBAction func onContactPanelRefreshButton(_ sender: Any) {
        self.contactMessagePanelTextView.text = self.buildMessage(location: roadRequester.getSourceLocation())
    }
    
    
    
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(overlay: overlay)
        renderer.strokeColor = primaryColor
        renderer.lineWidth = 3.0
        
        return renderer
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        //... handle sms screen actions
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func onLogoutButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
        currentUserInfo = nil
    }
    


    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "routeChoiceSegue"){
            let vc = segue.destination as! SearchVC
            vc.routeDelegate = self
            vc.mapItems = self.mapItems
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            backItem.tintColor = primaryColor
            navigationItem.backBarButtonItem = backItem
        } else if (segue.identifier == "userSegue"){
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            backItem.tintColor = primaryColor
            navigationItem.backBarButtonItem = backItem
        }
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
        //FTOB needed here.
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
        self.tripView.emergencyContactLabel.text = self.emergencyContactLabel.text
        self.tripView.routeDelegate = self
        self.startTripPanelView.addSubview(tripView)
    }
    
    private func createTimeManager(trip: Trip){
        self.timeManager = TimeManager(timeInterval: trip.timeInterval!)
        self.timeManager.tripPanelDelegate = self
        self.timeManager.startTimer(timeInterval: 60)
    }
    
    
    func cancelTrip(){
        //FTOB needed here.
        self.roadRequester.removeRoute()
        self.timeManager.endTimer()
        self.timeManager = nil
        self.tripView = nil
    }
    
    func completeTrip(){
        //FTOB needed here.
        self.roadRequester.removeRoute()
        self.timeManager.endTimer()
        self.timeManager = nil
        self.tripView = nil
    }
    
}

extension MainMapVC: TripPanelDelegate{
    //FTOB needed here.
    func updateTripPanel(timeInfo: String) {
        self.tripView.timeUsedLabel.text = timeInfo
    }
}


