//
//  FriendTrackVC.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
class FriendTrackVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var mapView: MKMapView!
    
    @IBOutlet weak var friendTrackTable: UITableView!
    
    var friendTrips: [TimePoint]!
    var friendTracker = FriendTracker()
    var currentFriendTrip: TimePoint?
    var timeInterval = TimeInterval(20)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.friendTracker.setMapView(mapView: self.mapView)
        friendTrackTable.delegate = self
        friendTrackTable.dataSource = self
        // Do any additional setup after loading the view.
        self.friendTracker.mapTimePoint(timePoint: self.currentFriendTrip!)
        self.friendTracker.trackNewTripWithTimer(trip: self.currentFriendTrip!, timeInterval: self.timeInterval)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        print("closed successfully")
        self.friendTracker.endTimer()
    }
    
    @IBAction func onTrackButton(_ sender: UIButton) {
        let trip = friendTrips[sender.tag]
        self.friendTracker.trackNewTripWithTimer(trip: trip, timeInterval: self.timeInterval)
    }
    
    
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (self.friendTrips != nil) {
            return self.friendTrips!.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendTrackTable.dequeueReusableCell(withIdentifier: "FriendTrackTableViewCell", for: indexPath) as! FriendTrackTableViewCell
        let trip = friendTrips[indexPath.row]
        cell.friendNameLabel.text = trip.username
        cell.phoneButton.tag = indexPath.row
        cell.messageButton.tag = indexPath.row
        cell.trackButton.tag = indexPath.row
        cell.selectionStyle = .none
        return cell
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
