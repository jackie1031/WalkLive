//
//  MessageVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

protocol MessageVCDelegate: class {
    func messagesSaved(messages: Message?)
}

class MessageVC: UIViewController {

    var messages : Message!
    var textFields : Array<UITextField>!
    var lastYCoor : Int!
    weak var delegate: MessageVCDelegate?
//    var filePath: String {
//        //1 - manager lets you examine contents of a files and folders in your app; creates a directory to where we are saving it
//        let manager = FileManager.default
//        //2 - this returns an array of urls from our documentDirectory and we take the first path
//        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
//        //print("this is the url path in the documentDirectory \(url)")
//        //3 - creates a new path component and creates a new file called "Data" which is where we will store our Data array.
//        return (url!.appendingPathComponent("Data").path)
//    }
    
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
        setUpTextFields()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    private func setUpTextFields() {
        // clear textfields
        if textFields == nil {
            self.textFields = Array()
            textFields.append(message1)
            textFields.append(message2)
            textFields.append(message3)
            textFields.append(message4)
        }
        for textField in textFields {
            textField.removeFromSuperview()
        }
        textFields.removeAll()
        // load data from local
//        loadData()
        // reset panel coordination
        messageEditPanel.frame.origin.y = 30
        // load last textfields
        lastYCoor = 30
        let messageSegments = self.messages.getMessages()
        for messageSegment in messageSegments {
            print(messageSegment)
            addTextField(message: messageSegment)
        }
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        addTextField(message: "")
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        delTextField()
    }
    
    private func addTextField(message : String) {
        // move add and del buttons down
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y + 40
        
        // create new label
        lastYCoor = lastYCoor + 40
        let newTextField = UITextField(frame: CGRect(x: 10, y: lastYCoor+30, width: 357, height: 30))
        if message == "" {
            newTextField.placeholder = "Enter text message here"
        } else {
            newTextField.text = message
        }
        newTextField.font = UIFont.systemFont(ofSize: 15)
        newTextField.borderStyle = UITextBorderStyle.roundedRect
        newTextField.autocorrectionType = UITextAutocorrectionType.no
        newTextField.keyboardType = UIKeyboardType.default
        newTextField.returnKeyType = UIReturnKeyType.done
        newTextField.clearButtonMode = UITextFieldViewMode.whileEditing
        newTextField.contentVerticalAlignment = UIControlContentVerticalAlignment.center
        self.view.addSubview(newTextField)
        textFields.append(newTextField)
        self.messages.addMessage(m: "")
    }
    
    private func delTextField() {
        if (textFields.count < 5) {
            return
        }
        // move add and del buttons up
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y - 40
        
        // delete last label
        let lastTextField = textFields.last
        lastTextField?.removeFromSuperview()
        if self.textFields.count != 0 {
            self.textFields.removeLast()
            self.messages.deleteLastMessage()
        }
        
        lastYCoor = lastYCoor - 40
    }
    
    @IBAction func onSaveButton(_ sender: Any) {
        var messageSegments = Array<String>()
        for textField in textFields {
            messageSegments.append(textField.text!)
        }
        self.messages.updateMessages(updatedMessages: messageSegments)
        delegate?.messagesSaved(messages: self.messages)
        //save locally
//        saveData()
    }
    
    private func saveData() {
        NSKeyedArchiver.archiveRootObject(self.messages, toFile: filePath)
    }
    
    // when do we call this function?
    private func loadData() {
        print("hi")
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            print("local data exists")
            self.messages = data
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
