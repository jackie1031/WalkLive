//
//  DangerInformation.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/18/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
class DangerInformation: NSObject, Codable {
    var dangerLevel: Int?
    var dangerClusters: [DangerCluster]?
}
