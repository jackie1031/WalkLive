//
//  Service.swift
//  Walklive
//
//  Created by Michelle Shu on 12/2/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

extension UIColor {
    convenience init(red: Int, green: Int, blue: Int) {
        assert(red >= 0 && red <= 255, "Invalid red component")
        assert(green >= 0 && green <= 255, "Invalid green component")
        assert(blue >= 0 && blue <= 255, "Invalid blue component")
        
        self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
    }
    
    convenience init(netHex: Int) {
        self.init(red:(netHex >> 16) & 0xff, green:(netHex >> 8) & 0xff, blue:netHex & 0xff)
    }
}

func getTodayString() -> String{
    
    let date = Date()
    let calender = Calendar.current
    let components = calender.dateComponents([.year,.month,.day,.hour,.minute,.second], from: date)

    let today_string = String(components.year!) + "-" + String(components.month!) + "-" + String(components.day!) + " " + String(components.hour!)  + ":" + String(components.minute!) + ":" +  String(components.second!)
    
    return today_string
}

func isDay() -> Int{
    let date = Date()
    let calender = Calendar.current
    let components = calender.dateComponents([.year,.month,.day,.hour,.minute,.second], from: date)
    let hour = components.hour!
    if (hour >= 9 && hour <= 21) {
        return 1
    }
    return 0

}

func convertDegreeToMeters(degree: Double) -> Double {
   return 1000 * degree
}

extension String {
    var westernArabicNumeralsOnly: String {
        let pattern = UnicodeScalar("0")..."9"
        return String(unicodeScalars
            .flatMap { pattern ~= $0 ? Character($0) : nil })
    }
}


let primaryColor = UIColor(netHex: 0x94CFC9)

let level0Color = UIColor(netHex: 0x8df265)
let level1Color = UIColor(netHex: 0xfffb72)
let level2Color = UIColor(netHex: 0xffb200)
let level3Color = UIColor(netHex: 0xff9400)
let level4Color = UIColor(netHex: 0xff7700)
let level5Color = UIColor(netHex: 0xFF0000)

let backEndClient = BackEndClient()
let jsonEncoder = JSONEncoder()
let jsonDecoder = JSONDecoder()
var currentUserInfo: UserLogin!
var messages = Message()
let stringBuilder = StringBuilder()
let warnigSignFactory = WarningSignFactory()
var overarchTimeManager: TimeManager!
