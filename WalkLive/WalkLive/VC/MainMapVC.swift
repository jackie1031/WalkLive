//
//  MainMapVC.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import GoogleMaps

class MainMapVC: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func loadView() {
        // Create a GMSCameraPosition that tells the map to display the
        // coordinate 39.3299,76.6205 at zoom level 12.
        let camera = GMSCameraPosition.camera(withLatitude: 39.3299, longitude: -76.6205, zoom: 12.0)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        mapView.settings.myLocationButton = true
        mapView.settings.scrollGestures = true
        mapView.settings.zoomGestures = true
        view = mapView
        
        // Creates a marker in the center of the map.
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: 39.3299, longitude: -76.6205)
        marker.title = "Johns Hopkins University"
        marker.snippet = "Baltimore, MD"
        marker.map = mapView
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
