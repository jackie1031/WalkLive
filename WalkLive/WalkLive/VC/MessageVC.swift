//
//  MessageVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class MessageVC: UIViewController {

    var message : Message
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Need to get message from local!!!
        message = Message()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        // need to get the coordinates of the last textfield
        message.getLastYcoor()
        var frame = CGRect.init(x: xCoor, y: yCoor, width: w, height: h)
        let text = UITextField(frame: frame)
        let view = UIView(frame: frame)
        view.addSubview(text)
        
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
