/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PetValidator}
 *
 * @author Wick Dynex
 */
@ExtendWith(MockitoExtension.class)
@DisabledInNativeImage
public class PetValidatorTests {

	private PetValidator petValidator;

	private Pet pet;

	private PetType petType;

	private Errors errors;

	private static final String petName = "Buddy";

	private static final String petTypeName = "Dog";

	private static final LocalDate petBirthDate = LocalDate.of(1990, 1, 1);



	@BeforeEach
	void setUp() {
		petValidator = new PetValidator();
		pet = new Pet();
		petType = new PetType();
		errors = new MapBindingResult(new HashMap<>(), "pet");
	}

	@Test
	void testValidate() {
		petType.setName(petTypeName);
		preparePetInstance(petName, petType, petBirthDate);
		petValidator.validate(pet, errors);

		assertFalse(errors.hasErrors());
	}

	@Nested
	class ValidateHasErrors {

		@Test
		void testValidateWithWhitespacePetName() {
			petType.setName(petTypeName);
			preparePetInstance("   ", petType, petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("name"));
		}

		@Test
		void testValidateWithInvalidPetName() {
			petType.setName(petTypeName);
			preparePetInstance("", petType, petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("name"));
		}

		@Test
		void testValidateWithNullPetName() {
			petType.setName(petTypeName);
			preparePetInstance(null, petType, petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("name"));
		}

		@Test
		void testValidateWithInvalidPetType() {
			preparePetInstance(petName, null, petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("type"));
		}

		@Test
		void testValidateWithInvalidBirthDate() {
			petType.setName(petTypeName);
			preparePetInstance(petName, petType, null);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("birthDate"));
		}

		@Test
		void testValidateWithMultipleErrors() {
			pet.setName("");

			pet.setBirthDate(null);

			pet.setType(petType);

			petValidator.validate(pet, errors);

			// Assert that we have error count of 2
			assertEquals(2, errors.getErrorCount());

			// Assert specifically which fields failed
			assertTrue(errors.hasFieldErrors("name"));
			assertTrue(errors.hasFieldErrors("birthDate"));
		}

		@Test //Ot Pet Validator.java    if (pet.isNew() && pet.getType() == null) {
		void testValidateExistingPetWithoutType() {
			pet.setId(1);
			pet.setName(petName);
			pet.setBirthDate(petBirthDate);
			pet.setType(null);

			petValidator.validate(pet, errors);

			// We expect NO errors because the 'if (pet.isNew()...)' block is skipped
			assertFalse(errors.hasFieldErrors("type"));
		}


		@ParameterizedTest
		@ValueSource(strings = {"123", "Fluffy @ Home", "Müller", "O'Connor"})
		void shouldAcceptExtraordinaryNames(String unusualName) {
			petType.setName(petTypeName);
			preparePetInstance(unusualName, petType, petBirthDate);

			petValidator.validate(pet, errors);

			assertFalse(errors.hasErrors());
		}

	}

	// --- ЗАДАЧА 3 (isNew && type == null)
	// We use booleans for clarity: isNew, hasType, expectError
	@ParameterizedTest(name = "isNew={0}, hasType={1} -> Error Expected={2}")
	@CsvSource({
		"true,  false, true",   // 1. New Pet + No Type = ERROR (This is the specific check)
		"false, false, false",  // 2. Existing Pet + No Type = NO Error
		"true,  true,  false",  // 3. New Pet + Has Type = NO Error
		"false, true,  false"   // 4. Existing Pet + Has Type = NO Error
	})
	void testValidateNewAndType(boolean isNew, boolean hasType, boolean expectError) {
		//isNew true/false
		if (isNew) {
			pet.setId(null); // ID null means isNew() is true
		} else {
			pet.setId(1);    // ID set means isNew() is false
		}

		if (hasType) {
			pet.setType(petType); // Set Valid Type -> true
		} else {
			pet.setType(null);    // Set Null Type -> false
		}

		// 3. Reset other fields to valid state so they dont interfere
		pet.setName(petName);
		pet.setBirthDate(petBirthDate);

		// 4. Validate
		petValidator.validate(pet, errors);

		// 5. Assert
		if (expectError) {
			assertTrue(errors.hasFieldErrors("type"), "Should have error because Pet is New and Type is Null");
		} else {
			assertFalse(errors.hasFieldErrors("type"), "Should NOT have error for this combination");
		}
	}

	// --- HELPER METHOD (Must be separate!) ---
	private void preparePetInstance(String petName, PetType petType, LocalDate petBirthDate) {
		pet.setName(petName);
		pet.setType(petType);
		pet.setBirthDate(petBirthDate);
	}
}
