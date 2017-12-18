//
//  TimeManager.swift
//  Walklive
//
//  Keeps track of time and other trip info through a RoadRequester
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
    var usedTimeInterval = 0
    var roadRequester: RoadRequester?
    var tripPanelDelegate: TripPanelDelegate?
    var tripId: Int?
    
    // Initializes TimeManager object
    init(timeInterval: TimeInterval, roadRequester: RoadRequester) {
        self.timeInterval = timeInterval
        self.roadRequester = roadRequester
    }
    
    /*
     Converts time interval object to minutes
     - Parameters:
     - timeInterval: TimeInterval object
     - Returns:
     - number of minutes
     */
    func convertTimeIntervalToMin(timeInterval: TimeInterval) -> Int{
        return Int(timeInterval/60)
    }
    
    /*
     Starts timer to count time.
     - Parameters:
     - timeInterval: TimeInterval object
     */
    func startTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTimeLabel), userInfo: nil, repeats: true);
        if (self.roadRequester != nil) {
        backEndClient.startTrip(success: { (currentTimePoint) in
            self.tripId = currentTimePoint.tripId
        }, failure: { (error) in
            print("failure")
        }, timePoint: self.buildTimePoint()!)
        }
    }
    
    /*
     Continues counting time.
     */
    func continueTimer(timeInterval: TimeInterval) {
        //update every 60 seconds
        self.updatePanel()
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(updateTimeLabel), userInfo: nil, repeats: true);
    }
    
    /*
     Updates time label.
     */
    @objc func updateTimeLabel(){
        self.usedTimeInterval += 1
        self.updatePanel()
        backEndClient.updateTrip(success: {
            print("updated!")
        }, failure: { (error) in
            print("update error")
        }, timePoint: self.buildTimePoint()!)
    }
    
    /*
     Stops counting time.
     */
    func endTimer() {
        self.timer.invalidate()
    }
    
    /*
     Updates trip panel info
     */
    func updatePanel(){
        self.tripPanelDelegate?.updateTripPanel(timeInfo: self.buildMessageOnCurrentTimer())
    }
    
    /*
     Builds message about time spent and whether it's overtime.
     - Returns: message string about time
     */
    func buildMessageOnCurrentTimer() -> String{
        if (self.isOverTime() > 0) {
            return "Time Used (overtime): " + String(self.usedTimeInterval) + " min(s)"
        }
        return "Time Used: " + String(self.usedTimeInterval) + " min(s)"
    }
    
    /*
     Checks if it's overtime
     - Returns: number of minutes over
     */
    func isOverTime() -> Int {
        if (self.usedTimeInterval > Int(self.timeInterval/60)) {
            return (self.usedTimeInterval - Int(self.timeInterval/60))
        }
        return 0
    }
    
    /*
     Stops counting time.
     */
    func mapDangerZones(){
        let timePoint = self.buildTimePoint()
        let dangerRequest = DangerRequest(curLat: (timePoint?.curLat)!, curLong: (timePoint?.curLong)!, isDay: isDay())
        backEndClient.getDangerLevel(success: { (dangerInformation) in
            OperationQueue.main.addOperation {
            if (dangerInformation.clusters != nil){
                self.roadRequester?.drawDangerZones(clusters: dangerInformation.clusters!)
            }
            }
        }, failure: { (error) in
            
        }, dangerRequest: dangerRequest)
    }
    
    /*
     builds a TimePoint object
     - Returns: a TimePoint object with location, time, destination and other info
     */
    func buildTimePoint() -> TimePoint?{
        if (self.roadRequester == nil) {
            return nil
        }
        let sourceAnnotation = self.roadRequester?.sourceAnnotation
        let destinationAnnotation = self.roadRequester?.destinationAnnotation
        let currentAnnotation = self.roadRequester?.getcurrentLocationInAnnotation()
        let timePoint = TimePoint()
        timePoint.username = currentUserInfo.username
        timePoint.curLat = Double((currentAnnotation?.coordinate.latitude)!)
        timePoint.curLong = Double((currentAnnotation?.coordinate.longitude)!)
        timePoint.startLat = Double((sourceAnnotation?.coordinate.latitude)!)
        timePoint.startLong = Double((sourceAnnotation?.coordinate.longitude)!)
        timePoint.endLat = Double((destinationAnnotation?.coordinate.latitude)!)
        timePoint.endLong = Double((destinationAnnotation?.coordinate.longitude)!)
        timePoint.destination = (destinationAnnotation?.title)!
        timePoint.startTime = getTodayString()
        timePoint.emergencyNum = currentUserInfo.contact
        timePoint.timeSpent = self.buildMessageOnCurrentTimer()
        timePoint.address = self.roadRequester?.trip.address
        if (self.tripId != nil) {
            timePoint.tripId = self.tripId
        }
        return timePoint
    }
    
}
