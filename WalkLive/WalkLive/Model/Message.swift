//
//  Message.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class Message NSObject {
    var messages : Array<String>
    var lastYCoor : Int
    
    init() {
        messages = Array()
    }
    
    func addMessage(m : String) {
        messages.append(m)
        lastYCoor += 40
    }
    
    func getLastYCoor() -> Int {
        return lastYCoor
    }
}
