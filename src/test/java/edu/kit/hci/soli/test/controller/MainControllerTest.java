package edu.kit.hci.soli.test.controller;

import edu.kit.hci.soli.controller.MainController;
import edu.kit.hci.soli.dto.KnownError;
import edu.kit.hci.soli.test.TestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = {"dev", "test"})
public class MainControllerTest {
    @Autowired private MainController mainController;
    @Autowired private TestService testService;

    @BeforeAll
    public static void clean(@Autowired TestService testService) {
        testService.reset();
    }

    @AfterEach
    public void tearDown() {
        testService.reset();
    }

    @Test
    public void testHandleError_NotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model model = new ExtendedModelMap();

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", 404);
        errorAttributes.put("error", "Not Found");
        errorAttributes.put("message", "No message available");
        errorAttributes.put("path", "/some-path");

        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(404);
        when(request.getAttribute("jakarta.servlet.error.message")).thenReturn("No message available");
        when(request.getAttribute("jakarta.servlet.error.request_uri")).thenReturn("/some-path");

        String view = mainController.handleError(model, request);

        assertEquals("error/known", view);
        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
    }

    @Test
    public void testHandleError_OtherError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model model = new ExtendedModelMap();

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", 500);
        errorAttributes.put("error", "Internal Server Error");
        errorAttributes.put("message", "An unexpected error occurred");
        errorAttributes.put("path", "/some-path");

        when(request.getAttribute("jakarta.servlet.error.status_code")).thenReturn(500);
        when(request.getAttribute("jakarta.servlet.error.message")).thenReturn("An unexpected error occurred");
        when(request.getAttribute("jakarta.servlet.error.request_uri")).thenReturn("/some-path");
        when(request.getAttribute("jakarta.servlet.error.timestamp")).thenReturn("2023-10-10T10:10:10");

        String view = mainController.handleError(model, request);

        assertEquals("error/unknown", view);
        assertEquals(500, model.getAttribute("status"));
        assertEquals("Internal Server Error", model.getAttribute("error"));
        assertEquals("An unexpected error occurred", model.getAttribute("message"));
        assertEquals("/some-path", model.getAttribute("path"));
    }

    @Test
    public void testGetDisabledPage() {
        ExtendedModelMap model = new ExtendedModelMap();
        String result = mainController.getDisabled(model);
        assertEquals("error/disabled_user", result);
    }

    @Test
    public void testSecurityTxt() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        String result = mainController.securityTxt(response);
        assertTrue(result.startsWith("""
                Contact: soli@iar.kit.edu
                Expires: 2099-12-31T23:00:00.000Z
                Preferred-Languages: de, en
                Canonical: """));
        assertTrue(result.endsWith(".well-known/security.txt\n"));
    }

    @Test
    public void testPublisherInfoEN() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        String result = mainController.publisherInfo(response, request);
        assertTrue(result.startsWith("Contact: soli@iar.kit.edu"));
    }

    @Test
    public void testPublisherInfoDE() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getLocale()).thenReturn(Locale.GERMANY);

        String result = mainController.publisherInfo(response, request);
        assertTrue(result.startsWith("Kontakt: soli@iar.kit.edu"));
    }
}
