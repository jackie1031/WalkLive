# 2017-group-14
## FrontEnd Install:
- Install the latest version of Xcode (9.0.1)
- run 'cd Walklive'
- run 'pod install'
- run 'open Walklive.xcworkspace'
- click 'Build' button in Xcode to run the APP

## BackEnd Install:
- run 'cd Walklive'
- run 'cd Server'
- run 'zip -r server.zip . -x ".*" -x "__MACOSX" -x "*.DS_Store' (???? still need to change)
- run 'mvn package'
- open a web browser, and type in: localhost:8080

*assume maven is already installed on the computer.If not, please set up maven following this tutorial: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
