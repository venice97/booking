package com.tv.booking;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.tv.booking.servicedto.HoldRequest;
import com.tv.booking.servicedto.ReserveRequest;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TicketBookingApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class TicketBookingAppTests {

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate testRestTemplate;

	
	@Test
	public void whenSendingRequestToManagementEndpointExpectReturn200() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.mgt + "/info", Map.class);
		Assert.assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	public void whenSendingTotalAvailableRequestExpectReturnANumber() throws Exception {
	
		ResponseEntity<Integer> responseEntity = this.testRestTemplate.getForEntity(
				 "/booking/totalAvailable", Integer.class);
		Integer capacity = (Integer) responseEntity.getBody();
		Assert.assertTrue( capacity > 0 );
	}
	
	@Test
	public void whenSendingHoldRequestExpectReturnSeatHoldId() {
	    HoldRequest holdRequest = new HoldRequest();
	    holdRequest.setCustomerEmail("foobar@gmail.com");
	    holdRequest.setNumSeats(15);
	       
		ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
	                "/booking/hold", holdRequest, String.class);
	    String seatHoldId = (String) responseEntity.getBody();
	    Assert.assertTrue(seatHoldId.contains("HD"));
	}
	
	@Test
	public void whenSendingReserveRequestForInvalidSeatHoldIdExpectReturnException() {
	    ReserveRequest reserveRequest = new ReserveRequest();
	    reserveRequest.setCustomerEmail("foobar@gmail.com");
	    reserveRequest.setSeatHoldId("invalid-seat-hold-id");
	       
	    ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
	    		"/booking/reserve", reserveRequest, String.class);    
	    Assert.assertTrue(responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST);
	}	
}
