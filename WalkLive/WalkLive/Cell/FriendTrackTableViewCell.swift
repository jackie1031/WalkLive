//
//  FriendTrackTableViewCell.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/10/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FriendTrackTableViewCell: UITableViewCell {

    @IBOutlet weak var friendNameLabel: UILabel!
    @IBOutlet weak var currentLocationLabel: UILabel!
    @IBOutlet weak var destinationLocationLabel: UILabel!
    @IBOutlet weak var timeSpentLabel: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }

}
