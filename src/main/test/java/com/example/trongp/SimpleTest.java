package com.example.trongp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {

    @Test
    void testOutput() {
        System.out.println("Test is running!");
        assert(true); // Just to make sure this runs without assertion errors
    }
}
