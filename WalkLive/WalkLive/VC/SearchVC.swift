//
//  SearchVC.swift
//  Walklive
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.mapItems != nil {
            return self.mapItems.count
        } else {
            return 0
        }
    }
    
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


