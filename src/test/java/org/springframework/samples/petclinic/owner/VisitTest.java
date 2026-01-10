package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisitTest {
	@Test
	void testSetDateUpdatesValue() {
		Visit visit = new Visit();

		LocalDate initialDate = visit.getDate();

		LocalDate newDate = initialDate.plusDays(1);
		visit.setDate(newDate);

		assertEquals(newDate, visit.getDate(), "The date should match the new value we set");

		assertNotEquals(initialDate, visit.getDate(), "The date should have changed");
		assertTrue(visit.getDate().isAfter(initialDate), "The new date should be after the initial date");
	}
}

