package edu.kit.hci.soli.dto;

import edu.kit.hci.soli.domain.Booking;
import edu.kit.hci.soli.domain.Room;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class LayoutParams {
    private final LoginStateModel login;
    private final RoomChangeListener onRoomChange;
    private final boolean hasMultipleRooms;
    private @Nullable Room room;
    private @Nullable Booking currentHighestBooking;
    private @Nullable Booking currentBookingOfUser;

    public LayoutParams(@NotNull LoginStateModel login, @Nullable Room room, @NotNull RoomChangeListener onRoomChange, boolean hasMultipleRooms) {
        this.login = Objects.requireNonNull(login);
        this.room = room;
        this.onRoomChange = Objects.requireNonNull(onRoomChange);
        this.hasMultipleRooms = hasMultipleRooms;
        ParamsUpdate paramsUpdate = onRoomChange.onRoomChange(room);
        this.currentHighestBooking = paramsUpdate.currentHighestBooking();
        this.currentBookingOfUser = paramsUpdate.currentBookingOfUser();
    }

    public @NotNull LoginStateModel getLogin() {
        return this.login;
    }

    public @Nullable Room getRoom() {
        return this.room;
    }

    public void setRoom(@Nullable Room room) {
        ParamsUpdate paramsUpdate = onRoomChange.onRoomChange(room);
        this.room = room;
        this.currentHighestBooking = paramsUpdate.currentHighestBooking();
        this.currentBookingOfUser = paramsUpdate.currentBookingOfUser();
    }

    public @Nullable Booking getCurrentHighestBooking() {
        return currentHighestBooking;
    }

    public @Nullable Booking getCurrentBookingOfUser() {
        return currentBookingOfUser;
    }

    public boolean isMultipleRooms() {
        return hasMultipleRooms;
    }

    /**
     * Functional interface for listening to room changes on a {@link LayoutParams}
     * and modifying relevant fields as needed.
     */
    @FunctionalInterface
    public interface RoomChangeListener {
        /**
         * Invoked initially when constructing {@link LayoutParams} and whenever the current room changes.
         * Use this to recompute necessary components by returning the appropriate {@link ParamsUpdate}.
         *
         * @param room the new room
         * @return the new current highest booking
         */
        @NotNull ParamsUpdate onRoomChange(@Nullable Room room);

    }

    /**
     * Encapsulates the component of {@link LayoutParams} that have to be updated when the current room changes.
     *
     * @param currentHighestBooking the booking, which has the highest priority at the current time if there is one
     * @param currentBookingOfUser the booking of the logged-in user at the current time if there is one
     */
    public record ParamsUpdate(
            @Nullable Booking currentHighestBooking,
            @Nullable Booking currentBookingOfUser
    ) {}
}
