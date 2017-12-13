//
//  RequestUpdater.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class TripUpdater: NSObject {
    private var timer = Timer()
    
    
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTimeLabel), userInfo: nil, repeats: true);
    }
    
    @objc func updateTimeLabel(){
        self.updatePanel()
//        backEndClient.updateTrip(success: {
//            print("updated!")
//        }, failure: { (error) in
//            print("update error")
//        }, timePoint: self.buildTimePoint()!)
    }
    
    func endTimer() {
        self.timer.invalidate()
    }
    
    func updatePanel(){

    }
}
