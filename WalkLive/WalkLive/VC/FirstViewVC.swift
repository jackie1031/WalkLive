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
    
    private var userData : UserLogin?
    var roadRequester = RoadRequester()
    var locationManager =  CLLocationManager()  
  
    private var userFilePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("UserInfo").path)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // clear data for testing
//        do {
//            try FileManager.default.removeItem(atPath: userFilePath)
//        } catch {
//            print("No data found.")
//        }
        self.authorizeLocationUpdate()
        if (self.loadUserData()) {
            backEndClient.loginAttempt(success: { (userInfo) in
                OperationQueue.main.addOperation {
                    self.performSegue(withIdentifier: "directLoginSegue", sender: nil)
                }
            }, failure: { (error) in
                OperationQueue.main.addOperation {
                    let errorSign = warnigSignFactory.makeAutoLoginWarningSign(loginError: error)
                    errorSign.center = self.view.center
                    self.view.addSubview(errorSign)
                }
            }, userLogin: userData!)
        }
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

    // load user data when first opens app
    private func loadUserData() -> Bool {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: userFilePath) as? UserLogin {
            print("local data found:")
            self.userData = data
            return true
        } else {
            return false
        }
    }

}

