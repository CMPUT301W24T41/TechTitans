# TechTitans

Event Management App

Overview
The Event Management App is a comprehensive solution designed to streamline event organization, attendee management, and communication for both organizers and attendees. With a focus on simplicity and convenience, the app facilitates seamless check-ins, updates, and engagement before and during events.

#### Google Maps API Configuration

This app utilizes the Google Maps API for displaying a dynamic map. To use this API in a local build, the following steps must be followed.

1. In the Google Cloud Console, go to the [APIs Overview](https://console.cloud.google.com/apis/dashboard?authuser=1&project=eventsigninapp-415919) for this project.
2. From the menu on the left, click Credentials.
3. Under API Keys, select Maps API Key.
4. The Google Maps API key should be visible on the right. Copy this key.
5. In the local `App` directory, add two files: `secrets.properties` and `local.defaults.properties`.
   1. In the `secrets.properties` file, add the following line:
      ```
      PLACES_API_KEY="key"
      ```
      where `key` is the API key copied from Step 4.
   2. In the `local.defaults.properties` file, add the following line:
      ```
      MAPS_API_KEY=PLACES_API_KEY
      ```
6. Re-run `build.gradle.kts`.
