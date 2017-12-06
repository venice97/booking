##### **Project Description**

This project implements a simple ticket service. Ticket service provides the following functions:

- Find the number of seats available within the venue.  Available seats are seats that are neither held nor reserved.
- Find and hold the best available seats on behalf of a customer. Each ticket hold should expire within a set number of seconds. 
- Reserve and commit a specific group of held seats for a customer

##### **Assumptions**

- The avenue arena seating has rows of seats.  The number of seats for each row is the same for all rows.
- Twenty-percent of seats in each row in the center is considered as premier seats.  Center seats and seats in row closer to the stage are considered better seats.  Reserving process assigns center seats for each row first then goes back to select starting with the first seat in that row.
- When a request is sent to the ticketing service, all previously-held seats are checked for expiration status and are released accordingly.

##### Application Design

- Module provides the following REST APIs for the ticketing service
  - /booking/totalAvailable - Find the number of seats available within the venue
    - Request parameter:  None
    - Response:  number of available seats
  - /booking/hold - Find and hold the best available seats on behalf of a customer
    -  Request:  JSON format
      - numSeats - number of seats to hold
      - customerEmail - customer email
    - Response:  returns a seat hold id with 'HD' prefix, this id is needed for ticket reservation.  Response 400 error code is returned for error conditions.
  - /booking/reserve - Reserve and commit a specific group of held seats for a customer
    - Request: JSON format
      - seatHoldId - the unique seat hold id 
      - customerEmail - customer email
    - Response:  returns a confirmed reservation id with 'RD' prefix. Response 400 error code is returned for error conditions.


- Following software related items are included:
  - Configuration of the arena seating capacity and ticket hold expiration time 
  - Unit testing of service layer
  - Sample unit testing of Spring controller for each API
  - API error conditions are provided

##### Build and Run Instructions

**Prerequisites:**

​	Maven:  version 3.5.2

​	Java JDK:  version 1.8.0_152

​ Build and run scripts are written for Windows-based systems. Open DOS command prompt and cd to 'booking' folder

​		  build.bat

​		  run.bat 

​ APIs can be tested with Postman, please import file:  

​		  testbookingcollection.postman_collection.json
