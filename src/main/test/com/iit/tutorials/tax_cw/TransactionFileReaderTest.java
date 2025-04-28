package com.iit.tutorials.tax_cw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFileReaderTest {

    @Test
    void calculateChecksum() {
        String test = "test";
        int expectedvalue = 4;
        assertEquals(expectedvalue, TransactionFileReader.calculateChecksum(test));
    }

    @Test
    void calculateChecksumSpecialCharacters() {
        String test = "test#$$";
        boolean hasSpecialChar = TransactionFileReader.calculateChecksum(test) != 0;
        assertTrue(hasSpecialChar);
    }

    @Test
    void calculateChecksumNumbers() {
        String test = "test123";
        int expectedvalue = 7;
        assertEquals(expectedvalue, TransactionFileReader.calculateChecksum(test));
    }
}