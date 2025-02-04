package edu.kit.hci.soli.domain;

import jakarta.persistence.*;
import lombok.ToString;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The datamodel for a room as it is stored in the database.
 */
@Entity
@Table(name = "soli_rooms")
@ToString
public class Room {
    /**
     * The unique identifier for the room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The opening hours of the room, mapped by day of the week.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "soli_room_opening_hours",
            joinColumns = {@JoinColumn(name = "room_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "day_of_week")
    private Map<DayOfWeek, TimeTuple> openingHours;

    /**
     * The name of the room.
     */
    private String name;

    /**
     * The description of the room.
     */
    private String description;

    /**
     * The location of the room.
     */
    private String location;

    /**
     * Constructs a new room with the specified details.
     *
     * @param id          the unique identifier for the room
     * @param name        the name of the room
     * @param description the description of the room
     * @param location    the location of the room
     */
    public Room(Long id, String name, String description, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    /**
     * Default constructor for JPA.
     */
    public Room() {
    }

    /**
     * Gets the unique identifier for the room.
     *
     * @return the unique identifier for the room
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Gets the name of the room.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the room.
     *
     * @return the description of the room
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the description for the location of the room.
     *
     * @return the location description
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the opening hours of the room.
     * <p>
     * The resulting value is a copy of the map with missing values for week days added
     *
     * @return the opening hours of the room
     */
    public Map<DayOfWeek, TimeTuple> getOpeningHours() {
        Map<DayOfWeek, TimeTuple> result = openingHours == null ? new HashMap<>() : new HashMap<>(openingHours);
        for (DayOfWeek value : DayOfWeek.values()) {
            if (value == DayOfWeek.SATURDAY || value == DayOfWeek.SUNDAY) continue;
            result.putIfAbsent(value, new TimeTuple());
        }
        return result;
    }

    /**
     * Sets the unique identifier for the room.
     *
     * @param id the unique identifier for the room
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the name of the room.
     *
     * @param name the name of the room
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description for the room.
     *
     * @param description the description of the room
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the description for the location of the room.
     *
     * @param location the new location for the room
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the opening hours of the room.
     *
     * @param openingHours the opening hours of the room
     */
    public void setOpeningHours(Map<DayOfWeek, TimeTuple> openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (getId() == null) {
            return false;
        }
        return o instanceof Room r && Objects.equals(getId(), r.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
