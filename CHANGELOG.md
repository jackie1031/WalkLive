# Changelog
All notable changes to this project will be documented in this file.
## [6.0.2] - 2017-12-16
### Added
- FE:  add special symbol checks to prevent breaking the database @mshu1
- FE:  fix emergency label bugs and CLLocation check bugs breaking the app @mshu1
### Changed
- BE:  fix get all trips function bugs and now the method with multiple trips can be decoded properly @mshu1
### Tested
- FE + BE:  tested all refactored functions @mshu1
- FE + BE:  redeployed server and checked every available functions for both frotend and backend @mshu1

## [6.0.2] - 2017-12-15
### Added
- BE:  add change password functions @mshu1
- BE:  add TripManager class to handle trip related methods and add various private functions to decrease code size @mshu1
- BE:  add Gson() related methods to effectively handle data classes @mshu1
### Changed
- FE:  fix friend tracking bugs and map annotation bugs(does not show titles) and colors on message board @mshu1
- BE:  fix trip id generation inconsistency in tests @mshu1
- BE:  fix get all trips function bugs and now the method with multiple trips can be decoded properly @mshu1
- BE:  improve trip id generation functions @mshu1
- BE:  refactor backend respond to friend request functions @mshu1
- BE:  refactor backend get friend list functions @mshu1
- BE:  refactor backend start trip, end trip, get all trips to relevant classes @mshu1
### Tested
- FE+BE:  tested all refactored functions @mshu1

## [6.0.2] - 2017-12-14
### Added
- BE:  add change password functions @mshu1
- BE:  add FriendManager class to handle methods and add various private functions to decrease code size@mshu1
### Changed
- BE:  refactor backend user retrivel functions @mshu1
- BE:  refactor backend update emergency contact functions @mshu1
- BE:  refactor backend create friend request, get incoming friend request, get outgoing friend request to relevant classes @mshu1
### Tested
- FE+BE:  tested all refactored functions @mshu1

## [6.0.1] - 2017-12-13
### Added
- FE:  add timer for auto updating friends' information User page @mshu1
- FE:  add timer and implementation for tracking friend's trips(update table) on tracker page @mshu1
- FE:  add auto updates on friend tracker page -> set friend to be the auto-update target(single trip) @mshu1
- FE:  add contact(text + phone) functionalities on User page, friend tracker page. @mshu1
- BE:  add DataBaseHandler, Connection Handler, UserManager to handle differemt methods and add various private functions to decrease code size @mshu1
### Changed
- BE:  refactor backend database set up functions @mshu1
- BE:  refactor backend sign up(create user) functions to relevant classes @mshu1
### Tested
- FE+BE:  tested all refactored functions @mshu1

## [6.0.1] - 2017-12-12
### Added
- FE:  add connection and functionality to backend for getting, responding friend request @mshu1
- FE:  add connection and functionality to backend for start trip, end trip @mshu1
- BE:  add getAlltrips backend implementation and test by @jackie
### Changed
- FE:  fixed various buttons implementations by @mshu1
- FE:  fixed friend name showing bug, wrong message bug by @mshu1
- FE:  fixed auto-message preview by @Yang
- BE:  fix friend request bug by @jackie and @mshu1
- BE:  fix get incoming friend request database by @jackie and @mshu1
- BE:  fix get friend lists'bug by @jackie and @mshu1
### Test
- FE + BE: tested accept, reject friend, get friend request code for both front end and back end by @mshu1 and @jackie
- FE + BE: tested start trip, get trip, update trip for both front end and back end by @mshu1 and @jackie


## [5.3.3] - 2017-12-11
### Added
- FE:  add warnings signs when route is not found and other success, failure blocks by @mshu1
- FE:  add friend requests front end implementations by @mshu1
- FE:  add settings save and setting friends as primary contact by @mshu1
### Changed
- BE:  fixed clearDB command bugs by @jeesoo

## [5.3.2] - 2017-12-10
### Added
- FE:  add start trip back end call and its related controllers by @mshu1
- FE:  add update emergency contact related friend list and its related code by @mshu1
- FE:  add errors handling and warning signs on sign up, log in, save setting by @mshu1
- BE:  add respond to friendrequest endpoint by @jeesoo
- BE:  add request id by @jeesoo
- BE:  add create trip, update trip, getAllTrips endpoint by @jackie
### Changed
- BE:  fix login password return bug by @jeesoo
- BE:  fix password check by @jeesoo
- BE:  fix emergency id and number return by @jeesoo
- FE:  fix constraints for controllers by @mshu1
### Test
- FE + BE: thoroughly tested emergencyContactUpdate code for both front end and back end by @mshu1 and @jeesoo 
- FE + BE: thoroughly tested createFriendRequest code for both front end and back end by @mshu1 and @jeesoo
- BE:  thoroughly tested createTrip, endTrip, code for back end by @jackie

## [5.3.0] - 2017-12-09
### Added
- FE:  add warning signs and logic code by @mshu1
- FE:  add signUp endpoint and signUp logistics based on endpoints by @mshu1
- FE:  add stores message to local disk by @yang
- BE:  add test for emergencycontact update by @jeesoo
- BE:  add start trip, update trip end points and connect to ClearDB by @jackie
### Changed
- BE:  fix login empty string by @jeesoo
- BE:  fix signup return value by @jeesoo
- BE:  fix nonexistent user error code by @jeesoo
- BE:  Update start/get trip, wip by @jackie
- BE:  fix user model and json field name conventions for emergency contact by @jeesoo
- FE:  refin implementation for signUp and Login by @mshu1
- FE:  fix emergency contact showings now that endpoints are connected by @mshu1
- FE:  fix messageVC by @yang
### Test
- FE + BE:  thoroughly test signUp code for both front end and back end by @mshu1 and @jeesoo 
- FE + BE:  thoroughly test getUser code for both front end and back end by @mshu1 and @jeesoo 

