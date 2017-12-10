//
//  WarningSignView.swift
//  WalkLive
//
//  Created by Michelle Shu on 12/9/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class WarningSignView: UIView {

    @IBOutlet weak var headerLabel: UILabel!
    @IBOutlet weak var warningContentView: UILabel!
    
    func loadNib() -> WarningSignView {
        let bundle = Bundle(for: type(of: self))
        let nibName = type(of: self).description().components(separatedBy: ".").last!
        let nib = UINib(nibName: nibName, bundle: bundle)
        return nib.instantiate(withOwner: self, options: nil).first as! WarningSignView
    }
    
    @IBAction func onOkButton(_ sender: Any) {
        self.removeFromSuperview()
    }
    
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}
