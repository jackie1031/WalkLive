//
//  MessageVC.swift
//  WalkLive
//
//  Created by Yang Cao on 12/7/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class MessageVC: UIViewController {
    
    private var filePath: String {
        let manager = FileManager.default
        let url = manager.urls(for: .documentDirectory, in: .userDomainMask).first
        return (url!.appendingPathComponent("Data").path)
    }
    
    let WITHOUT_TRIP = 0
    let WITH_TRIP = 1
    
    let LABEL_WITHOUT_TRIP = "Type \"Coordinate\" to plug in current location in the form (Longitude, Latitude). Type \"Phone\" to plug in phone number."
    let LABEL_WITH_TRIP = "Type \"Coordinate\" to plug in current location in the form (Longitude, Latitude). Type \"Phone\" to plug in phone number. Type \"Time\" to plug in time spent. Typey \"Destination\" to plug in trip destination."
    
    private var unsavedMessages : Message!
    private var textFieldsWithoutTrip : Array<UITextField>!
    private var textFieldsWithTrip : Array<UITextField>!
    
    private var index : Int!
    
    @IBOutlet weak var messageEditPanel: UIView!
    @IBOutlet weak var noteLabel: UILabel!
    @IBOutlet weak var addTextButton: UIButton!
    @IBOutlet weak var delTextButton: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    
    
    override func viewDidLoad() {
        self.setKeyboard()
        self.loadData()
        self.index = segmentedControl.selectedSegmentIndex
        self.setUpTextFields()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onAddTextButton(_ sender: Any) {
        if (index == WITHOUT_TRIP) {
            self.addTextField(textFields: self.textFieldsWithoutTrip, message: "")
        } else {
            self.addTextField(textFields: self.textFieldsWithTrip, message: "")
        }
        setPanelPosition()
    }

    @IBAction func onDeleteLastTextButton(_ sender: Any) {
        if (index == WITHOUT_TRIP) {
            self.delTextField(textFields: self.textFieldsWithoutTrip)
        } else {
            self.delTextField(textFields: self.textFieldsWithTrip)
        }
        setPanelPosition()
    }
    
    // Determine which page the user is on
    @IBAction func onSegmentedControl(_ sender: Any) {
        if (segmentedControl.selectedSegmentIndex == WITHOUT_TRIP) {
            self.index = WITHOUT_TRIP
            self.switchSegmentedControl()
        } else if (segmentedControl.selectedSegmentIndex == WITH_TRIP) {
            self.index = WITH_TRIP
            self.switchSegmentedControl()
        } else {
            print("Error.")
            return
        }
    }
    
    private func setPanelPosition() {
        var textFields : Array<UITextField>
        if index == 0 {
            textFields = textFieldsWithoutTrip
        } else {
            textFields = textFieldsWithTrip
        }
        // TODO: move buttons and labels
        if (textFields.count == 0) {
            messageEditPanel.frame.origin.y = 40
        } else {
            let textFieldFrame : CGRect = textFields.last!.frame
            messageEditPanel.frame.origin.y = textFieldFrame.origin.y + 10
        }
        let frame : CGRect = messageEditPanel.frame
        noteLabel.frame.origin.y = frame.origin.y + 40
    }
    
    // reload saved texts
    private func setUpTextFields() {
        // clean both text field arrays
        if textFieldsWithoutTrip != nil {
            for textField in textFieldsWithoutTrip {
                textField.removeFromSuperview()
            }
            textFieldsWithoutTrip.removeAll()
        } else {
            textFieldsWithoutTrip = Array()
        }
        if textFieldsWithTrip != nil {
            for textField in textFieldsWithTrip {
                textField.removeFromSuperview()
            }
            textFieldsWithTrip.removeAll()
        } else {
            textFieldsWithTrip = Array()
        }
        
        // determine label content
        if (self.index == WITHOUT_TRIP) {
            self.noteLabel.text = LABEL_WITHOUT_TRIP
        } else if (self.index == WITH_TRIP) {
            self.noteLabel.text = LABEL_WITH_TRIP
        } else {
            print("Error.")
            return
        }
        // load saved messages and update text field arrays
        for messageSegment in self.unsavedMessages.getMessagesWithoutTrip() {
            addTextField(textFields: self.textFieldsWithoutTrip, message: messageSegment)
        }
        for messageSegment in self.unsavedMessages.getMessagesWithTrip() {
            addTextField(textFields: self.textFieldsWithTrip, message: messageSegment)
        }
        
    }
    
    private func switchSegmentedControl() {
        var textFields : Array<UITextField>
        var otherTextFields : Array<UITextField>
        if (self.index == WITHOUT_TRIP) {
            textFields = self.textFieldsWithoutTrip
            otherTextFields = self.textFieldsWithTrip
            self.noteLabel.text = LABEL_WITHOUT_TRIP
        } else if (self.index == WITH_TRIP) {
            textFields = self.textFieldsWithTrip
            otherTextFields = self.textFieldsWithoutTrip
            self.noteLabel.text = LABEL_WITH_TRIP
        } else {
            print("Error.")
            return
        }
        for textField in otherTextFields {
            textField.isHidden = true
        }
        for textFields in textFields {
            textFields.isHidden = false
        }
        // TODO: move buttons and labels
//        if (textFields.count == 0) {
//            messageEditPanel.frame.origin.y = 40
//        } else {
//            let textFieldFrame : CGRect = textFields.last!.frame
//            messageEditPanel.frame.origin.y = textFieldFrame.origin.y - 20
//        }
//        let frame : CGRect = messageEditPanel.frame
//        noteLabel.frame.origin.y = frame.origin.y + 40
        setPanelPosition()
    }
    
    // add a new text field
    private func addTextField(textFields : Array<UITextField>, message : String) {
        var currentIndex : Int
        if textFields == self.textFieldsWithoutTrip {
            currentIndex = 0
        } else {
            currentIndex = 1
        }
        if (textFields.count >= 8) {
            let errorView = warnigSignFactory.saveMessageWarningSign(status: 1)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return
        }
        
        // create new text field
        var lastYCoor : Int
        if (textFields.count == 0) {
            lastYCoor = 110
        } else {
            lastYCoor = Int((textFields.last?.frame.minY)! + 40)
        }
        let newTextField = UITextField(frame: CGRect(x: 10, y: lastYCoor, width: 357, height: 30))
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
        if (self.index != currentIndex) {
            newTextField.isHidden = true
        }
        if (currentIndex == 0) {
            self.textFieldsWithoutTrip.append(newTextField)
        } else {
            self.textFieldsWithTrip.append(newTextField)
        }
        
//        // TODO: move buttons and labels down
//        if (textFields.count == 0) {
//            messageEditPanel.frame.origin.y = 40
//        } else {
//            let textFieldFrame : CGRect = textFields.last!.frame
//            messageEditPanel.frame.origin.y = textFieldFrame.origin.y + 10
//        }
//        let frame : CGRect = messageEditPanel.frame
//        noteLabel.frame.origin.y = frame.origin.y + 40
        setPanelPosition()
    }
    
    // delete a text field
    private func delTextField(textFields : Array<UITextField>) {
        var currentIndex : Int
        if textFields == self.textFieldsWithoutTrip {
            currentIndex = 0
        } else {
            currentIndex = 1
        }
        if (textFields.count < 5) {
            let errorView = warnigSignFactory.saveMessageWarningSign(status: 0)
            errorView.center = self.view.center
            self.view.addSubview(errorView)
            return
        }
        // delete last text field
        let lastTextField = textFields.last
        lastTextField?.removeFromSuperview()
        let textFieldFrame : CGRect
        if (currentIndex == 0) {
            self.textFieldsWithoutTrip.removeLast()
            textFieldFrame = self.textFieldsWithoutTrip.last!.frame
        } else {
            self.textFieldsWithTrip.removeLast()
            textFieldFrame = self.textFieldsWithTrip.last!.frame
        }
        // TODO: move buttons and labels down
//        if (textFields.count == 0) {
//            messageEditPanel.frame.origin.y = 40
//        } else {
//            messageEditPanel.frame.origin.y = textFieldFrame.origin.y - 20
//        }
//        let frame : CGRect = messageEditPanel.frame
//        noteLabel.frame.origin.y = frame.origin.y + 40
        setPanelPosition()
    }
    
    // save both messages
    @IBAction func onSaveButton(_ sender: Any) {
        var messageSegments1 = Array<String>()
        for textField in textFieldsWithoutTrip {
            messageSegments1.append(textField.text!)
        }
        var messageSegments2 = Array<String>()
        for textField in textFieldsWithTrip {
            messageSegments2.append(textField.text!)
        }
        unsavedMessages.updateMessagesWithoutTrip(updatedMessages: messageSegments1)
        unsavedMessages.updateMessagesWithTrip(updatedMessages: messageSegments2)
        self.saveData()
        let view = warnigSignFactory.makeSaveSuccessSign()
        view.center = self.view.center
        self.view.addSubview(view)
    }
    
    // save message setting locally
    private func saveData() {
        NSKeyedArchiver.archiveRootObject(self.unsavedMessages, toFile: filePath)
        messages.updateMessagesWithoutTrip(updatedMessages: unsavedMessages.getMessagesWithoutTrip())
        messages.updateMessagesWithTrip(updatedMessages: unsavedMessages.getMessagesWithTrip())
    }
    
    // load message setting from local directory
    private func loadData() {
        if let data = NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as? Message {
            self.unsavedMessages = data
        } else {
            unsavedMessages = loadMessageInfo()
        }
        // if local data is empty
        if (unsavedMessages.getMessagesWithoutTrip().count == 0) {
            unsavedMessages = loadMessageInfo()
        }
    }
    
    private func loadMessageInfo() -> Message {
        let temp = Message()
        temp.updateMessagesWithoutTrip(updatedMessages: messages.getMessagesWithoutTrip())
        temp.updateMessagesWithTrip(updatedMessages: messages.getMessagesWithTrip())
        return temp
    }
    
    private func setKeyboard(){
        let hideTap = UITapGestureRecognizer(target: self, action: #selector(MainMapVC.hideKeyboardTap(_:)))
        hideTap.numberOfTapsRequired = 1
        self.view.isUserInteractionEnabled = true
        self.view.addGestureRecognizer(hideTap)
    }
    
    @objc func hideKeyboardTap(_ recoginizer: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }

    
}
