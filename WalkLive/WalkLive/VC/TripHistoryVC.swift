//
//  TripHistoryVC.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/16/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
var locationManager =  CLLocationManager()


class TripHistoryVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    @IBOutlet weak var mapView: MKMapView!
    
    @IBOutlet weak var tripTable: UITableView!
    var trips: [TimePoint]?
    var tracker = HistoryTracker()
    override func viewDidLoad() {
        super.viewDidLoad()
        self.getTripHistory()
        self.setDelegate()
        tracker.setMapView(mapView: self.mapView)
        // Do any additional setup after loading the view.
    }
    
    
    /// Private functions
    
    /*
     Sets itself as the delegate of trip table
     */
    private func setDelegate(){
        self.tripTable.delegate = self
        self.tripTable.dataSource = self
    }
    
    
    /// Public functions
    
    
    /*
     Creates a single table view cell that contains trip info
     - Parameters:
     - tableView: UITableView that contains trip info
     - indexPath: index of cell
     - Returns: UITableViewCell object containing trip info
     */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tripTable.dequeueReusableCell(withIdentifier: "TripHistoryTableViewCell", for: indexPath) as! TripHistoryTableViewCell
        let trip = self.trips![indexPath.row]
        cell.destinationNameLabel.text = trip.destination
        cell.startLocationLabel.text =  "Start at: (" + String(format: "%.3f", (trip.startLat)!) + ", " + String(format: "%.3f", (trip.startLong)!) + ")"
        cell.endLocationLabel.text = "End at: (" + String(format: "%.3f", (trip.curLat)!) + ", " + String(format: "%.3f", (trip.curLong)!) + ")"
        cell.startTimeLabel.text = "Start time: " + trip.startTime!
        cell.endTimeLabel.text = trip.timeSpent
        cell.trackButton.tag = indexPath.row
        return cell
    }
    
    /*
     Sets up table view that shows trip history
     - Parameters:
     - tableView: UITableView that shows trip history
     - sections: number of rows
     - Returns: number of rows needed
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (self.trips != nil) {
            return self.trips!.count
        }
        return 0
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     Gets trip history.
     */
    func getTripHistory(){
        backEndClient.getTripHistory(success: { (timepoints) in
            OperationQueue.main.addOperation {
                self.trips = timepoints
                self.tripTable.reloadData()
            }
        }) { (error) in
            // Warning signs
        }
    }
    
    @IBAction func onTrackButton(_ sender: UIButton) {
        self.tracker.mapTimePoint(timePoint: self.trips![sender.tag])
    }
    
    /*
     if (self.tripView == nil) {
     return messages.buildMessageWithoutTrip()
     }
     
     return messages.buildMessageWithTrip(timeManager: timeManager, roadRequester: roadRequester
 */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
