package edu.kit.hci.soli.service.impl;

import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.repository.BookingsRepository;
import edu.kit.hci.soli.repository.RoomRepository;
import edu.kit.hci.soli.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final BookingsRepository bookingsRepository;

    /**
     * Constructs a RoomService with the specified {@link RoomRepository}.
     *
     * @param roomRepository the repository for managing Room entities
     */
    public RoomServiceImpl(RoomRepository roomRepository, BookingsRepository bookingsRepository) {
        this.roomRepository = roomRepository;
        this.bookingsRepository = bookingsRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return roomRepository.existsById(id);
    }

    @Override
    public Optional<Room> getOptional(long id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    @Override
    public long count() {
        return roomRepository.count();
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void delete(Room room) {
        bookingsRepository.deleteByRoom(room);
        roomRepository.delete(room);
    }
}
