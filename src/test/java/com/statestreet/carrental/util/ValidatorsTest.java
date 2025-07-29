package com.statestreet.carrental.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "JBLVA2AE4EH87", // too short
            "JBLVA2AE4EH877002999455", // too long
            "jbLVA2AE4eh877002", // small letters
            "JBLVA2AE4 EH877002" // space
    })
    public void validateVin_invalidTest(String vinString) {
        assertThrows(ValidationException.class, () -> Validators.validateVin(vinString));
    }

    @Test
    public void validateVin_validTest() {
        assertDoesNotThrow(() -> Validators.validateVin("JBLVA2AE4EH877002"));
    }

}
