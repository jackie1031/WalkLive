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

    private var userFilePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("UserInfo").path)
    }
    
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
        self.authorizeLocationUpdate()
            self.initializeView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.emergencyContactLabel.text = stringBuilder.emerStringBuilderWithUser()
    }
    
    /// Private functiosn for general logistics
    private func initializeView() {
        self.setDelegate()
        self.setupRoadRequester()
        self.setKeyboard()
        self.hidePanels()
        self.checkOngoingTrip()
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
    
    private func clearUserData() {
        do {
            try FileManager.default.removeItem(atPath: userFilePath)
        } catch {
            print("No data found.")
        }
    }
    
    private func hideKeyboard(){
        self.view.endEditing(true)
    }
    
    func checkOngoingTrip(){
        backEndClient.getOngoingTrip(success: { (timePoint) in
            self.setOngoingTrip(timePoint: timePoint)
        }, failure: { (error) in
            
        })
    }
    
    func setOngoingTrip(timePoint: TimePoint) {
        self.roadRequester.drawRouteFromTimePoint(success: { (trip) in
            trip.timeSpentInt = Int((timePoint.timeSpent?.westernArabicNumeralsOnly)!)
            trip.tripId = timePoint.tripId
            trip.address = timePoint.address
            self.continueTrip(trip: trip)
        }, failure: { (error) in
            
        }, timePoint: timePoint)
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
            controller.recipients = [stringBuilder.emerStringBuilder()]
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
            return messages.buildMessageWithoutTrip()
        }
        
        return messages.buildMessageWithTrip(timeManager: timeManager)
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
        if (self.timeManager != nil) {
            self.timeManager.endTimer()
        }
        self.dismiss(animated: true, completion: nil)
        currentUserInfo = nil
        self.clearUserData()
    }
    


    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let backItem = UIBarButtonItem()
        backItem.title = "Map"
        backItem.tintColor = primaryColor
        navigationItem.backBarButtonItem = backItem
        if (segue.identifier == "routeChoiceSegue"){
            let vc = segue.destination as! SearchVC
            vc.routeDelegate = self
            vc.mapItems = self.mapItems
        }
    }

}

extension MainMapVC: RouteDelegate{
    func updateRoute(index: Int) {
        let targetItem = self.mapItems[index]
        self.roadRequester.drawRouteFromCurrentLocation(success: { (trip) in
            self.startTrip(trip: trip)
        }, failure: { (error) in
            self.roadRequester.removeRoute()
            self.roadRequester.setCurrentLocation()
            OperationQueue.main.addOperation {
               let errorSign = warnigSignFactory.cannotBuildRouteWarningSign()
                errorSign.center = self.view.center
                self.view.addSubview(errorSign)
            }
        }, destinationMapItem: targetItem)
    }
    
    private func startTrip(trip: Trip){
        //FTOB needed here.
        self.createTimeManager(trip: trip)
        self.createTripView(trip: trip)
        self.timeManager.startTimer(timeInterval: 60)
    }
    
    private func continueTrip(trip: Trip){
        self.createTripView(trip: trip)
        self.createTimeManager(trip: trip)
        self.timeManager.tripId = trip.tripId
        self.timeManager.usedTimeInterval = trip.timeSpentInt!
        self.timeManager.continueTimer(timeInterval: 60)
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
        self.timeManager = TimeManager(timeInterval: trip.timeInterval!, roadRequester: self.roadRequester)
        self.timeManager.tripPanelDelegate = self
        if (trip.timeSpentInt != nil){
            self.timeManager.usedTimeInterval = trip.timeSpentInt!}
        overarchTimeManager = self.timeManager
    }
    
    
    func cancelTrip(){
        //FTOB needed here.
        backEndClient.endTrip(success: {
            OperationQueue.main.addOperation {
            self.roadRequester.removeRoute()
            self.timeManager.endTimer()
            self.timeManager = nil
            self.tripView = nil
            overarchTimeManager = nil
            print("success")
            }
        }, failure: { (error) in
            print("fail to end trip")
        }, timePoint: timeManager.buildTimePoint()!)
    }
    
    func completeTrip(){
        //FTOB needed here.
        backEndClient.endTrip(success: {
            OperationQueue.main.addOperation {
            self.roadRequester.removeRoute()
            self.timeManager.endTimer()
            self.timeManager = nil
            overarchTimeManager = nil
            self.tripView = nil
                print("success")
            }
        }, failure: { (error) in
            print("fail to end trip")
        }, timePoint: timeManager.buildTimePoint()!)
    }
}

extension MainMapVC: TripPanelDelegate{
    //FTOB needed here.
    func updateTripPanel(timeInfo: String) {
        self.tripView.timeUsedLabel.text = timeInfo
    }
}


