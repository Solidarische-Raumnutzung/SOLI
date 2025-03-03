package edu.kit.hci.soli.test.service;

import edu.kit.hci.soli.service.TimeService;
import edu.kit.hci.soli.test.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = {"dev", "test"})
public class TimeServiceTest {
    @Autowired private TimeService timeService;
    @Autowired private TestService testService;

    @Test
    public void testRelative() {
        int weekdayOffset = switch (timeService.currentSlot().getDayOfWeek()) {
            case SATURDAY -> 2;
            case SUNDAY -> 1;
            default -> 0;
        };
        LocalDateTime currentSlotNormalized = timeService.currentSlot().plusDays(weekdayOffset);
        if (weekdayOffset == 0) assertFalse(currentSlotNormalized.isAfter(timeService.minimumTime(testService.room)));
        else assertTrue(currentSlotNormalized.isAfter(timeService.minimumTime(testService.room)));
        assertTrue(currentSlotNormalized.isBefore(timeService.maximumTime(testService.room)));
        assertTrue(currentSlotNormalized.plusMinutes(16).isAfter(timeService.minimumTime(testService.room)));
    }

    @Test
    public void testDefaultTimeZoneSet() {
        // We set our current timezone in init, but depending on it probably isn't good
        // Make sure we set it correctly anyway
        // as there will be a slight delay between the two timestamps, allow for a bit of leeway
        LocalDateTime ldt1 = timeService.now();
        LocalDateTime ldt2 = LocalDateTime.now();
        assertTrue(ldt1.isBefore(ldt2) || ldt1.isEqual(ldt2));
        assertTrue(ldt1.plusSeconds(5).isAfter(ldt2) || ldt1.plusSeconds(5).isEqual(ldt2));
    }
}