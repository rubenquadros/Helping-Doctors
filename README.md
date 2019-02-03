# Helping-Doctors

## Introduction

Helping Doctors is a platform for both doctors as well as patients.

It is mainly targeted for people in remote areas in India who have to travel long distances to consult doctors.

For patients - A simple user friendly android application

For doctors - A web portal

Below are the doctor/1000 patient ratios: 

### • Germany 4.19 (2015)

### • USA 2.57 (2014)

### • India 0.76 (2016)

As you can see in India, the ratio is particularly low.

In today's world where everyone is using a smartphone and where there is an app for anything and everything, we want to provide a simple solution for this problem with the help of Agora RTC.

We have designed and developed a mobile app for the users (patients), using which they are only a click away from consulting a doctor of their choice.

The users can consult with the doctor though an audio or video call.

## Mobile Application (Android)

To build the application follow the below steps:

1. Create a developer account at [Agora.io](https://agora.io) and obtain an App ID.

2. Update "app/src/main/resources/values/strings.xml" with your App ID: 

 ```xml
 <string name="agora_app_id">YOUR_APP_ID</string>"
 ```
3. Download the Agora Voice SDK from [here](https://docs.agora.io/en/Video/downloads)

   Extract the downloaded file and navigate inside the libs folder. 
   Copy agora-rtc-sdk.jar into the lib folder in your project.
   Copy the remaining folders into [jniLibs folder](/Mobile App/app/src/main/jniLibs/)
   
4. Download the Agora Signalling SDK from [here](https://docs.agora.io/en/Signaling/downloads)

   Extract the downloaded file and navigate inside the libs folder
   Copy agora-sig-sdk.jar into the lib folder in your project
   Copy the content inside the remaining folders into [jniLibs folder](/Mobile App/app/src/main/jniLibs/)

5. Go to [Firebase Console](https://console.firebase.google.com/) and login using your google credentials.

6. Click on Add Project to add your project.

7. Click on Add App and fill in the required fields.
   
   The first 2 fields are straight forward.
   You can add your SHA-1 certificate here or you can add it later on.

8. To add your SHA fingerprints certificate, click on settings next to Project Oveview and then click on project settings.
   
   You can generate your SHA-1 certificate by following [this](https://developers.google.com/android/guides/client-auth)

9. Once you add your SHA-1 certificate, download the google-services.json file and replace it [here](/Mobile App/app/)


## AudioVedioApp (Web Application)

This application is present [here](/AudioVedioApp/).

If you want to build the application yourself follow the below steps:

1. Create a developer account at [Agora.io](https://agora.io) and obtain an App ID.

2. Download the Signalling SDK for web from [here](https://docs.agora.io/en/Signaling/downloads)
   
   Extract the downloaded file and navigate inside the libs folder
   Re-name AgoraSig-1.4.0.js to AgoraSig.js and place it [here](/AudioVedioApp/static) 

3. Update "\AudioVedioApp\static\agora.config.js" with your App ID: 

 ```xml
 const AGORA_APP_ID = 'YOUR_APP_ID'
 ```
4. Open the project in visual studio code

5. Execute "npm install" command  in the terminal- this will download the all the neccesary  node modules.

6. Execute "npm start" command in the terminal - this will start the development server to make Audio and Vedio calls.

## WebApp (Web Application)

This application is present [here](/WebApp/).

To build the application yourself follow the below steps:

1. Open the project in visual studio code

2. update "src\static\property.js" with ip and port where AudioVedioApp is running. 

 ```xml
 const ip = "localhost";
 const port = "8080";
 ```

3. Execute "npm install" command  in the terminal- this will download the all the neccessary node modules.

4. Execute "npm start" command in the terminal - this will start the development server where doctors can create and use their account.


## Future Scope

• A mobile application for doctors

• Group audio and video calls. For example - If my parents are staying in another state and I want to consult with a doctor along with     them

• A chat service so that patients have an option to chat with the doctors as well

• Multiple language support in app, so that everyone is able to use it without any hassle
