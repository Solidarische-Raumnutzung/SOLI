package edu.kit.hci.soli.service.impl;

import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.repository.RoomRepository;
import edu.kit.hci.soli.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    /**
     * Constructs a RoomService with the specified {@link RoomRepository}.
     *
     * @param roomRepository the repository for managing Room entities
     */
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
    public void delete(Room room) {
        roomRepository.delete(room);
    }
}
