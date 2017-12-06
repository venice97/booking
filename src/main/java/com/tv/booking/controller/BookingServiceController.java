package com.tv.booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tv.booking.domain.SeatHold;
import com.tv.booking.service.TicketService;
import com.tv.booking.servicedto.HoldRequest;
import com.tv.booking.servicedto.ReserveRequest;

@Controller
@RequestMapping("/booking")
public class BookingServiceController {

	@Autowired
	TicketService ticketService;

    public static final Logger logger = LoggerFactory.getLogger(BookingServiceController.class);

    @RequestMapping(value = "totalAvailable", method=RequestMethod.GET)
    public @ResponseBody Integer retrieveNumSeatsAvailable () {
        return ticketService.numSeatsAvailable();
    }
    
    @RequestMapping(value = "hold", method=RequestMethod.POST)
    public @ResponseBody String holdTickets(
    	   @RequestBody HoldRequest holdRequest) {
    	
        SeatHold seatHold = ticketService.findAndHoldSeats(
        	holdRequest.getNumSeats(), holdRequest.getCustomerEmail());
        
        return seatHold.getSeatHoldId();
    }
    
    @RequestMapping(value = "reserve", method=RequestMethod.POST)
    public @ResponseBody String reserveTickets(
    	   @RequestBody ReserveRequest reserveRequest) {
        return ticketService.reserveSeats(
        	reserveRequest.getSeatHoldId(), reserveRequest.getCustomerEmail());
    }

}
