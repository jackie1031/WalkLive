//
//  BackEndClient.swift
//  Walklive
//
//  Created by Michelle Shu on 12/4/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class BackEndClient: NSObject {
    let APICONTEXT = "/WalkLive/api"
    var user: User!
    
    private func buildURLComponents() -> URLComponents{
        var urlComponents = URLComponents()
        urlComponents.scheme = "https"
        urlComponents.host = "walklive.herokuapp.com"
        return urlComponents
    }
    
    func encoderDecoderTest(){
        let requestTest = FriendRequest()
        let requestTestTwo = FriendRequest()
        let encoder = JSONEncoder()
        let decoder = JSONDecoder()
        do {
            let encodedJSON = try encoder.encode(requestTest)
            _ = try? decoder.decode(FriendRequest.self, from: encodedJSON) as FriendRequest
            print(encodedJSON)
        } catch {
            print("error")
        }
        
        var requestList = [FriendRequest]()
        requestList.append(requestTest)
        requestList.append(requestTestTwo)
        do {
            let encodedJSON = try encoder.encode(requestList)
            let decodedList = try? decoder.decode([FriendRequest].self, from: encodedJSON) as [FriendRequest]
            print(encodedJSON)
        } catch {
            print("error")
        }
    }
    
    // A testing function to check deployment of Heroku, connecting backend to frontend,
    // Also serves as a gerneral model right now.
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
    
    func makeFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(String(describing: User.currentUser?.username))/friend_requests"
        var makeFriendRequest = URLRequest(url: urlComponents.url!)
        makeFriendRequest.httpMethod = "POST"
        
        let encoder = JSONEncoder()
        do {
            let encodedJSON = try encoder.encode(friendRequest)
            makeFriendRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: makeFriendRequest) { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            success()
        }
    }
    
    func respondFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(String(describing: User.currentUser?.username))/friend_requests"
        var makeFriendRequest = URLRequest(url: urlComponents.url!)
        makeFriendRequest.httpMethod = "POST"
        
        let encoder = JSONEncoder()
        do {
            let encodedJSON = try encoder.encode(friendRequest)
            makeFriendRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: makeFriendRequest) { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            success()
        }
    }
    
    func loginAttempt(success: @escaping (UserLogin) -> (), failure: @escaping (Error) -> (), userLogin: UserLogin) {
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/login"
        var loginAttemptRequest = URLRequest(url: urlComponents.url!)
        loginAttemptRequest.httpMethod = "POST"

        do {
            let newUserLoginAsJSON = try jsonEncoder.encode(userLogin)
            loginAttemptRequest.httpBody = newUserLoginAsJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: loginAttemptRequest, completionHandler: {
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
                print(status)
            let userContact = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
            currentUserInfo = userContact
                success(userContact!)}
        }).resume()
    }
    
    func signUpAttempt(success: @escaping (UserLogin) -> (), failure: @escaping (Error) -> (), userLogin: UserLogin) {
        var urlComponents = self.buildURLComponents()
        //CHANGE ENDPOINT
        urlComponents.path = self.APICONTEXT + "/users"
        var signUpRequest = URLRequest(url: urlComponents.url!)
        signUpRequest.httpMethod = "POST"
        
        do {
            let newUserSignUpAsJSON = try jsonEncoder.encode(userLogin)
            signUpRequest.httpBody = newUserSignUpAsJSON
        } catch {
            failure(error)
        }
        URLSession.shared.dataTask(with: signUpRequest, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 201) {
                failure(SignUpError(status: status))
            } else {
            let userContact = try? jsonDecoder.decode(UserLogin.self, from: data!) as UserLogin
            currentUserInfo = userContact
                success(userContact!)}
        }).resume()
    }
    
    func saveAttempt(success: @escaping () -> (), failure: @escaping (Error) -> (), phoneNum: String, emergencyContact: String) {
//        let endpoint = "."
//        guard let url = URL(string: endpoint) else {
//            print("Error: cannot create URL")
//            let error = BackendError.urlError("Could not construct URL")
//            failure(error)
//        }
        
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(User.currentUser?.username ?? "nobody")/friend_requests"
        var userLoginUrlRequest = URLRequest(url: urlComponents.url!)
        userLoginUrlRequest.httpMethod = "POST"
        
        let keys = ["phoneNum", "emergencyContact"]
        let values = [phoneNum, emergencyContact]
        let userDict = NSDictionary.init(objects: keys, forKeys: values as [NSCopying])
        self.user = User(dictionary: userDict)
        let encoder = JSONEncoder()
        do {
            let newUserLoginAsJSON = try encoder.encode(user)
            userLoginUrlRequest.httpBody = newUserLoginAsJSON
        } catch {
            failure(error)
        }
      
        URLSession.shared.dataTask(with: userLoginUrlRequest, completionHandler: { //?
            (data, response, error) in
            // check for errors
            if error != nil {
                failure(error!)
            }
            if let httpResponse = response as? HTTPURLResponse { //?
                print("status code: \(httpResponse.statusCode)")
                failure(error!)
            }
            // if success, log in
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                failure(LoginError(status: status))
            } else {
                success()
            }
        }).resume()
    }

    func acceptFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(User.currentUser?.username ?? "default")/friend_requests/\(friendRequest.recipient)/accept"
        var acceptFriendRequest = URLRequest(url: urlComponents.url!)
        acceptFriendRequest.httpMethod = "DELETE"
        
        URLSession.shared.dataTask(with: acceptFriendRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                failure(LoginError(status: status))
            } else {
                success()
            }
        }).resume()
    }
    
    func declineFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(User.currentUser?.username ?? "nobody" )/friend_requests/\(friendRequest.recipient)/reject"
        var rejectFriendRequest = URLRequest(url: urlComponents.url!)
        rejectFriendRequest.httpMethod = "DELETE"
        
        URLSession.shared.dataTask(with: rejectFriendRequest, completionHandler: { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            
            
            let status = (response as! HTTPURLResponse).statusCode
            if (status != 200) {
                failure(LoginError(status: status))
            } else {
                success()
            }
        }).resume()
    }
    
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
    

    
    func startTrip(success: @escaping () -> (), failure: @escaping (Error) -> (), timePoint: TimePoint){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/start_trip"
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
                print(status)
                    success()
            }
        }).resume()
    }
    

}
