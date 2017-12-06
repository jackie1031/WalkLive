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
        let encoder = JSONEncoder()
        let decoder = JSONDecoder()
        do {
            let encodedJSON = try encoder.encode(requestTest)
            _ = try? decoder.decode(FriendRequest.self, from: encodedJSON) as FriendRequest
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
}
