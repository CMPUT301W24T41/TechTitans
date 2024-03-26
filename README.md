# TechTitans

Event Management App

Overview: 

The Event Management App is a comprehensive solution designed to streamline event organization, attendee management, and communication for both organizers and attendees. 
With a focus on simplicity and convenience, the app facilitates seamless check-ins, updates, and engagement before and during events.

Features


For Attendees

Quick Check-in: Attendees can effortlessly check into events by scanning the provided QR code.

Profile Customization: Attendees can upload, remove, or update their profile picture for a personalized experience.

Profile Management: Attendees can update their information such as name, homepage, and contact details.
Push Notifications: Receive important updates and announcements from event organizers via push notifications.

Event Details: Access event details and announcements within the app.
Automatic Profile Picture Generation: Profile pictures are deterministically generated from the profile name if not uploaded.

No Login Required: Attendees can use the app without the need for a username or password.

Event Registration: Attendees can sign up to attend events directly from event details.

Browse Other Events: Attendees can browse event posters and details of other events.

Event Registration History: View a history of events signed up for, current and future.


For Organizers

Event Creation: Organizers can create new events and generate unique QR codes for attendee check-ins.

QR Code Management: Option to reuse existing QR codes for attendee check-ins.

Attendee Management: View the list of attendees who have checked into the event.

Communication: Send notifications to all attendees through the app.

Event Promotion: Upload event posters and generate unique promotion QR codes linking to event details.

Real-time Attendance Tracking: Track real-time attendance and receive alerts for important milestones.

QR Code Sharing: Share generated QR code images to other apps for dissemination.

Geolocation Insights: View attendee check-in locations on a map.

Attendance History: Monitor the frequency of attendee check-ins.

Attendee Registration: See who is signed up to attend the event.

Optional Attendance Limits: Optionally limit the number of attendees that can sign up for an event.


For Administrators

Event and Profile Management: Ability to remove events, profiles, and images.

Browse Functionality: Browse through events, profiles, and images for administrative purposes.


Installation

Clone the repository from GitHub.


Contributing

We welcome contributions from the community. Please follow these guidelines:

Fork the repository.

Create your feature branch (git checkout -b feature/YourFeature).

Commit your changes (git commit -am 'Add some feature').

Push to the branch (git push origin feature/YourFeature).

Create a new Pull Request.

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
