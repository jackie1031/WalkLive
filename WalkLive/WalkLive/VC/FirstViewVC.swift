//
//  ViewController.swift
//  Walklive
//
//  Created by Michelle Shu on 9/27/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FirstViewVC: UIViewController, GIDSignInUIDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        GIDSignIn.sharedInstance().uiDelegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

