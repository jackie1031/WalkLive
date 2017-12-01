//
//  FriendTripTableViewCell.swift
//  Walklive
//
//  Created by Michelle Shu on 11/30/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FriendTripTableViewCell: UITableViewCell {

    @IBOutlet weak var friendNameLabel: UILabel!
    @IBOutlet weak var sourceLabel: UILabel!
    @IBOutlet weak var destinationLabel: UILabel!
    @IBOutlet weak var timeSpentLabel: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
