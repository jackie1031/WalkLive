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
        
        //            let userLogin = UserLogin(username: userNameTextField.text!, password: passwordTextField.text!)
        //            let encoder = JSONEncoder()
        //            do {
        //                let newUserLoginAsJSON = try encoder.encode(userLogin)
        //                userLoginUrlRequest.httpBody = newUserLoginAsJSON
        //            } catch {
        //                failure(error)
        //            }
        
        
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
        urlComponents.path = self.APICONTEXT + "/users/\(String(describing: User.currentUser?.userId))/friend_requests"
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
        urlComponents.path = self.APICONTEXT + "/users/\(String(describing: User.currentUser?.name))/friend_requests"
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
    
    func acceptFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(User.currentUser?.userName ?? "admin")/friend_requests/\(friendRequest.recipient)/accept"
        var acceptFriendRequest = URLRequest(url: urlComponents.url!)
        acceptFriendRequest.httpMethod = "DELETE"
        
        URLSession.shared.dataTask(with: acceptFriendRequest) { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            success()
        }
    }
    
    func declineFriendRequest(success: @escaping () -> (), failure: @escaping (Error) -> (), friendRequest: FriendRequest){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/\(User.currentUser?.userName ?? "nobody" )/friend_requests/\(friendRequest.recipient)/reject"
        var rejectFriendRequest = URLRequest(url: urlComponents.url!)
        rejectFriendRequest.httpMethod = "DELETE"
        
        URLSession.shared.dataTask(with: rejectFriendRequest) { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            success()
        }
    }
    
    func updateEmergencyContact(success: @escaping (EmergencyContact) -> (), failure: @escaping (Error) -> (), emergencyContact: EmergencyContact){
        var urlComponents = self.buildURLComponents()
        urlComponents.path = self.APICONTEXT + "/users/"
        var updateEmergencyContactRequest = URLRequest(url: urlComponents.url!)
        updateEmergencyContactRequest.httpMethod = "PUT"
        
        let encoder = JSONEncoder()
        do {
            let encodedJSON = try encoder.encode(emergencyContact)
            updateEmergencyContactRequest.httpBody = encodedJSON
        } catch {
            failure(error)
        }
        
        URLSession.shared.dataTask(with: updateEmergencyContactRequest) { (data, response, error) in
            if (error != nil) {
                failure(error!)
            }
            let updatedEmergencyContact = try? jsonDecoder.decode(EmergencyContact.self, from: data!) as EmergencyContact
            success(updatedEmergencyContact!)
        }
    }
    
//
//    let requestTest = FriendRequest()
//    let encoder = JSONEncoder()
//    let decoder = JSONDecoder()
//    do {
//    let encodedJSON = try encoder.encode(requestTest)
//    _ = try? decoder.decode(FriendRequest.self, from: encodedJSON) as FriendRequest
//    print(encodedJSON)
//    } catch {
//    print("error")
//    }
    
    
//    func startTrip(success: @escaping () -> (), failure: @escaping (Error) -> (), trip: Trip){
//        success()
//    }
}
