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
struct SignUpError: Error {
//    enum ErrorKind {
//        case InvalidUserId
//        case InvalidPassword
//        case InvalidContact
//        case InvalidEmergencyId
//        case InvalidEmergencyNumber
//    }
//
//    let kind: ErrorKind
    let status: Int
}

struct LoginError: Error {
    //    enum ErrorKind {
    //        case InvalidUserId
    //        case InvalidPassword
    //        case InvalidContact
    //        case InvalidEmergencyId
    //        case InvalidEmergencyNumber
    //    }
    //
    //    let kind: ErrorKind
    let status: Int
}

