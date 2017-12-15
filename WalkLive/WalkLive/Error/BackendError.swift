//
//  BackendError.swift
//  WalkLive
//
//  Created by Yang Cao on 12/5/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

//InvalidUserId    Code 401
//InvalidPassword Code 403
//InvalidContact Code 405
//InvalidEmergencyId    Code 406
//InvalidEmergencyNumber    Code 407
class SignUpError: Error {
//    enum ErrorKind {
//        case InvalidUserId
//        case InvalidPassword
//        case InvalidContact
//        case InvalidEmergencyId
//        case InvalidEmergencyNumber
//    }
//
//    let kind: ErrorKind
    var status: Int! = 0
    init(status: Int) {
        self.status = status
    }
    func returnReason() -> String {
        switch status {
        case 401:
            return "InvalidUserId: the username may be illegal or the username already exists!"
        case 403:
            return "InvalidPassword: the password may be illegal"
        case 405:
            return "InvalidContact: the contact number entered may be illegal"
        case 406:
            return "InvalidEmergencyId"
        case 407:
            return "InvalidEmergencyNumber"
        default:
            return "Unknown error"
        }
    }
}


class LoginError: Error {
//    InvalidUsername:Code: 401
//    IncorrectPassword:Code: 403
    var status: Int! = 0
    init(status: Int) {
        self.status = status
    }
    func returnReason() -> String {
        switch status {
//        case 401:
//            return "InvalidUserId: the username may be illegal or the username does not exist!"
        case 401:
            return "The username or password is incorrect!"
        case 403:
            return "InvalidPassword: the password may be illegal or inccorect"
        default:
            return "Unknown error" + String(status)
        }
    }
}

class RouteError: Error {
    //    InvalidUsername:Code: 401
    //    IncorrectPassword:Code: 403
    var status: Int! = 0
    init(status: Int) {
        self.status = status
    }
    func returnReason() -> String {
        switch status {
        case 401:
            return "InvalidUserId: the username may be illegal or the username does not exist!"
        case 403:
            return "InvalidPassword: the password may be illegal or inccorect"
        default:
            return "Unknown error" + String(status)
        }
    }
}
    
    class FriendRequestError: Error{
        var status: Int! = 0
        init(status: Int) {
            self.status = status
        }
        func returnReason() -> String {
            switch status {
            case 401:
                return "InvalidUserId: the username may be illegal or the username does not exist!"
            case 400:
                return "NonExistent_RequestId: cannot find this friend request or it has already been responded"
            default:
                return "Unknown error" + String(status)
            }
    }
}

class TripError: Error{
    var status: Int! = 0
    init(status: Int) {
        self.status = status
    }
    func returnReason() -> String {
        switch status {
        case 2:
            return "NonExistentTrip: the trip has already ended."
        case 402:
            return "NonExsistentTripId: cannot find this trip."
        default:
            return "Unknown error" + String(status)
        }
    }
}


