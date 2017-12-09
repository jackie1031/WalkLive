# 2017-group-14
## FrontEnd Install:
- Install the latest version of Xcode (9.0.1)
- run 'cd WalkLive'
- run 'pod install'
- run 'open WalkLive.xcworkspace'
- click 'Build' button in Xcode to run the APP

## BackEnd Install:
- run 'cd WalkLive/Server'
- run 'mvn package'
- run 'java -jar target/OOSE-group14-1.0-SNAPSHOT.jar' 
- login to mysql by typing in ' mysql -u root -p', and for password, directly hit enter key
- run 'source tables.sql' with mysql

*assume maven is already installed on the computer.If not, please set up maven following this tutorial: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

## Deploy App
- run 'cd WalkLive/Server'
- run'mvn clean heroku:deploy'
- (since our app is iOS, instead of a web app, there's nothing to show on the website, but we can check whether it deploys successfully by opening a new tab in the browser as'https://walklive.herokuapp.com/WalkLive/api/test' and it will display 'success')

