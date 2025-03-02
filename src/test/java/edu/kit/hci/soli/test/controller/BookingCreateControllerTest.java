package edu.kit.hci.soli.test.controller;

import edu.kit.hci.soli.controller.BookingCreateController;
import edu.kit.hci.soli.domain.*;
import edu.kit.hci.soli.dto.BookingAttemptResult;
import edu.kit.hci.soli.dto.KnownError;
import edu.kit.hci.soli.dto.form.CreateEventForm;
import edu.kit.hci.soli.repository.RoomRepository;
import edu.kit.hci.soli.service.BookingsService;
import edu.kit.hci.soli.service.RoomService;
import edu.kit.hci.soli.service.TimeService;
import edu.kit.hci.soli.test.Either;
import edu.kit.hci.soli.test.TestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = {"dev", "test"})
public class BookingCreateControllerTest {
    @Autowired private TestService testService;
    @Autowired private BookingCreateController bookingsController;
    @Autowired private TimeService timeService;
    @Autowired private RoomRepository roomRepository;
    @Autowired
    private BookingsService bookingsService;
    @Autowired
    private RoomService roomService;

    @BeforeAll
    public static void clean(@Autowired TestService testService) {
        testService.reset();
    }

    @AfterEach
    public void tearDown() {
        testService.reset();
    }

    private Either<KnownError, Booking> lsmCreateBooking(CreateEventForm formData, User user, long room) {
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        String result = bookingsController.createBooking(model, response, request, room, testService.paramsFor(user, request), () -> user, formData);
        if (result.equals("error/known")) {
            return new Either.Left<>((KnownError) model.get("error"));
        }
        assertEquals("redirect:/" + room, result);
        List<Booking> bookings = bookingsService.getBookingsByUser(user, roomService.getOptional(room).orElseThrow(), 0, 10).toList();
        assertEquals(1, bookings.size());
        return new Either.Right<>(bookings.getFirst());
    }

    @Test
    public void testIllegalRoom() {
        CreateEventForm formData = new CreateEventForm(
                timeService.currentSlot().plusMinutes(30),
                timeService.currentSlot().plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.NOT_FOUND, lsmCreateBooking(formData, testService.user, -1).assertLeft());
    }

