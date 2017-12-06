//
//  FriendRequestVC.swift
//  Walklive
//
//  Created by Michelle Shu on 11/19/17.
//  Copyright © 2017 OOSE-TEAM14. All rights reserved.
//

import UIKit

class FriendRequestVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var segmentControl: UISegmentedControl!
    @IBOutlet weak var requestTable: UITableView!
    var receivedfriendRequests: [FriendRequest]!
    var sentFriendRequests: [FriendRequest]!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        requestTable.delegate = self
        requestTable.dataSource = self
        // Do any additional setup after loading the view.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.receivedfriendRequests != nil {
            return self.receivedfriendRequests.count
        } else {
            return 0
        }
    }
    
    private func numCount(){
        
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = requestTable.dequeueReusableCell(withIdentifier: "LocationCell", for: indexPath) as! LocationTableViewCell
//        let mapItem = self.mapItems[indexPath.row]
//        let location = Location(mapItem: mapItem)
//        cell.goButton.tag = indexPath.row
//        cell.destinationNameLabel.text = location.destinationName
//        cell.addressLabel.text = location.address
//        if (location.phoneNumber != nil) {
//            cell.phoneNumberLabel.text = location.phoneNumber
//        }
//        cell.selectionStyle = .none
        return cell
    }
    
    
    
//    override func viewWillAppear(_ animated: Bool) {
//        self.setBackButtons()
//    }
//
//    func setBackButtons(){
//        let backItem = UIBarButtonItem()
//        backItem.tintColor = primaryColor
//        self.navigationItem.backBarButtonItem = backItem
//    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
