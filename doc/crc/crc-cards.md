# Organizer

| Responsibilities                  | Collaborators                         |
| --------------------------------- | ------------------------------------- |
| create or reuse QR codes          | [Event](#event) (creates)             |
| upload event posters              | [Attendee](#attendee) (can view list) |
| hold information about organizers |                                       |

| Details                                     |
| ------------------------------------------- |
| This class will extend [User](#user) class. |

# User

| Responsibilities                                   | Collaborators                         |
| ---------------------------------------------------| ------------------------------------- |
| disable/enable geolocation tracking                | [Attendee](#attendee) (inherit)       |
| hold information common to all users               | [Organizer](#organizer) (inherit)     |
|                                                    | [Administrator](#administrator) (inherit)  |

| Details                                             |
| --------------------------------------------------- |
| This class will be extended by [Attendee](#attendee), [Organizer](#organizer) and [Administrator](#administrator) class. |