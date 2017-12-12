//
//  SetFriendTableViewCell.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class SetFriendTableViewCell: UITableViewCell {

    @IBOutlet weak var friendNameLabel: UILabel!
    @IBOutlet weak var friendNumberLabel: UILabel!
    @IBOutlet weak var selectButton: UIButton!
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
