package edu.kit.hci.soli.service.impl;

import edu.kit.hci.soli.config.SoliConfiguration;
import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class TimeServiceImpl implements TimeService {
    private final SoliConfiguration soliConfiguration;

    /**
     * Constructs a new {@code TimeServiceImpl} instance.
     *
     * @param soliConfiguration the app configuration
     */
    public TimeServiceImpl(SoliConfiguration soliConfiguration) {
        this.soliConfiguration = soliConfiguration;
    }

    @Override
    public LocalDateTime normalize(LocalDateTime time) {
        return time.minusMinutes(time.getMinute() % 15).withSecond(0).withNano(0);
    }

    @Override
    public LocalDateTime currentSlot() {
        return normalize(now());
    }

    @Override
    public LocalDateTime minimumTime(Room room) {
        LocalDateTime ldt = normalize(now().plusMinutes(15));
        return switch (ldt.getDayOfWeek()) {
            case SATURDAY -> minimize(room ,ldt.plusDays(2));
            case SUNDAY -> minimize(room, ldt.plusDays(1));
            default -> ldt;
        };
    }

    private LocalDateTime minimize(Room room, LocalDateTime ldt) {
        return room.getOpeningHours().get(ldt.getDayOfWeek()).getStart().atDate(ldt.toLocalDate());
    }

    @Override
    public LocalDateTime maximumTime(Room room) {
        LocalDateTime ldt = normalize(now().plusDays(14));
        return switch (ldt.getDayOfWeek()) {
            case SATURDAY -> maximize(room, ldt.minusDays(1));
            case SUNDAY -> maximize(room, ldt.minusDays(2));
            default -> ldt;
        };
    }

    private LocalDateTime maximize(Room room, LocalDateTime ldt) {
        return room.getOpeningHours().get(ldt.getDayOfWeek()).getEnd().atDate(ldt.toLocalDate());
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(soliConfiguration.getTimeZone().toZoneId());
    }
}
