//
//  RouteDelegate.swift
//  Walklive
//
//  Created by Michelle Shu on 12/1/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation
protocol RouteDelegate: class {
    func updateRoute(index: Int)
    func cancelTrip()
}
