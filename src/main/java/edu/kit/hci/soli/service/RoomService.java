package edu.kit.hci.soli.service;

import edu.kit.hci.soli.domain.Room;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service class for managing {@link Room} entities.
 * Provides methods to change and retrieve room details.
 */
public interface RoomService {
    /**
     * Checks if a room exists by its ID.
     *
     * @param id the ID of the room
     * @return true if the room exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Retrieves a room by its ID.
     *
     * @param id the ID of the room
     * @return the room with the specified ID
     * @throws NoSuchElementException if the room is not found
     */
    Room get(long id);

    /**
     * Retrieves a room by its ID.
     *
     * @param id the ID of the room
     * @return the room with the specified ID or {@link Optional#empty}
     */
    Optional<Room> getOptional(long id);

    /**
     * Retrieves all rooms.
     *
     * @return a list of all rooms
     */
    List<Room> getAll();

    /**
     * Creates a new room.
     *
     * @param room the room to create
     * @return the created room
     */
    Room create(Room room);
}
