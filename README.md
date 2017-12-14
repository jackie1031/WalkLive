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

* assume maven is already installed on the computer.If not, please set up maven following this tutorial: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
* our app is currently using a clearDB database with $9.99 plan. It only allows a maximum of 15 connections and 18000 requires per hour. Running tests repeatedly may used up the maximum amount of queries allowed per hour. 

## Deploy App
- run 'cd WalkLive/Server'
- run'mvn clean heroku:deploy'

* our app is currently using a clearDB database with $9.99 plan. It only allows a maximum of 15 connections and 18000 requires per hour. Running tests while deploying the app may cause an increasing amount of connections flushed into our server and break our database connections. Please comment out tests if members want to redeploy this app. 
* Since our app is iOS, instead of a web app, there's nothing to show on the website, but we can check whether it deploys successfully by opening a new tab in the browser as'https://walklive.herokuapp.com/WalkLive/api/test' and it will display 'success'

