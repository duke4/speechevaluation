package de.mkcode.speechprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SpeechprocessingApplicationIT {

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testProcessStatistics_withoutUrls() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		final String url = "http://localhost:8081/evaluation";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		String expected = "{mostSpeeches:null,mostSecurity:null,leastWordy:null}";

		try {
			JSONAssert.assertEquals(expected, response.getBody(), false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testProcessStatistics() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		final String url = "http://localhost:8081/evaluation" +
							"?url1=http://localhost:8081/statistics1.csv" +
							"&url2=http://localhost:8081/statistics2.csv";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		String expected = "{mostSpeeches:null,mostSecurity:\"Caesare Collins\",leastWordy:null}";

		try {
			JSONAssert.assertEquals(expected, response.getBody(), false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testProcessStatistics_uncoveredUrl() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		final String url = "http://localhost:8081/evaluations";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}
