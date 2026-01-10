package org.springframework.samples.petclinic.system;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(WelcomeController.class)
class WelcomeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testWelcome() throws Exception {
		// Go to the home page "/"
		mockMvc.perform(get("/"))
			// Check we got a 200 OK
			.andExpect(status().isOk())
			// Check it served the "welcome" view
			.andExpect(view().name("welcome"));
	}
}
