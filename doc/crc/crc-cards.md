# User

| Responsibilities                     | Collaborators                             |
| ------------------------------------ | ----------------------------------------- |
| disable/enable geolocation tracking  | [Attendee](#attendee) (inherit)           |
| hold information common to all users | [Organizer](#organizer) (inherit)         |
|                                      | [Administrator](#administrator) (inherit) |

| Details                                                                                                                  |
| ------------------------------------------------------------------------------------------------------------------------ |
| This class will be extended by [Attendee](#attendee), [Organizer](#organizer) and [Administrator](#administrator) class. |

# Organizer

| Responsibilities                  | Collaborators                         |
| --------------------------------- | ------------------------------------- |
| create or reuse QR codes          | [Event](#event) (creates)             |
| upload event posters              | [Attendee](#attendee) (can view list) |
| hold information about organizers |                                       |

| Details                                     |
| ------------------------------------------- |
| This class will extend [User](#user) class. |

# Attendee

| Responsibilities                                      | Collaborators                      |
| ----------------------------------------------------- | ---------------------------------- |
| keeps track of attendee information (like attendance) | [Organizer](#organizer) (can view) |

| Details |
| ------- |
|         |

# Administrator

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Event

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Event View

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Attendee Settings

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Attende View

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Organizer View

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |

# Administrator View

| Responsibilities    | Collaborators                      |
| ------------------- | ---------------------------------- |
| can delete Event    | [Event](#event) (can delete)       |
| can delete profiles | [Attendee](#attendee) (can delete) |
| can delete images   |                                    |

| Details |
| ------- |
|         |

# QR Fragment

| Responsibilities | Collaborators |
| ---------------- | ------------- |
| some text        | some text     |

| Details |
| ------- |
|         |
