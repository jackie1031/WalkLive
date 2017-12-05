# 2017-group-14
## FrontEnd Install:
- Install the latest version of Xcode (9.0.1)
- run 'cd WalkLive'
- run 'pod install'
- run 'open WalkLive.xcworkspace'
- click 'Build' button in Xcode to run the APP

## BackEnd Install:
- run 'cd WalkLive'
- run 'cd Server'
- run 'mvn package'
- run 'java -jar target/OOSE-group14-1.0-SNAPSHOT.jar' 
- open a web browser, and type in: localhost:8080

*assume maven is already installed on the computer.If not, please set up maven following this tutorial: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

## Deploy App
- run 'cd WalkLive/Server'
- run'heroku open'
