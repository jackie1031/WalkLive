//
//  SearchVC.swift
//  Walklive
//
//  Searching for destination location view
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit

class SearchVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var searchLocationTable: UITableView!
    var mapItems: [MKMapItem]!
    weak var routeDelegate: RouteDelegate?
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        searchLocationTable.delegate = self
        searchLocationTable.dataSource = self
        self.drawLocation()
        // Do any additional setup after loading the view.
    }
    
    /*
     Gets number of rows needed to show search results
     - Parameters:
     - tableView: UITableView that shows search results
     - section: number of rows
     - Returns: number of search results
     */
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.mapItems != nil {
            return self.mapItems.count
        } else {
            return 0
        }
    }
    
    /*
     Sets up a single table view cell about a specific location
     - Parameters:
     - tableView: UITableView that shows search results
     - indexPath: index the cell is at
     - Returns: single table view cell
     */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = searchLocationTable.dequeueReusableCell(withIdentifier: "LocationCell", for: indexPath) as! LocationTableViewCell
        let mapItem = self.mapItems[indexPath.row]
        let location = Location(mapItem: mapItem)
        cell.goButton.tag = indexPath.row
        cell.destinationNameLabel.text = location.destinationName
        cell.addressLabel.text = location.address
        if (location.phoneNumber != nil) {
            cell.phoneNumberLabel.text = location.phoneNumber
        }
        cell.selectionStyle = .none
        return cell
    }
    
    /*
     Draws searched locations on the map
     */
    func drawLocation() {
        var count = 0
        var listAnnotation = [MKPointAnnotation]()
        for mapItem in mapItems{
            count += 1
            let sourceAnnotation = MKPointAnnotation()
            sourceAnnotation.title = String(count)
            sourceAnnotation.coordinate = mapItem.placemark.coordinate
            listAnnotation.append(sourceAnnotation)
        }
        self.mapView.showAnnotations(listAnnotation, animated: true)
    }
    
    /*
     When user sets destination, route delegate (MainMapVC) updates the trip.
     */
    @IBAction func onGoButton(_ sender: Any) {
        let button = sender as! UIButton
        self.routeDelegate?.updateRoute(index: button.tag)
        self.navigationController?.popViewController(animated: true)
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


