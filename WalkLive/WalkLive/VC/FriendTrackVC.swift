//
//  FriendTrackVC.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import MessageUI


class FriendTrackVC: UIViewController, UITableViewDelegate, UITableViewDataSource, MFMessageComposeViewControllerDelegate {

    @IBOutlet weak var mapView: MKMapView!
    
    @IBOutlet weak var friendTrackTable: UITableView!
    
    var friendTrips: [TimePoint]!
    var friendTracker = FriendTracker()
    var currentFriendTrip: TimePoint?
    var timeInterval = TimeInterval(20)
    var tripUpdater: TripUpdater!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.friendTracker.setMapView(mapView: self.mapView)
        friendTrackTable.delegate = self
        friendTrackTable.dataSource = self
        // Do any additional setup after loading the view.
        self.initializeUpdates()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        print("closed successfully")
        self.endTimers()

    }

    
    // Private functions
    
    
    /*
     Initializes friend tracker and trip updater.
     */
    private func initializeUpdates(){
        self.friendTracker.mapTimePoint(timePoint: self.currentFriendTrip!)
        self.friendTracker.trackNewTripWithTimer(trip: self.currentFriendTrip!, timeInterval: self.timeInterval)
        self.tripUpdater = TripUpdater(tripTableDelegate: self)
        self.tripUpdater.startTimer(timeInterval: self.timeInterval)
    }
    
    /*
     Stops tracking and trip updates
     */
    private func endTimers(){
        self.friendTracker.endTimer()
        self.tripUpdater.endTimer()
    }
    
    
    // Public functions
    
    
    /*
     Track a friend's trip
     */
    @IBAction func onTrackButton(_ sender: UIButton) {
        let trip = friendTrips[sender.tag]
        self.friendTracker.trackNewTripWithTimer(trip: trip, timeInterval: self.timeInterval)
    }
    
    /*
     Gets all friend trips.
     */
    func updateAllTimePoints(){
        backEndClient.getAllTrip(success: { (timePoints) in
            OperationQueue.main.addOperation {
                self.friendTrips = timePoints
                self.friendTrackTable.reloadData()
            }
        }) { (error) in
            print("failed to get all friend trips")
        }
    }
    
    /*
     Refreshes table view
     - Parameters:
       - tableView : UITableView showing friends trips
       - section: number of rows
     - Returns: number of friend trips
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (self.friendTrips != nil) {
            return self.friendTrips!.count
        }
        return 0
    }
    
    /*
     Sets up a single cell showing a friend trip
     - Parameters:
       - tableView : UITableView showing friends trips
       - section: number of rows
     - Returns: a single UITableViewCell
     */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendTrackTable.dequeueReusableCell(withIdentifier: "FriendTrackTableViewCell", for: indexPath) as! FriendTrackTableViewCell
        let friendTrip = friendTrips[indexPath.row]
        cell.friendNameLabel.text = friendTrip.username
        cell.currentLocationLabel.text = "At: (" + String(format: "%.3f", (friendTrip.curLat)!) + ", " + String(format: "%.3f", (friendTrip.curLong)!) + ")"
        cell.destinationLocationLabel.text = "To: "+friendTrip.destination!
        cell.timeSpentLabel.text = friendTrip.timeSpent
        cell.phoneButton.tag = indexPath.row
        cell.messageButton.tag = indexPath.row
        cell.trackButton.tag = indexPath.row
        cell.selectionStyle = .none
        return cell
    }
    
    /*
     Sets up message to send to a friend
     */
    @IBAction func onMessageButton(_ sender: UIButton) {
        let phoneNum = friendTrips![sender.tag].emergencyNum
        if (MFMessageComposeViewController.canSendText()) {
            let controller = MFMessageComposeViewController()
            controller.body = "Hey! Where are you?" + "From: \(currentUserInfo.username!)"
            controller.recipients = [phoneNum!]
            controller.messageComposeDelegate = self
            self.present(controller, animated: true, completion: nil)
        }
    }
    
    /*
     Calls friend
     */
    @IBAction func onPhoneButton(_ sender: UIButton) {
        let phoneNum = friendTrips![sender.tag].emergencyNum
        //Does not work in Simulator
        if let url = URL(string: "tel://\(phoneNum!)"), UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    /*
     Sets up view controller for editing and sending text messages
     - Parameters:
       - controller: MFMessageComposeViewController for handling text messages
       - result: MessageComposeResult
     */
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        //... handle sms screen actions
        self.dismiss(animated: true, completion: nil)
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


extension FriendTrackVC: TripTableUpdateDelegate{
    /*
     Updates table view that shows friend trips
     */
    func updateTable() {
        backEndClient.getAllTrip(success: { (friendTrips) in
            OperationQueue.main.addOperation {
                self.friendTrips = friendTrips
                self.friendTrackTable.reloadData()
                print("reloaded table!")
            }
        }) { (error) in
            print("failed to update")
        }
    }
}

