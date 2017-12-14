//
//  OnGoingTripView.swift
//  Walklive
//
//  Created by Michelle Shu on 12/3/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class OnGoingTripView: UIView {
    
    @IBOutlet weak var destinationLabel: UILabel!
    @IBOutlet weak var addressLabel: UILabel!
    @IBOutlet weak var emergencyContactLabel: UILabel!
    @IBOutlet weak var estimatedTimeLabel: UILabel!
    @IBOutlet weak var timeUsedLabel: UILabel!
    var routeDelegate: RouteDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func loadNib() -> OnGoingTripView {
        let bundle = Bundle(for: type(of: self))
        let nibName = type(of: self).description().components(separatedBy: ".").last!
        let nib = UINib(nibName: nibName, bundle: bundle)
        return nib.instantiate(withOwner: self, options: nil).first as! OnGoingTripView
    }
    
    @IBAction func onCancelTrip(_ sender: Any) {
        self.removeFromSuperview()
        self.routeDelegate?.cancelTrip()
    }
    
    @IBAction func onCompleteTrip(_ sender: Any) {
        self.removeFromSuperview()
        self.routeDelegate?.completeTrip()
    }
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}
