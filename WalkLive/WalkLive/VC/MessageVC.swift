//
//  MessageVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

protocol MessageVCDelegate: class {
    func messagesSaved(unsavedMessages: Message?)
}

class MessageVC: UIViewController {

    var unsavedMessages : Message!
    var textFields : Array<UITextField>!
    var lastYCoor : Int!
    weak var delegate: MessageVCDelegate?
    
    @IBOutlet weak var messageEditPanel: UIView!
    @IBOutlet weak var noteLabel: UILabel!
    @IBOutlet weak var addTextButton: UIButton!
    @IBOutlet weak var delTextButton: UIButton!
    @IBOutlet weak var message1: UITextField!
    @IBOutlet weak var message2: UITextField!
    @IBOutlet weak var message3: UITextField!
    @IBOutlet weak var message4: UITextField!
    
    
    override func viewDidLoad() {
        setUpTextFields()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        addTextField(message: "")
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        delTextField()
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
        // reset coordinations
        lastYCoor = 30
        messageEditPanel.frame.origin.y = 30
        noteLabel.frame.origin.y = 30 + 50
        // load last textfields
        let messageSegments = self.unsavedMessages.getMessages()
        for messageSegment in messageSegments {
            print(messageSegment)
            addTextField(message: messageSegment)
        }
    }
    
    private func addTextField(message : String) {
        // move buttons and labels down
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y + 40
        let labelFrame : CGRect = noteLabel.frame
        noteLabel.frame.origin.y = labelFrame.origin.y + 40
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
    }
    
    private func delTextField() {
        if (textFields.count < 5) {
            return
        }
        // move add and del buttons up
        let frame : CGRect = messageEditPanel.frame
        messageEditPanel.frame.origin.y = frame.origin.y - 40
        let labelFrame : CGRect = noteLabel.frame
        noteLabel.frame.origin.y = labelFrame.origin.y - 40
        
        // delete last label
        let lastTextField = textFields.last
        lastTextField?.removeFromSuperview()
        if self.textFields.count != 0 {
            self.textFields.removeLast()
            self.unsavedMessages.deleteLastMessage()
        }
        
        lastYCoor = lastYCoor - 40
    }
    
    @IBAction func onSaveButton(_ sender: Any) {
        var messageSegments = Array<String>()
        for textField in textFields {
            messageSegments.append(textField.text!)
        }
        unsavedMessages.updateMessages(updatedMessages: messageSegments)
        delegate?.messagesSaved(unsavedMessages: unsavedMessages)
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
