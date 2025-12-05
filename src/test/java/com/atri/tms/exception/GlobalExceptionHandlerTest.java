package com.atri.tms.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");

        ResponseEntity<?> res = handler.handleNotFound(ex);

        assertEquals(404, res.getStatusCode().value());
        assertEquals("Not found", res.getBody());
    }
}
