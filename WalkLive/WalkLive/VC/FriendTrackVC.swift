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
    var timePoints = [TimePoint]()
    let friendTracker = FriendTracker()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        friendTracker.setMapView(mapView: self.mapView)
        friendTrackTable.delegate = self
        friendTrackTable.dataSource = self
        self.testTimePoint()
        // Do any additional setup after loading the view.
    }
    func testTimePoint() {
        let tp1 = TimePoint()
        tp1.curLat = 40.7589
        tp1.curLong = -73.9851
        tp1.startLat = 40.785091
        tp1.startLong = -73.968285
        tp1.endLat = 40.748817
        tp1.endLong = -73.985428
        tp1.username = currentUserInfo.username

        tp1.destination = "Empire State Building"
        
        timePoints.append(tp1)
        friendTracker.mapTimePoint(timePoint: tp1)
        friendTrackTable.reloadData()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.timePoints != nil {
            return self.timePoints.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = friendTrackTable.dequeueReusableCell(withIdentifier: "FriendTrackTableViewCell", for: indexPath) as! FriendTrackTableViewCell
        
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