    @Test
    public void testMissingArguments() {
        CreateEventForm formData = new CreateEventForm(
                null,
                null,
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.MISSING_PARAMETER, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testMisalignedTime() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).minusMinutes(31),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testMissingArgument_End() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusMinutes(30),
                null,
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.MISSING_PARAMETER, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testMissingArgument_Priority() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusMinutes(30),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                null,
                ShareRoomType.NO
        );
        assertEquals(KnownError.MISSING_PARAMETER, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testMissingArgument_Cooperative() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusMinutes(30),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                null
        );
        assertEquals(KnownError.MISSING_PARAMETER, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testLargeTime() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).minusMinutes(30),
                timeService.minimumTime(testService.room).plusHours(10).toLocalTime(),
                "",
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    public void testPastTime() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).minusMinutes(15),
                timeService.minimumTime(testService.room).plusMinutes(15).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartAfterEnd_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusHours(2),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartBeforeMinimumTime_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).minusMinutes(15),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testEndAfterMaximumTime_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusMinutes(30),
                timeService.maximumTime(testService.room).plusMinutes(15).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartNotMultipleOf15Minutes_ReturnsInvalidTimeError() {
        LocalDateTime min = timeService.minimumTime(testService.room);
        CreateEventForm formData = new CreateEventForm(
                min.plusMinutes(7),
                min.plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(min, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertRight().getStartDate());

    }

    @Test
    void testEndNotMultipleOf15Minutes_ReturnsInvalidTimeError() {
        LocalDateTime min = timeService.minimumTime(testService.room);
        CreateEventForm formData = new CreateEventForm(
                min.plusMinutes(30),
                min.plusHours(1).plusMinutes(7).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(min.plusHours(1), lsmCreateBooking(formData, testService.user, testService.room.getId()).assertRight().getEndDate());
    }

    @Test
    void testStartAndEndOnDifferentDays_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).withHour(23).withMinute(45),
                timeService.minimumTime(testService.room).plusDays(1).withHour(0).withMinute(15).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartOnSaturday_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).with(DayOfWeek.SATURDAY),
                timeService.minimumTime(testService.room).with(DayOfWeek.SATURDAY).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartOnSunday_ReturnsInvalidTimeError() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).with(DayOfWeek.SUNDAY),
                timeService.minimumTime(testService.room).with(DayOfWeek.SUNDAY).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, testService.room.getId()).assertLeft());
    }

    @Test
    void testStartBeforeOpeningHours_ReturnsInvalidTimeError() {
        Room room = testService.room;
        room.setOpeningHours(Map.of(DayOfWeek.MONDAY, new TimeTuple(LocalTime.of(9, 0), LocalTime.of(17, 0))));
        room = roomRepository.save(room);
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).with(DayOfWeek.MONDAY).withHour(8),
                timeService.minimumTime(testService.room).with(DayOfWeek.MONDAY).withHour(10).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, room.getId()).assertLeft());
    }

    @Test
    void testEndAfterClosingHours_ReturnsInvalidTimeError() {
        Room room = testService.room;
        room.setOpeningHours(Map.of(DayOfWeek.MONDAY, new TimeTuple(LocalTime.of(9, 0), LocalTime.of(17, 0))));
        room = roomRepository.save(room);
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).with(DayOfWeek.MONDAY).withHour(16),
                timeService.minimumTime(testService.room).with(DayOfWeek.MONDAY).withHour(18).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        assertEquals(KnownError.INVALID_TIME, lsmCreateBooking(formData, testService.user, room.getId()).assertLeft());
    }

    @Test
    public void testCreateBooking() {
        CreateEventForm formData = new CreateEventForm(
                timeService.minimumTime(testService.room).plusMinutes(30),
                timeService.minimumTime(testService.room).plusHours(1).toLocalTime(),
                null,
                Priority.HIGHEST,
                ShareRoomType.NO
        );
        lsmCreateBooking(formData, testService.user, testService.room.getId()).assertRight();
    }

    @Test
    public void testNewBooking_RoomNotFound() {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        when(roomService.getOptional(1L)).thenReturn(Optional.empty());

        String view = bookingsController.newBooking(model, response, 1L, testService.paramsFor(testService.user, request), null, null, null);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
        assertEquals("error/known", view);
    }

    @Test
    public void testNewBooking_StartAndEndNull() {
        Room room = new Room();
        room.setId(1L);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(timeService.minimumTime(room)).thenReturn(LocalDateTime.now().minusDays(1));
        when(timeService.maximumTime(room)).thenReturn(LocalDateTime.now().plusDays(1));

        String view = bookingsController.newBooking(model, response, 1L, testService.paramsFor(testService.user, request), null, null, null);

        assertEquals("bookings/create/form", view);
        assertEquals(room, request.getSession().getAttribute("room"));
        assertEquals(LocalTime.now().plusMinutes(30).getMinute(), ((LocalTime) model.getAttribute("end")).getMinute());
        assertEquals(LocalDateTime.now().getMinute(), ((LocalDateTime) model.getAttribute("start")).getMinute());
        assertEquals(ShareRoomType.NO, model.getAttribute("cooperative"));
    }

    @Test
    public void testNewBooking_CooperativeNull() {
        Room room = new Room();
        room.setId(1L);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalTime end = start.toLocalTime().plusMinutes(30);
        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(timeService.minimumTime(testService.room)).thenReturn(LocalDateTime.now().minusDays(1));
        when(timeService.maximumTime(testService.room)).thenReturn(LocalDateTime.now().plusDays(1));

        String view = bookingsController.newBooking(model, response, 1L, testService.paramsFor(testService.user, request), start, end, null);

        assertEquals("bookings/create/form", view);
        assertEquals(room, request.getSession().getAttribute("room"));
        assertEquals(start, model.getAttribute("start"));
        assertEquals(end, model.getAttribute("end"));
        assertEquals(ShareRoomType.NO, model.getAttribute("cooperative"));
    }

    @Test
    public void testNewBooking_AllParametersProvided() {
        Room room = new Room();
        room.setId(1L);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalTime end = start.toLocalTime().plusMinutes(30);
        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(timeService.minimumTime(testService.room)).thenReturn(LocalDateTime.now().minusDays(1));
        when(timeService.maximumTime(testService.room)).thenReturn(LocalDateTime.now().plusDays(1));

        String view = bookingsController.newBooking(model, response, 1L, testService.paramsFor(testService.user, request), start, end, true);

        assertEquals("bookings/create/form", view);
        assertEquals(room, request.getSession().getAttribute("room"));
        assertEquals(start, model.getAttribute("start"));
        assertEquals(end, model.getAttribute("end"));
        assertEquals(ShareRoomType.YES, model.getAttribute("cooperative"));
    }

    @Test
    public void testResolveConflict_AttemptedBookingNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("attemptedBooking", null);
        Model model = new ExtendedModelMap();

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
        assertEquals("error/known", view);
    }

    @Test
    public void testResolveConflict_RoomNotFound() {
        Booking attemptedBooking = new Booking();
        HttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("attemptedBooking", attemptedBooking);
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        when(roomService.getOptional(1L)).thenReturn(Optional.empty());

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
        assertEquals("error/known", view);
    }

    @Test
    public void testResolveConflict_RoomIdMismatch() {
        Room room = new Room();
        room.setId(2L);
        HttpServletRequest request = new MockHttpServletRequest();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        Booking attemptedBooking = new Booking();
        attemptedBooking.setRoom(room);

        request.getSession().setAttribute("attemptedBooking", attemptedBooking);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals(KnownError.NOT_FOUND, model.getAttribute("error"));
        assertEquals("error/known", view);
    }

    @Test
    public void testResolveConflict_SuccessfulBookingAttempt() {
        assertEquals(true, true);

        Room room = new Room();
        room.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        Booking attemptedBooking = new Booking();
        attemptedBooking.setRoom(room);

        BookingAttemptResult.PossibleCooperation bookingResult = new BookingAttemptResult.PossibleCooperation.Immediate(List.of(), List.of());

        request.getSession().setAttribute("attemptedBooking", attemptedBooking);
        request.getSession().setAttribute("bookingResult", bookingResult);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(bookingsService.affirm(attemptedBooking, bookingResult)).thenReturn(new BookingAttemptResult.Success(attemptedBooking));

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals("redirect:/" + room.getId(), view);
    }

    @Test
    public void testResolveConflict_FailedBookingAttempt() {
        Room room = new Room();
        room.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        Booking attemptedBooking = new Booking();
        attemptedBooking.setRoom(room);

        BookingAttemptResult.PossibleCooperation bookingResult = new BookingAttemptResult.PossibleCooperation.Immediate(List.of(), List.of());

        request.getSession().setAttribute("attemptedBooking", attemptedBooking);
        request.getSession().setAttribute("bookingResult", bookingResult);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(bookingsService.affirm(attemptedBooking, bookingResult)).thenReturn(new BookingAttemptResult.Failure(List.of()));

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals(KnownError.EVENT_CONFLICT, model.getAttribute("error"));
        assertEquals("error/known", view);
    }

    @Test
    public void testResolveConflict_StagedBookingAttempt() {
        Room room = new Room();
        room.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        Booking attemptedBooking = new Booking();
        attemptedBooking.setRoom(room);

        BookingAttemptResult.PossibleCooperation bookingResult = new BookingAttemptResult.PossibleCooperation.Immediate(List.of(), List.of());

        request.getSession().setAttribute("attemptedBooking", attemptedBooking);
        request.getSession().setAttribute("bookingResult", bookingResult);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(bookingsService.affirm(attemptedBooking, bookingResult)).thenReturn(new BookingAttemptResult.Staged(attemptedBooking));

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals("bookings/create/success_staged", view);
    }

    @Test
    public void testResolveConflict_PossibleCooperation() {
        Room room = new Room();
        room.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Model model = new ExtendedModelMap();
        RoomService roomService = mock(RoomService.class);
        BookingsService bookingsService = mock(BookingsService.class);
        TimeService timeService = mock(TimeService.class);
        BookingCreateController bookingsController = new BookingCreateController(timeService, bookingsService, roomService);

        Booking attemptedBooking = new Booking();
        attemptedBooking.setRoom(room);

        BookingAttemptResult.PossibleCooperation bookingResult = new BookingAttemptResult.PossibleCooperation.Immediate(List.of(), List.of());

        request.getSession().setAttribute("attemptedBooking", attemptedBooking);
        request.getSession().setAttribute("bookingResult", bookingResult);

        when(roomService.getOptional(1L)).thenReturn(Optional.of(room));
        when(bookingsService.affirm(attemptedBooking, bookingResult)).thenReturn(bookingResult);

        String view = bookingsController.resolveConflict(model, request, 1L, testService.paramsFor(testService.user, request));

        assertEquals("bookings/create/conflict", view);
    }
}
