package edu.kit.hci.soli.test.controller;

import edu.kit.hci.soli.controller.RoomsController;
import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.dto.KnownError;
import edu.kit.hci.soli.dto.LayoutParams;
import edu.kit.hci.soli.dto.form.EditOrCreateRoomForm;
import edu.kit.hci.soli.service.RoomService;
import edu.kit.hci.soli.test.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = {"dev", "test"})
public class RoomsControllerTest {
    @Autowired private TestService testService;
    @Autowired private RoomsController roomsController;
    @Autowired private RoomService roomService;

    @BeforeAll
    public static void clean(@Autowired TestService testService) {
        testService.reset();
    }

    @AfterEach
    public void tearDown() {
        testService.reset();
    }

    @Test
    public void testDeleteUnknownRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertEquals("error/known", roomsController.deleteRoom(model, response, testService.room.getId() + 1, testService.paramsFor(testService.user, request)));
        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
        assertEquals(1, roomService.getAll().size());
    }

    @Test
    public void testDeleteRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertEquals("redirect:/admin/rooms", roomsController.deleteRoom(model, response, testService.room.getId(), testService.paramsFor(testService.user, request)));
        assertEquals(0, roomService.getAll().size());
    }

    @Test
    public void testDeleteSelectedRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        LayoutParams layoutParams = testService.paramsFor(testService.user, request);
        layoutParams.setRoom(testService.room);
        assertEquals("redirect:/admin/rooms", roomsController.deleteRoom(model, response, testService.room.getId(), layoutParams));
        assertNull(layoutParams.getRoom());
        assertEquals(0, roomService.getAll().size());
    }

    @Test
    public void testEditUnknownRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        EditOrCreateRoomForm formData = new EditOrCreateRoomForm(testService.room.getId() + 1, "Testraum", "Beschreibung", "Ort");
        assertEquals("error/known", roomsController.editOrCreateRoom(model, response, formData));
        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
    }

    @Test
    public void testEditRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        EditOrCreateRoomForm formData = new EditOrCreateRoomForm(testService.room.getId(), "Testraum", "Beschreibung", "Ort");
        assertEquals("redirect:/admin/rooms", roomsController.editOrCreateRoom(model, response, formData));
        assertEquals("Beschreibung", roomService.getOptional(testService.room.getId()).orElseThrow().getDescription());
    }

    @Test
    public void testCreateRoom() {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        EditOrCreateRoomForm formData = new EditOrCreateRoomForm(null, "Testraum", "Beschreibung", "Ort");
        assertEquals("redirect:/admin/rooms", roomsController.editOrCreateRoom(model, response, formData));
        assertEquals("Beschreibung", roomService.getOptional(testService.room.getId() + 1).orElseThrow().getDescription());
    }
}
