package org.springframework.samples.petclinic.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class PersonTest {
	// Helper method: Setup the Validator (copied from ValidatorTests.java)
	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}
	// New Helper method: Reduces duplication of running the validator in every test
	private Set<ConstraintViolation<Person>> getViolations(Person person) {
		Validator validator = createValidator();
		return validator.validate(person);
	}

	// Test Inheritance Logic (BaseEntity)
	@Test
	void testInheritedFields() {
		Person person = new Person();

		// New persons should be "new" (id is null)
		assertTrue(person.isNew(), "Person should be new when created");

		// Set an ID (inherited from BaseEntity)
		person.setId(100);

		// Check ID and verify it is no longer "new"
		assertEquals(100, person.getId());
		assertFalse(person.isNew(), "Person should not be new after setting ID");
	}

	@ParameterizedTest
	@ValueSource(strings = {"John", "Alice", "Bob", "Max"})
	void testSetAndGetFirstName(String name) {
		Person person = new Person();
		person.setFirstName(name);
		assertEquals(name, person.getFirstName());
	}

	@ParameterizedTest
	@ValueSource(strings = {"Doe", "Smith", "Wayne"})
	void testSetAndGetLastName(String name) {
		Person person = new Person();
		person.setLastName(name);
		assertEquals(name, person.getLastName());
	}

	// Exercise 8: Parametrized with Mixed Valid/Invalid Data
	@ParameterizedTest(name = "First='{0}', Last='{1}' -> Expected Errors={2}")
	@CsvSource({
		"' ', lastName, 1",       // Blank First Name
		"firstName, ' ', 1",      // Blank Last Name
		"'', lastName, 1",        // Empty First Name
		"firstName, '', 1",       // Empty Last Name
		"firstName, lastName, 0", // Happy Path
		"' ', ' ', 2",            // Both Blank
		"'', '', 2"               // Both Empty
	})
	void testPersonValidationMixed(String firstName, String lastName, int expectedErrorCount) {
		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);

		// Uses the new helper method to remove duplicate lines
		Set<ConstraintViolation<Person>> violations = getViolations(person);

		// The Assertion:
		assertThat(violations).hasSize(expectedErrorCount);
	}
}
