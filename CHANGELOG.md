# Changelog
All notable changes to this project will be documented in this file.
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
- BE:  deployed on Heroku

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

 
