//
//  ViewController.swift
//  Walklive
//
//  Login/Signup view
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
  
    // Local path where the user info is stored
    // For retrieving local data to check whether
    // a user has logged out
    private var userFilePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("UserInfo").path)
    }
    
    
    // Private Functions
    
    
    /*
     Loads user data when first opens app, if data exists, it means the user didn't log out
     */
    private func loadUserData() -> Bool {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: userFilePath) as? UserLogin {
            print("local data found:")
            self.userData = data
            return true
        } else {
            return false
        }
    }
    
    /*
     Checks authrization status and start update locations
     */
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
    
    
    // Public functions
    
    
    /*
     If the user didn't log out (there exists local data), then log the user in
     Else does nothing.
     */
    override func viewDidLoad() {
        super.viewDidLoad()
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

