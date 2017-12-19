//
//  DangerRequest.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/18/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class DangerRequest: NSObject, Codable {
    var curLong: Double?
    var curLat: Double?
    var isDay: Int?
    
    init(curLat: Double, curLong: Double, isDay: Int){
        self.curLat = curLat
        self.curLong = curLong
        self.isDay = isDay
    }
}
