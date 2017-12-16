//
//  ViewController.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class FirstViewVC: UIViewController, CLLocationManagerDelegate {

    var roadRequester = RoadRequester()
    var locationManager =  CLLocationManager()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.authorizeLocationUpdate()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
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

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

