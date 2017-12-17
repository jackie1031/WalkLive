//
//  TripHistoryTableViewCell.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/16/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class TripHistoryTableViewCell: UITableViewCell {
    @IBOutlet weak var destinationNameLabel: UILabel!
    @IBOutlet weak var startLocationLabel: UILabel!
    @IBOutlet weak var endLocationLabel: UILabel!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var endTimeLabel: UILabel!
    @IBOutlet weak var trackButton: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
