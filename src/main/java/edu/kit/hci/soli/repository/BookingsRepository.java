package edu.kit.hci.soli.repository;

import edu.kit.hci.soli.domain.*;
import edu.kit.hci.soli.dto.BookingByDay;
import edu.kit.hci.soli.dto.BookingByHour;
import edu.kit.hci.soli.dto.BookingByMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Repository for managing {@link Booking} entities.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
public interface BookingsRepository extends JpaRepository<Booking, Serializable> {
    /**
     * Finds bookings that overlap with the specified time range.
     *
     * @param room  the room in which to search
     * @param start the start of the time range
     * @param end   the end of the time range
     * @return a stream of bookings that overlap with the specified time range
     */
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND ((b.startDate > :start AND b.startDate < :end) OR (b.endDate > :start AND b.endDate < :end) OR (b.startDate <= :start AND b.endDate >= :end))")
    Stream<Booking> findOverlappingBookings(Room room, LocalDateTime start, LocalDateTime end);

    /**
     * Finds bookings that overlap with the specified time range.
     *
     * @param room  the room in which to search
     * @param start the start of the time range
     * @param end   the end of the time range
     * @param user  the user to filter by
     * @return a stream of bookings that overlap with the specified time range
     */
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND b.user = :user AND ((b.startDate > :start AND b.startDate < :end) OR (b.endDate > :start AND b.endDate < :end) OR (b.startDate <= :start AND b.endDate >= :end))")
    Stream<Booking> findOverlappingBookings(Room room, LocalDateTime start, LocalDateTime end, User user);

    /**
     * Finds bookings that overlap with the specified time range and have open requests.
     *
     * @param room  the room in which to search
     * @param start the start of the time range
     * @param end   the end of the time range
     * @return a stream of bookings that overlap with the specified time range and have open requests
     */
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND b.openRequests IS NOT EMPTY AND ((b.startDate > :start AND b.startDate < :end) OR (b.endDate > :start AND b.endDate < :end) OR (b.startDate <= :start AND b.endDate >= :end))")
    Stream<Booking> findOverlappingWithOutstandingRequests(Room room, LocalDateTime start, LocalDateTime end);

    /**
     * Finds bookings by user and room.
     *
     * @param room the room associated with the bookings
     * @param user the user associated with the bookings
     * @param pageable the pagination information
     * @return the page of bookings
     */
    Page<Booking> findByUserAndRoom(User user, Room room, Pageable pageable);

    /**
     * Checks if a booking exists for the specified user.
     *
     * @param user the user to check for
     * @return true if a booking exists for the user, false otherwise
     */
    boolean existsByUser(User user);

    /**
     * Deletes all bookings for the specified user.
     *
     * @param user the user whose bookings are to be deleted
     */
    void deleteAllByUser(User user);

    /**
     * Finds bookings with outstanding requests for the specified user.
     *
     * @param user the user to check for
     * @return a stream of bookings with outstanding requests for the specified user
     */
    @Query("SELECT b FROM Booking b WHERE :user IN elements(b.openRequests)")
    Stream<Booking> findWithOutstandingRequests(User user);

    /**
     * Finds all bookings created by a guest.
     */
    @Query("SELECT b FROM Booking b WHERE b.startDate > :time AND b.id IN (SELECT b1.id FROM Booking b1 JOIN b1.user u WHERE u.userId LIKE 'guest/%')")
    Stream<Booking> findFutureBookingsByGuests(LocalDateTime time);

