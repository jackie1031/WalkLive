//
//  TimeManager.swift
//  Walklive
//
//  Created by Michelle Shu on 12/4/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
import MapKit
import CoreLocation

class TimeManager: NSObject {
    private var timer = Timer()
    private var timeInterval: TimeInterval!
    private var usedTimeInterval = 0
    private var roadRequester: RoadRequester?
    var tripPanelDelegate: TripPanelDelegate?
    
    init(timeInterval: TimeInterval) {
        self.timeInterval = timeInterval
    }
    
    func convertTimeIntervalToMin(timeInterval: TimeInterval) -> Int{
        return Int(timeInterval/60)
    }
    
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTimeLabel), userInfo: nil, repeats: true);
    }
    
    @objc func updateTimeLabel(){
        self.usedTimeInterval += 1
        self.updatePanel()
    }
    
    func endTimer() {
        self.timer.invalidate()
    }
    
    func updatePanel(){
        self.tripPanelDelegate?.updateTripPanel(timeInfo: self.buildMessageOnCurrentTimer())
    }
    
    func buildMessageOnCurrentTimer() -> String{
        if (self.isOverTime() > 0) {
            return "Time Used (overtime): " + String(self.usedTimeInterval) + " min(s)"
        }
        return "Time Used: " + String(self.usedTimeInterval) + " min(s)"
    }
    
    func isOverTime() -> Int {
        if (self.usedTimeInterval > Int(self.timeInterval/60)) {
            return (self.usedTimeInterval - Int(self.timeInterval/60))
        }
        return 0
    }
    
    func buildTimePoint() -> TimePoint?{
        if (self.roadRequester == nil) {
            return nil
        }
        let sourceAnnotation = self.roadRequester?.sourceAnnotation
        let destinationAnnotation = self.roadRequester?.destinationAnnotation
        let currentAnnotation = self.roadRequester?.getcurrentLocationInAnnotation()
        let timePoint = TimePoint()
        
        timePoint.curLat = Double((currentAnnotation?.coordinate.latitude)!)
        timePoint.curLong = Double((currentAnnotation?.coordinate.longitude)!)
        timePoint.startLat = Double((sourceAnnotation?.coordinate.latitude)!)
        timePoint.startLong = Double((sourceAnnotation?.coordinate.longitude)!)
        timePoint.endLat = Double((destinationAnnotation?.coordinate.latitude)!)
        timePoint.endLong = Double((destinationAnnotation?.coordinate.longitude)!)
        
        timePoint.destination = (destinationAnnotation?.title)!
        timePoint.startTime = getTodayString()
        
        return timePoint
    }
    
}
