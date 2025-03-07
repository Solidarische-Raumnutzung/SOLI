package edu.kit.hci.soli.service;

import edu.kit.hci.soli.domain.Room;

import java.util.List;
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
     * Retrieves the number of rooms.
     *
     * @return the number of rooms
     */
    long count();

    /**
     * Creates a new room.
     *
     * @param room the room to create
     * @return the created room
     */
    Room save(Room room);

    /**
     * Deletes a room.
     *
     * @param room the room to delete
     */
    void delete(Room room);
}
