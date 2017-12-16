//
//  RequestUpdater.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import UIKit
class TripUpdater: NSObject {
    private var timer = Timer()
    private var tripTableDelegate: TripTableUpdateDelegate!
    
    init(tripTableDelegate: TripTableUpdateDelegate){
        self.tripTableDelegate = tripTableDelegate
    }
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTimeLabel), userInfo: nil, repeats: true);
    }
    
    @objc func updateTimeLabel(){
        self.tripTableDelegate.updateTable()
    }
    
    func endTimer() {
        self.timer.invalidate()
    }
    
}