    /**
     * Returns the number of bookings per weekday (all time).
     *
     * @return the number of bookings per weekday
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByDay(DAY_OF_WEEK(b.startDate), COUNT(b)) FROM Booking b WHERE b.openRequests IS EMPTY GROUP BY DAY_OF_WEEK(b.startDate)")
    Stream<BookingByDay> countBookingsPerWeekdayAllTime();

    /**
     * Returns the number of bookings per weekday (recent).
     *
     * @param frame the amount of time to look back
     * @return the number of bookings per weekday
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByDay(DAY_OF_WEEK(b.startDate), COUNT(b)) FROM Booking b WHERE CURRENT_TIMESTAMP - b.endDate <= :frame AND b.openRequests IS EMPTY GROUP BY DAY_OF_WEEK(b.startDate)")
    Stream<BookingByDay> countBookingsPerWeekdayRecent(Duration frame);

    /**
     * Returns the number of bookings per hour of day (all time).
     *
     * @return the number of bookings per hour of day
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByHour(EXTRACT(HOUR FROM b.startDate), COUNT(b)) FROM Booking b WHERE b.openRequests IS EMPTY GROUP BY EXTRACT(HOUR FROM b.startDate)")
    Stream<BookingByHour> countBookingsPerHourAllTime();

    /**
     * Returns the number of bookings per hour of day (recent).
     *
     * @param frame the amount of time to look back
     * @return the number of bookings per hour of day
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByHour(EXTRACT(HOUR FROM b.startDate), COUNT(b)) FROM Booking b WHERE CURRENT_TIMESTAMP - b.endDate <= :frame AND b.openRequests IS EMPTY GROUP BY EXTRACT(HOUR FROM b.startDate)")
    Stream<BookingByHour> countBookingsPerHourRecent(Duration frame);

    /**
     * Returns the number of bookings per month (all time).
     *
     * @return the number of bookings per month
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByMonth(EXTRACT(MONTH FROM b.startDate), COUNT(b)) FROM Booking b WHERE b.openRequests IS EMPTY GROUP BY EXTRACT(MONTH FROM b.startDate)")
    Stream<BookingByMonth> countBookingsPerMonthAllTime();

    /**
     * Returns the number of bookings per month (recent).
     *
     * @param frame the amount of time to look back
     * @return the number of bookings per month
     */
    @Query("SELECT NEW edu.kit.hci.soli.dto.BookingByMonth(EXTRACT(MONTH FROM b.startDate), COUNT(b)) FROM Booking b WHERE CURRENT_TIMESTAMP - b.endDate <= :frame AND b.openRequests IS EMPTY GROUP BY EXTRACT(MONTH FROM b.startDate)")
    Stream<BookingByMonth> countBookingsPerMonthRecent(Duration frame);

    /**
     * Anonymizes bookings older than the specified date.
     *
     * @param date the date to compare the end date to
     * @param anonymousUser the user to anonymize the bookings to
     */
    @Query("UPDATE Booking b SET b.user = :anonymousUser WHERE b.endDate < :date")
    @Modifying
    void anonymizeOlderThan(LocalDateTime date, User anonymousUser);

    /**
     * Deletes all bookings that are outdated and have open requests.
     *
     * @param date the date to compare the start date to
     */
    @Procedure("soli_bookings_cleanup_outdated_requests")
    @Modifying
    void deleteOutdatedRequests(@Param("p_date") LocalDateTime date);

    /**
     * Gets the highest priority of all bookings that overlap with the specified time.
     *
     * @param room the currently selected room
     * @param time the time to check for overlapping bookings
     * @return the highest priority of all bookings that overlap with the specified time
     */
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND b.openRequests IS EMPTY AND b.startDate <= :time AND b.endDate >= :time ORDER BY b.priority ASC, b.shareRoomType DESC")
    Optional<Booking> getHighestPriority(Room room, LocalDateTime time);

    /**
     * Gets the booking of the logged-in user that overlaps with the given time if there is one.
     *
     * @param room the currently selected room
     * @param time the time to check for booking
     * @param currentUser user that is logged in
     * @return the highest priority of all bookings that overlap with the specified time
     */
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND b.openRequests IS EMPTY AND b.startDate <= :time AND b.endDate >= :time AND b.user = :currentUser")
    Optional<Booking> getCurrentBookingOfUser(Room room, LocalDateTime time, User currentUser);

    /**
     * Deletes all bookings for the specified room.
     *
     * @param room the room whose bookings are to be deleted
     */
    void deleteByRoom(Room room);
}
