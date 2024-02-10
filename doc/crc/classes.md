# Entities

## User

- abstract class
- can disable/enable geolocation tracking (03.02.01)

## Attendee

- has an attribute attendance (01.05.01)
- keeps track of check in count (01.09.01)
- can have profile picture (02.02.01)

## Organizer

- can create [Event](#event) (01.01.01)
- can create or reuse QR codes (01.01.02)
- can upload event posters (01.04.01)
- can view list of [Attendee](#attendee) (01.02.01)
- can send notifications to [Attendee](#attendee) (02.03.01)

## Administrator

- has special permissions (deletions)

## Event

- has a list of [Attendee](#attendee)
- can have a poster

## Map

# UI Elements

## Event View

- can display details about the events (02.04.01)
- can display announcements about events (02.04.01)
- can display event poster

## Attendee Settings

- allows user to update information (02.02.03)
- can generate profile picture from profile name (02.05.01)

## Attendee View

- can display and scan QR code (02.01.01)
- can upload and remove profile picture (02.02.01)

## Organizer View

- can upload event poster (01.04.01)
- can see [Map](#map)

## Administrator View

- can delete events (04.01.01)
- can delete profiles (04.02.01)
- can delete images (04.03.01)

## QR Fragment

- displays QR codes (01.01.02)
