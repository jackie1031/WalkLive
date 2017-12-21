//
//  BackEndClient.swift
//  Walklive
//
//  Created by Michelle Shu on 12/4/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

// Handles communications between frontend and backend
class BackEndClient: NSObject {
    let APICONTEXT = "/WalkLive/api"
    
    // This is the path where user data is stored locally
    private var userFilePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("UserInfo").path)
    }
    
    /*
     Saves user data if it's a successful login/signup.
     This ensures that if the user hasn't logged out, then the app will automatically
     log the user in by using the stored data.
     */
    private func saveUserData() {
        NSKeyedArchiver.archiveRootObject(currentUserInfo, toFile: userFilePath)
    }
    
    /*
     Builds URL.
     */
    private func buildURLComponents() -> URLComponents{
        var urlComponents = URLComponents()
        urlComponents.scheme = "https"
        urlComponents.host = "walklive.herokuapp.com"
        return urlComponents
    }
    


    /*
     A testing function to check deployment of Heroku, connecting backend to frontend,
     Also serves as a gerneral model right now.
     */
    func testEndPoint(success: @escaping () -> (), failure: @escaping () -> ()) {
        let endpoint = "https://walklive.herokuapp.com/WalkLive/api/tests"
        let url = URL(string: endpoint)
        var userLoginUrlRequest = URLRequest(url: url!)
        userLoginUrlRequest.httpMethod = "GET"

        
        URLSession.shared.dataTask(with: userLoginUrlRequest, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure()
            }
            if let httpResponse = response as? HTTPURLResponse { //?
                print("status code: \(httpResponse.statusCode)")
                success()
            }
            // if success, log in
        }).resume()
    }
    
    
    /**
     * ================================================================
     * Login, SignUp Session
     * ================================================================
     */
    
    /*
     loginAttempt
     Method: POST
     URL: /WalkLive/api/users/login
     Content: { username: [string], password: [string] }
     Failure Response:
     InvalidUsername                    Code 401
     Content: { reason: NONEXISTENT_USER }
     InvalidPassword                    Code 401
     Content: { reason: INCORRECT_PASSWORD}
     Success Response:                  Code 201
     Content: { username: [string], contact: [string], emergency_id: [string], emergency_number: [string] }
     */
    func loginAttempt(success: @escaping (UserLogin) -> (), failure: @escaping (LoginError) -> (), userLogin: UserLogin) {
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/login"
        var loginAttemptRequest = URLRequest(url: urlComponents.url!)
        loginAttemptRequest.httpMethod = "POST"

        do {
            let newUserLoginAsJSON = try jsonEncoder.encode(userLogin)
            loginAttemptRequest.httpBody = newUserLoginAsJSON
        } catch {
            failure(LoginError(status: 0))
        }
        
        URLSession.shared.dataTask(with: loginAttemptRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(LoginError(status: 0))
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(LoginError(status: status))
            } else {
                print(status)
            let userContact = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
                currentUserInfo = UserLogin(username: (userContact?.username)!, password: userLogin.password!, contact: (userContact?.contact)!)
                
                self.saveUserData()
                success(userContact!)}
        }).resume()
    }
    
    /*
     signUpAttempt
     Method: POST
     URL: /WalkLive/api/users
     Content: { userLogin: [string] }
     Failure Response:
     InvalidUsername            Code 401
     Content: { reason: NONEXISTENT_USER }
     InvalidPassword            Code 401
     Content: { reason: INCORRECT_PASSWORD }
     InvalidContact             Code 400
     Content: { reason: INVALID_CONTACT_NUMBER }
     Success Response:         Code 201
     Content: { username: [string], contact:[string] }
     */
    func signUpAttempt(success: @escaping (UserLogin) -> (), failure: @escaping (SignUpError) -> (), userLogin: UserLogin) {
        var urlComponents = self.buildURLComponents()
        //CHANGE ENDPOINT
        urlComponents.path = self.APICONTEXT + "/users"
        var signUpRequest = URLRequest(url: urlComponents.url!)
        signUpRequest.httpMethod = "POST"
        
        do {
            let newUserSignUpAsJSON = try jsonEncoder.encode(userLogin)
            signUpRequest.httpBody = newUserSignUpAsJSON
        } catch {
            failure(SignUpError(status:0))
        }
        URLSession.shared.dataTask(with: signUpRequest, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(SignUpError(status:0))
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 201) {
                failure(SignUpError(status: status))
            } else {
                let userContact = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
                currentUserInfo = userLogin
                self.saveUserData()
                success(userContact!)
            }
        }).resume()
    }

    
    /**
     * ================================================================
     * Friend Request Section
     * ================================================================
     */
    
    /*
     createFriendRequest
     Method: POST
     URL: /WalkLive/api/users/[username]/friend_requests
     Content: { recipient: [string] }
     Failure Response:
     InvalidUsername    Code 404
     Content: { reason: NONEXISTENT_USER }
     InvalidRecipient    Code 400
     Content: { reason: NONEXISTENT_RECIPIENT }
     InvalidDate Code 400
     Content: { reason: INVALID_DATE_FORMAT }
     Success Response:    Code 201
     Content: {}
    */
    func createFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username!)/friend_requests"
        var makeFriendRequest = URLRequest(url: urlComponents.url!)
        makeFriendRequest.httpMethod = "POST"
        
        let encoder = JSONEncoder()
        do {
            let encodedJSON = try encoder.encode(friendRequest)
            makeFriendRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }
        URLSession.shared.dataTask(with: makeFriendRequest, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(SignUpError(status:0))
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 201) {
                failure(SignUpError(status: status))
            } else {
                success()}
        }).resume()
        
    }
    
    /*
     acceptFriendRequest
     Method: PUT
     URL: /WalkLive/api/users/[username]/friend_requests/[requestId]/accept
     Content: {}
     Failure Response:
     InvalidUsername    Code 401
     Content: { reason: NONEXISTENT_USER }
     InvalidRequestId   Code 400
     Content: { reason: NONEXISTENT_RECIPIENT }
     Success Response:  Code 200
     Content: {}
     */
    func acceptFriendRequest(success: @escaping () -> (), failure: @escaping (FriendRequestError) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username!)/friend_requests/\(friendRequest._id!)/accept"
        var acceptFriendRequest = URLRequest(url: urlComponents.url!)
        acceptFriendRequest.httpMethod = "PUT"
        
        print(urlComponents.path)
        
        URLSession.shared.dataTask(with: acceptFriendRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(FriendRequestError(status: 0))
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(FriendRequestError(status: status))
            } else {
                print(status)
                success()
            }
        }).resume()
    }
    
    /*
     rejectFriendRequest
     Method: PUT
     URL: /WalkLive/api/users/[username]/friend_requests/[requestId]/reject
     Content: {}
     Failure Response:
     InvalidUsername    Code 401
     Content: { reason: NONEXISTENT_USER }
     InvalidRequestId   Code 400
     Content: { reason: NONEXISTENT_RECIPIENT }
     Success Response:  Code 200
     Content: {}
     */
    func rejectFriendRequest(success: @escaping () -> (), failure: @escaping (FriendRequestError) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username!)/friend_requests/\(friendRequest._id!)/reject"
        
        print(urlComponents.path)
        var rejectFriendRequest = URLRequest(url: urlComponents.url!)
        rejectFriendRequest.httpMethod = "PUT"
        
        URLSession.shared.dataTask(with: rejectFriendRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(FriendRequestError(status: 0))
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(FriendRequestError(status: status))
            } else {
                print(status)
                success()
            }
        }).resume()
    }
    
    /*
     getSentFriendRequests:
     
     Method: GET
     URL: /WalkLive/api/users/[username]/sent_friend_requests
     Content: {}
     Failure Response:
     InvalidUsername    Code 401
     Content: { reason: NONEXISTENT_USER }
     Success Response:    Code 200
     Content: { <friendRequest 1>, <friendRequest 2>, ...}
     */
    func getSentFriendRequests(success: @escaping ([FriendRequest]?) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username ?? "nobody")/sent_friend_requests"
        var getSentFriendRequests = URLRequest(url: urlComponents.url!)
        getSentFriendRequests.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getSentFriendRequests, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(LoginError(status: status))
            } else {
                let friendRequests = try? jsonDecoder.decode([FriendRequest].self, from: data!) as [FriendRequest]
                success(friendRequests)
            }
        }).resume()
    }
    
    /*
     getReceivedFriendRequests:
     Method: GET
     URL: /WalkLive/api/users/[username]/friend_requests
     Content: {}
     Failure Response:
     InvalidUsername    Code 401
     Content: { reason: NONEXISTENT_USER }
     Success Response:    Code 200
     Content: { <friendRequest 1>, <friendRequest 2>, ...}
     */
    func getReceivedFriendRequests(success: @escaping ([FriendRequest]) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username ?? "nobody")/friend_requests"
        print(urlComponents.path)
        var getReceivedFriendRequests = URLRequest(url: urlComponents.url!)
       getReceivedFriendRequests.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getReceivedFriendRequests, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                failure(LoginError(status: status))
            } else {
                let friendRequests = try? jsonDecoder.decode([FriendRequest].self, from: data!) as [FriendRequest]
                success(friendRequests!)
            }
        }).resume()
    }
    
    /*
     getFriendList:
     Method: GET
     URL: /WalkLive/api/users/[username]/friends
     Content: {}
     Failure Response:
     InvalidUsername    Code 401
     Content: { reason: NONEXISTENT_USER }
     Success Response:    Code 200
     Content: { <{ username: [string], contact: [string] }>, <user 2>, ...}
     */
    func getFriendList(success: @escaping ([Friend]?) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username ?? "nobody" )/friends"
        var getFriendList = URLRequest(url: urlComponents.url!)
        getFriendList.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getFriendList, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                failure(LoginError(status: status))
            } else {
                let friends = try? jsonDecoder.decode([Friend].self, from: data!) as [Friend]
                success(friends)
            }
        }).resume()
    }
    
    
    
    /**
     * ================================================================
     * Setting Session
     * ================================================================
     */
    
    /*
     updateEmergencyContact
     Method: PUT
     URL: /WalkLive/api/users/[username]/emergency_info
     Content: { emergency_id: [string], emergency_number: [string] }
     Failure Response:
     InvalidUsername            Code 401
     Content: { reason: NONEXISTENT_USER }
     InvalidEmergencyId         Code 400
     Content: { reason: NONEXISTENT_USER }
     InvalidEmergencyNumber     code 400
     Content: { reason: INVALID_CONTACT_NUMBER }
     Success Response:          Code 200
     Content: { emergency_id: [string], emergency_number: [string] }
     */
    func updateEmergencyContact(success: @escaping (EmergencyContact) -> (), failure: @escaping (Error) -> (), emergencyContact: EmergencyContact){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username ?? "nobody")/emergency_info"
        var updateEmergencyContactRequest = URLRequest(url: urlComponents.url!)
        updateEmergencyContactRequest.httpMethod = "PUT"
        
        do {
            let encodedJSON = try jsonEncoder.encode(emergencyContact)
            updateEmergencyContactRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }

        URLSession.shared.dataTask(with: updateEmergencyContactRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(LoginError(status: status))
            } else {
                print(status)
                let returnEmergencyContact = try? jsonDecoder.decode(EmergencyContact.self, from: data!) as EmergencyContact
                currentUserInfo.emergency_id = returnEmergencyContact?.emergency_id
                currentUserInfo.emergency_number = returnEmergencyContact?.emergency_number
                success(returnEmergencyContact!)}
        }).resume()
    }
    
    /*/users/:username/contact_info
     */
    func updateUserContact(success: @escaping (UserLogin) -> (), failure: @escaping (Error) -> (), userLogin: UserLogin){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(currentUserInfo.username ?? "nobody")/contact_info"
        var updateEmergencyContactRequest = URLRequest(url: urlComponents.url!)
        updateEmergencyContactRequest.httpMethod = "PUT"
        
        do {
            let encodedJSON = try jsonEncoder.encode(userLogin)
            updateEmergencyContactRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: updateEmergencyContactRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(LoginError(status: status))
            } else {
                print(status)
                let returnUser = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
                currentUserInfo.contact = returnUser?.contact
                success(returnUser!)}
        }).resume()
    }
    
    
    /**
     * ================================================================
     * Users Session
     * ================================================================
     */
    
    /*
     getUser
     Method: GET
     URL: /WalkLive/api/users/[username]
     Content: {}
     Failure Response:
     InvalidUsername    Code 404
     Content: { reason: NONEXISTENT_USER }
     Success Response:  Code 200
     Content: { username: [string], contact: [string], emergency_id: [string], emergency_number: [string] }
     */
    func getUser(success: @escaping (UserLogin) -> (), failure: @escaping (Error) -> (), username: String){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(username)"
        var getUserRequest = URLRequest(url: urlComponents.url!)
        getUserRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getUserRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            
            if (status != 200) {
                failure(SignUpError(status: status))
            } else {
            let userContact = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
                success(userContact!)}
        }).resume()
    }
    
    /*
     getUsers
     Method: GET
     URL: /WalkLive/api/users
     Content: {}
     Failure Response:
     Internal Error       Code 410
     Content: { reason: NONEXISTENT_USER }
     Success Response:    Code 200
     Content: { <user 1>, <user 2>, ...}
     */
    func getUsers(success: @escaping ([UserLogin]) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users"
        var getUserRequest = URLRequest(url: urlComponents.url!)
        getUserRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getUserRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            
            if (status != 200) {
                failure(SignUpError(status: status))
            } else {
                let userContact = try? jsonDecoder.decode([UserLogin].self, from: data!) as [UserLogin]
                success(userContact!)}
        }).resume()
    }
    
    
    /**
     * ================================================================
     * Trip Session
     * ================================================================
     */
    
    /*
     startTrip
     Method: POST
     URL: /WalkLive/api/trips
     Content: {timePoint : TimePoint}
     Failure Response:
     InvalidDestination    Code 409
     Content: { reason: INVALID_DESTINATION }
     Success Response:    Code 200
     Content: {}
     */
    func startTrip(success: @escaping (TimePoint) -> (), failure: @escaping (Error) -> (), timePoint: TimePoint){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips"
        var startTripRequest = URLRequest(url: urlComponents.url!)
        startTripRequest.httpMethod = "POST"
        
        do {
            let startTripJSON = try jsonEncoder.encode(timePoint)
            startTripRequest.httpBody = startTripJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: startTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(LoginError(status: status))
            } else {
                let timePoint = try? jsonDecoder.decode(TimePoint.self, from: data!) as TimePoint
                success(timePoint!)}
        }).resume()
    }
    
    /*
     endTrip
     Method: PUT
     URL: /WalkLive/api/trips/[tripId]/endtrip
     Content: {timePoint : [string]}
     Failure Response:
     InvalidTargetID    Code 402
     Content: { reason: INVALID_DESTINATION }
     Success Response:    Code 200
     Content: {}
     */
    func endTrip(success: @escaping () -> (), failure: @escaping (Error) -> (), timePoint: TimePoint){
        if (timePoint.tripId == nil){
            failure(TripError(status:2))
        }
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/\(timePoint.tripId!)/endtrip"
        var endTripRequest = URLRequest(url: urlComponents.url!)
        endTripRequest.httpMethod = "PUT"
        
        do {
            let startTripJSON = try jsonEncoder.encode(timePoint)
            endTripRequest.httpBody = startTripJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: endTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                success()
            }
        }).resume()
    }
    
    /*
     updateTrip
     Method: PUT
     URL: /WalkLive/api/trips/[tripId]/update
     Content: {curLong:[double],curLat:[double],timeSpent:[String]}
     Failure Response:
     InvalidTargetID    Code 402
     Content: { reason: INVALID_TARGETID }
     Success Response:    Code 200
     Content: {}
     */
    func updateTrip(success: @escaping () -> (), failure: @escaping (Error) -> (), timePoint: TimePoint){
        if (timePoint.tripId == nil){
            failure(TripError(status:2))
        }
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/\(timePoint.tripId!)/update"
        var endTripRequest = URLRequest(url: urlComponents.url!)
        endTripRequest.httpMethod = "PUT"
        
        do {
            let startTripJSON = try jsonEncoder.encode(timePoint)
            endTripRequest.httpBody = startTripJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: endTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                success()
            }
        }).resume()
        
    }
    
    /*
     getAllTrip
     Method: GET
     URL: /WalkLive/api/trips/[username]/allTrips
     Content: {}
     Failure Response:
     InvalidTargetId    Code 402
     Content: { reason: INVALID_TARGETID }
     Success Response:    Code 200
     Content: { <trip 1>, <trip 2>, <trip 3>, ... }
     */
    func getAllTrip(success: @escaping ([TimePoint]?) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/\(currentUserInfo.username!)/allTrips"
        var getAllTripRequest = URLRequest(url: urlComponents.url!)
        getAllTripRequest.httpMethod = "GET"
        
        
        URLSession.shared.dataTask(with: getAllTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                let timePoints = try? jsonDecoder.decode([TimePoint].self, from: data!) as [TimePoint]
                success(timePoints)}
        }).resume()
    }
    
    /*
     getSingleTrip
     Method: GET
     URL: /WalkLive/api/trips/[tripId]
     Content: {}
     Failure Response:
     InvalidTripID    Code 402
     Content: { reason: INVALID_TRIPID }
     Success Response:    Code 200
     Content: { <timePoint> }
     */
    func getSingleTrip(success: @escaping (TimePoint) -> (), failure: @escaping (Error) -> (), tripId: Int){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/getById/\(tripId)"
        var getSingleTripRequest = URLRequest(url: urlComponents.url!)
        getSingleTripRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getSingleTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                let timePoint = try? jsonDecoder.decode(TimePoint.self, from: data!) as TimePoint
                success(timePoint!)}
        }).resume()
        
    }
    
    /*
     getTripHistory
     Method: GET
     URL: /WalkLive/api/trips/[username]/tripHistory
     Content: {}
     Failure Response:
     InvalidTargetID    Code 402
     Success Response:    Code 200
     Content: { <trip 1>, <trip 2>, ... }
     */
    func getTripHistory(success: @escaping ([TimePoint]) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/\(currentUserInfo.username!)/tripHistory"
        var getSingleTripRequest = URLRequest(url: urlComponents.url!)
        getSingleTripRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getSingleTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                let timePoint = try? jsonDecoder.decode([TimePoint].self, from: data!) as [TimePoint]
                success(timePoint!)}
        }).resume()
        
    }
    
    /*
     getTripHistory
     Method: GET
     URL: /WalkLive/api/trips/getByName/[username]
     Content: {}
     Failure Response:
     InvalidTargetID    Code 402
     Success Response:    Code 200
     Content: { <trip> }
     */
    func getOngoingTrip(success: @escaping (TimePoint) -> (), failure: @escaping (Error) -> ()){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/trips/getByName/\(currentUserInfo.username!)"
        var getSingleTripRequest = URLRequest(url: urlComponents.url!)
        getSingleTripRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getSingleTripRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                let timePoint = try? jsonDecoder.decode(TimePoint.self, from: data!) as TimePoint
                success(timePoint!)}
        }).resume()
        
    }
    
    /**
     * ================================================================
     * Danger Mapping Session
     * ================================================================
     */
    func getDangerLevel(success: @escaping (DangerInformation) -> (), failure: @escaping (Error) -> (), dangerRequest: DangerRequest) {
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/crime/\(dangerRequest.curLat!)/\(dangerRequest.curLong!)/\(dangerRequest.isDay!)"
        print(urlComponents.path)
        var getDangerLevelRequest = URLRequest(url: urlComponents.url!)
        getDangerLevelRequest.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: getDangerLevelRequest, completionHandler: {
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                print(status)
                failure(TripError(status: status))
            } else {
                print(status)
                let dangerInformation = try? jsonDecoder.decode(DangerInformation.self, from: data!) as DangerInformation
                success(dangerInformation!)}
        }).resume()
    }
}
