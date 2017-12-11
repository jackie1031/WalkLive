# Changelog
All notable changes to this project will be documented in this file.
## [5.1.3] - 2017-12-04
### Added
- FE:  add route tracking panels and panels show time estimation, time spent, trip details by @mshu1
- FE:  add timer to keep track of the current trip; implemented cancel trip by @mshu1
- FE:  add login and signup endpoints
### Changed
- FE:  finished first version of message builder by @mshu1
- FE:  all panels now can be hidden when double click on the corresponding buttons by @mshu1
- FE:  add login and signup endpoints @yang
- BE:  added all work from backup branch - added friendrequest endpoints by @jeesoo
- BE:  added friendRequest tests and minor changes to database table name by @jeesoo
- BE:  fixed Cherokee problem by @Jackie

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

 
