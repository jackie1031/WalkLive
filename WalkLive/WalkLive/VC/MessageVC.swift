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
    
    var filePath: String {
        //1 - manager lets you examine contents of a files and folders in your app; creates a directory to where we are saving it
        let manager = FileManager.default
        //2 - this returns an array of urls from our documentDirectory and we take the first path
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        //print("this is the url path in the documentDirectory \(url)")
        //3 - creates a new path component and creates a new file called "Data" which is where we will store our Data array.
        return (url!.appendingPathComponent("Data").path)
    }
    
    
    @IBOutlet weak var messageEditPanel: UIView!
    
    @IBOutlet weak var addTextButton: UIButton!
    @IBOutlet weak var delTextButton: UIButton!
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
        let yCoor:Int = Int(message4.frame.maxY)
//        print(yCoor)
        self.lastYCoor = yCoor
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        // move add and del buttons down
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y + 40
//        let addButtonFrame : CGRect = addTextButton.frame
//        let delButtonFrame : CGRect = delTextButton.frame
//        addTextButton.frame.origin.y = addButtonFrame.origin.y + 40
//        delTextButton.frame.origin.y = delButtonFrame.origin.y + 40
        
        // create new label
        lastYCoor = lastYCoor + 40
        let newTextField = UITextField(frame: CGRect(x: 10, y: lastYCoor+30, width: 357, height: 30))
        newTextField.placeholder = "Enter text message here"
        newTextField.font = UIFont.systemFont(ofSize: 15)
        newTextField.borderStyle = UITextBorderStyle.roundedRect
        newTextField.autocorrectionType = UITextAutocorrectionType.no
        newTextField.keyboardType = UIKeyboardType.default
        newTextField.returnKeyType = UIReturnKeyType.done
        newTextField.clearButtonMode = UITextFieldViewMode.whileEditing
        newTextField.contentVerticalAlignment = UIControlContentVerticalAlignment.center
        self.view.addSubview(newTextField)
        textFields.append(newTextField)
//        let yCoor:Int = Int(newTextField.frame.maxY)
//        print(yCoor)
        // add new message to Message object?
        message.addMessage(m: "")
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        // delete last label
        let lastTextField = textFields.last
        lastTextField?.removeFromSuperview()
        message.deleteLastMessage()
        textFields.removeLast()
        lastYCoor = lastYCoor - 40
        // move add and del buttons up
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y - 40
//        let addButtonFrame : CGRect = addTextButton.frame
//        let delButtonFrame : CGRect = delTextButton.frame
//        addTextButton.frame.origin.y = addButtonFrame.origin.y - 40
//        delTextButton.frame.origin.y = delButtonFrame.origin.y - 40
    }
    
    @IBAction func onSaveButton(_ sender: Any) {
        var messageSegments = Array<String>()
        for textField in textFields {
            messageSegments.append(textField.text!)
        }
        message.updateMessages(updatedMessages: messageSegments)
        saveData()
        loadData()
    }
    
    private func saveData() {
        //4 - NSKeyedArchiver is going to look in every shopping list class and look for encode function and is going to encode our data and save it to our file path.  This does everything for encoding and decoding.
        //5 - archive root object saves our array of shopping items (our data) to our filepath url
        NSKeyedArchiver.archiveRootObject(self.message, toFile: filePath)
    }
    
    // when do we call this function?
    private func loadData() {
        //6 - if we can get back our data from our archives (load our data), get our data along our file path and cast it as an array of ShoppingItems
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            self.message = data
        }
        for messageSegment in self.message.getMessages() {
            print(messageSegment)
        }
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