## [5.2.4] - 2017-12-08
### Added
- FE:  add login endpoint and login logistics based on endpointsby @mshu1
- BE:  add cleardb database connection code by @jeesoo
### Test
- FE + BE: thoroughly tested login code for both front end and back end by @mshu1 and @jeesoo 

## [5.2.3] - 2017-12-07
### Added
- FE:  add friend requests models, friends list table connection, delegate code by @mshu1
- FE:  add text message logisticis by @yang
### Changed
- BE:  update BE heroku & database by @jackie
- BE:  finish database setting for trips by @jackie
### Test
- FE: test decoder, encoder for JSON and wrote testing parsers and endpoints by @mshu1

## [5.2.2] - 2017-12-06
### Added
- FE:  add friend request update logistics by @mshu1
### Changed
- FE:  refactor friend request, mainmap, delegates by @mshu1
- FE:  move settings method to backendClient by @yang
- BE:  fixed tables.sql syntax by @jeesoo
- BE:  fix Heroku database by @jackie
### Test
- FE: test decoder, encoder for JSON and wrote testing parsers and endpoints by @mshu1

## [5.2.1] - 2017-12-05
### Added
- FE:  add route tracking panels and panels show time estimation, time spent, trip details by @mshu1
- FE:  add endpoints for friend requests by @mshu1
### Changed
- FE:  fix folder path to combine "WalkLive" and "Walklive" by @mshu1
- FE:  change launch screen, friend request table logistics @mshu1
- FE:  refine login signup and settings endpoints by @yang
- BE:  update dangerzone parsing by @Jackie

## [5.2.0] - 2017-12-04
### Added
- FE:  add route tracking panels and panels show time estimation, time spent, trip details by @mshu1
- FE:  add timer to keep track of the current trip; implemented cancel trip by @mshu1
- FE:  add login and signup endpoints @yang
### Changed
- FE:  finished first version of message builder by @mshu1
- FE:  all panels now can be hidden when double click on the corresponding buttons by @mshu1
- BE:  added all work from backup branch - added friendrequest endpoints by @jeesoo
- BE:  added friendRequest tests and minor changes to database table name by @jeesoo
- BE:  fixed Cherokee problem by @Jackie
### Test
- FE + BE:  Tested the first Heroku deployed test endpoint by @mshu1 and @jackie


## [5.1.2] - 2017-12-02
### Added
- FE:  add route choice controller and user now can choose their destinations when seeing multiple options by @mshu1
### Changed
- FE:  fix toggle keyboards for all controllers; now users can tap on any view controller to dimiss keyboard @mshu1
- BE:  update Heroku by @jackie

## [5.1.2] - 2017-12-01
### Changed
- FE:  convert route requests to meet network request standard --success/failure blocks @mshu1

## [5.1.1] - 2017-11-30
### Added
- FE:  add setting controller, emergency number calls by @mshu1
### Changed
- FE:  refactor route planning to a separate object by @mshu1

## [5.1.0] - 2017-11-29
### Added
- FE:  once trip start, add trip tracking pannel board by @mshu1
- FE:  add calling the police button to trip panel and tested by @mshu1
- BE： pass all tests on travis by @jackie
### Changed
- FE:  change the message board and the ways messages are built by @mshu1

## [5.0.1] - 2017-11-29
### Added
- FE:  once trip start, add trip tracking pannel board by @mshu1
- FE:  add timer to keep track of the current trip; implemented cancel trip by @mshu1
- FE:  add calling the police button to trip panel and tested by @mshu1
- BE： pass all tests on travis by @jackie
- BE:  deployed on Heroku by @jackie

## [4.0.1] - 2017-11-19
### Added
- FE: add friend request page, trip request page, trip search page for front end by @mshu1
- BE: add deployment on Heroku by @jackie, trip tests for backend by @jackie
- BE: add more endpoints and testing for user database by @jeesoo

## [4.0.1] - 2017-11-12
### Changed
- FE: Migrate frontend from Google Maps to MapKit by @mshu1
- FE: Fix map routing and now it is fully functioning @mshu1
- FE: Add first version of feature demo @mshu1
### Added
- General: Official logo by @jeesoo
- BE: added sign up, login tests by @jeesoo

## [4.0.1] - 2017-11-06
### Changed
- FE: Login UI by @mshu1
- FE: Connect Settings to Settings VC by @yang
### Added
- BE: local host for backend by @jackie

## [3.0.2] - 2017-10-30
### Changed
- FE: GoogleMaps constraints changed by @mshu1
### Added
- BE: Trip, timePoint, and Travellog models added by @tianyi
- BE: signUp/logIn endpoints and backend database added by @jeesoo
- FE: Added Setting page Prototype by @yang 
- FE: Google map direction function added by @tianyi and @yang

## [3.0.1] - 2017-10-25
### Changed
- FE: Constraints and theme color for SignUp/Login changed by @mshu1
- FE: Timepoint model and SignUp/Login controllers changed by @mshu1
### Added
- FE: Start and End positions for maps added by @yang 
- FE: User model added by @yang

## [3.0.0] - 2017-10-16
### Added
- FE: New Timepoint added by @mshu1.
- FE: New constraints and layouts on SignUp/LogIn added by @mshu1.

 
