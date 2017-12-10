//
//  MessageVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class MessageVC: UIViewController {

    var message : Message!
    var textFields : Array<UITextField>!
    var lastYCoor : Int!
    
    @IBOutlet weak var addTextButton: UIButton!
    @IBOutlet weak var delTexButton: UIButton!
    @IBOutlet weak var message1: UITextField!
    @IBOutlet weak var message2: UITextField!
    @IBOutlet weak var message3: UITextField!
    @IBOutlet weak var message4: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Need to get message from local!!!
        message = Message()
        textFields = Array()
        textFields.append(message1)
        textFields.append(message2)
        textFields.append(message3)
        textFields.append(message4)
        self.lastYCoor = 150
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        // move add and del buttons down
        var addButtonFrame : CGRect = addTextButton.frame
        var delButtonFrame : CGRect = delTexButton.frame
        addButtonFrame.origin.y = addButtonFrame.origin.y + 40
        delButtonFrame.origin.y = delButtonFrame.origin.y + 40
        
        // create new label
        lastYCoor = lastYCoor + 40
        let newTextField = UITextField(frame: CGRect(x: 10, y: lastYCoor, width: 357, height: 30))
        newTextField.placeholder = "Enter text here"
        self.view.addSubview(newTextField)
        
        // add new message to Message object
        message.addMessage(m: "")
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        // delete last label
        var lastTextField = textFields.last
        lastTextField?.removeFromSuperview()
        message.deleteLastMessage()
        textFields.removeLast()
        lastYCoor = lastYCoor - 40
        // move add and del buttons up
        var addButtonFrame : CGRect = addTextButton.frame
        var delButtonFrame : CGRect = delTexButton.frame
        addButtonFrame.origin.y = addButtonFrame.origin.y - 40
        delButtonFrame.origin.y = delButtonFrame.origin.y - 40
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
