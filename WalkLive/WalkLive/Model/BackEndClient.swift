//
//  BackEndClient.swift
//  Walklive
//
//  Created by Michelle Shu on 12/4/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import Foundation

class BackEndClient: NSObject {
    
    
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
}
