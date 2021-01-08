# LogsManagementSystem
- In every real time application managing logs is one of the crucial part. Also managing logs in such a way, that it store frequently, we can easily take backup and retrieve easily is demand of every application.
- So with this solution, we can easily make and retrieve logs from our application.
- It is just a component, which can be integrated with any application. I also integrated with some of my applications and it work very smoothly and give me lots of data to analyze.
- We can integrate it with any of our website, and see details of users, about where are going more, what they are doing, how much time they spend and much more. This is just an example, its scope is much bigger.
- This logsManagementSystem has following features:
1) It can maintain log files according to hourly, daily, monthly or yearly basis.
2) It can give logs of according to date.
3) It can work well in system, where thousands or request from different sources, coming to logSystem in seconds.

# Protocols
- I have made certain protocols while making this logging server, you can change if want in your ways, but for understanding code, I will write my protocols here:
1) At first my protocol for sending request to logging server and storing log files are:
    applicationID, deviceID , current time , nature , payload #
    
    ApplicationID -> It was id of application for which logging server is running
    Device ID -> It was the deviceId given by user
    current Time -> it is calculated automatically which making logs ( we used georgian time format in this)
    nature -> it show which type of message coming to server Request/Response
    payload -> It was given by the user, it can be anything, which information the user wants to store in that logging server, in this we generally store a big JSON of our request and response. It depends on user protocol
2) For sending request to logging server you have to give applicationID, deviceID, request and payload. 
3) For sending request to retrieval server you have to give applicationID, deviceId and Date. Here you have to give date for which logs you want to retrieve in format yyyy-mm-dd.

# Prerequisite
- Java (1.8 or higher)

# Running
1) Download zip file from my git repository, unzip it and store in where you want.
2) Open command prompt and go to LogsManagementSystem directory.
3) Go to src folder and type following command
```
cmp.bat
```
4) In this file compilation code for logging server is there, as I did not use any IDE, so I compile from command prompt itself. You can use any IDE, but compilation and running process will differ from my technique.
5) Go back to parent folder and then go to classes folder.
```
runServer.bat
```
6) Now, this command prompt will halt, as it is running server. You can stop the server by pressing Ctrl+c or closing command prompt.
7) Now, open another commmand prompt,as in previous command prompt, our logging server is running, so in this we will run client, for sending request. Go to logsManagementSystem folder in this command prompt alsp.
8) Now go to classes folder and then type following command
```
runClient.bat
```
9) Now go to logsManagementSystem\src\RetrievalServer\Server directory. You can now close my logging server.
10) Run following command
```
cmp.bat
run.bat
```
11) Now, retrieval server is running on port 3000, from this command prompt.
12) Open another command prompt and go to again logsManagementSystem\src\RetrievalServer\Client directory
13) type following command in command prompt
 ```
 cmp.bat
 run.bat
 ```
 14) Now you will get log, that you have stored with logging server.
 15) Now if you understand, how to compile and run, then there is threadedClient also there and its batch file runThreadedClient is also there in classes folder, So try to have some fun with it, by sending multiple request and try to customise this system by your own protocols.
 
 
