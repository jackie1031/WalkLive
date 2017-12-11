//
//  AddFriendView.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class AddFriendView: UIView {
    
    @IBOutlet weak var statusLabel: UILabel!
    
    @IBOutlet weak var friendIdLabel: UITextField!
    
    
    @IBAction func onSendButton(_ sender: Any) {
        let friendRequest = FriendRequest()
        if (friendIdLabel.text == "") {
            return
        }
        friendRequest.recipient = friendIdLabel.text
        backEndClient.createFriendRequest(success: {
            OperationQueue.main.addOperation {
            self.statusLabel.text = "sent successfully to " + self.friendIdLabel.text!
            self.statusLabel.textColor = primaryColor
            }
        }, failure: { (error) in
            print(error)
        }, friendRequest: friendRequest)
    }
    
    
    @IBAction func onClearButton(_ sender: Any) {
        self.friendIdLabel.text = ""
    }
    
    
    @IBAction func onCancelButton(_ sender: Any) {
        self.removeFromSuperview()
    }
    
    func loadNib() -> AddFriendView {
        let bundle = Bundle(for: type(of: self))
        let nibName = type(of: self).description().components(separatedBy: ".").last!
        let nib = UINib(nibName: nibName, bundle: bundle)
        return nib.instantiate(withOwner: self, options: nil).first as! AddFriendView
    }
    
    
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}
