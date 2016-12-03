package text_engine.boundaries;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import text_engine.items.GameEntity;
import text_engine.items.Item;

/**
 * Represents a room.
 */
public class Room extends GameEntity implements Serializable {

    public static final int CONTENT_LIMIT = 10;
  /*
  INVARIANTS:
  - `doors` can hold only one copy of a given door (all Doors in `doors` must be unique)
   */

    private final List<Item> items;
    private final Set<Door> doors;

    /**
     * Base constructor. Constructs a {@link Room}, with an initial set of items and doors.
     *
     * @param name        The name of the room
     * @param description The description of the room
     * @param items       The initial items in the room
     * @param doors       The initial doors for the room
     */
    public Room(@NotNull String name, @NotNull String description,
                @NotNull Collection<Item> items, @NotNull Collection<Door> doors)
            throws IllegalArgumentException {
        super(name, description);

        if (items.size() > CONTENT_LIMIT) {
            throw new IllegalArgumentException(String.format("Rooms can have a maximum of %d items.",
                                                             CONTENT_LIMIT));
        }

        this.items = new ArrayList<>(items);
        this.doors = new HashSet<>(doors);
    }

    /**
     * Constructs a {@link Room}, with an initial set of doors.
     *
     * @param name  The name of the room
     * @param doors The initial doors for the room
     */
    public Room(@NotNull String name, @NotNull String description, Door... doors) {
        this(name, description, new ArrayList<>(), Arrays.asList(doors));
        Collections.addAll(this.doors, doors);
    }

    /**
     * Constructs a {@link Room}, with an initial set of items.
     *
     * @param name  The name of the room
     * @param items The initial items in the room
     * @throws IllegalArgumentException {@code items} is larger than {@value CONTENT_LIMIT}.
     */
    public Room(@NotNull String name, @NotNull String description, Item... items) {
        this(name, description, Arrays.asList(items), new ArrayList<>());
        Collections.addAll(this.items, items);
    }

    /**
     * Constructs a {@link Room}, only a name (no items or doors).
     *
     * @param name the name of the room
     */
    public Room(@NotNull String name, @NotNull String description) {
        this(name, description, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Adds new {@link Door}s as doors to this {@link Room}.
     *
     * @param doors {@link Door}s to be added
     * @return doors which have been successfully added to the room.
     */
    Door[] addDoors(Door... doors) {
        return Arrays.stream(doors).filter(this.doors::add).toArray(Door[]::new);
    }

    public Door[] getDoors() {
        return doors.toArray(new Door[doors.size()]);
    }

    /**
     * @param door the {@link Door} through which to get the next {@link Room}.
     * @return {@link Room} on the other side of the given {@link Door}.
     */
    public Room getRoomThroughDoor(Door door) throws IllegalArgumentException {
        if (doors.contains(door)) {
            return door.getOtherRoom(this);
        }

        throw new IllegalArgumentException(
                String.format("This room (%s) is not connected to the given door, (%s).",
                              toString(), door.toString()));
    }

    /**
     * Adds a new {@link Item} to this room.
     *
     * @param item {@link Item} to be added
     * @throws IllegalArgumentException if the number of new, unique items + current items are more
     *                                  than the {@link #CONTENT_LIMIT}.
     */
    public void addItem(Item item) {
        if (!this.items.contains(item) && this.items.size() == CONTENT_LIMIT) {
            throw new IllegalArgumentException(String.format("Room has hit limit of %d items.",
                                                             CONTENT_LIMIT));
        }

        this.items.add(item);
    }

    /**
     * Examine the room, collecting all available objects into a string.
     *
     * @return A concatenation of all items' descriptions and door locations
     */
    public String examine() {
        String result = "";
        for (Item item : this.items) {
            result += item.toString() + "\n";
        }
        for (Door door : this.doors) {
            result += door.toString() + "\n";
        }
        return result;
    }

    /**
     * Check if free travel is permitted between this {@link Room} and the given {@link Room}.
     *
     * @param other The {@link Room} to check
     * @return Whether the two {@link Room}s are connected
     */
    public boolean canMoveTo(Room other) {
        for (Door door : doors) {
            try {
                Room fetchedOtherRoom = door.getOtherRoom(this);
                if (fetchedOtherRoom.equals(other)) {
                    return true;
                }
            }
            catch (IllegalArgumentException | IllegalStateException ignored) {
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Room room = (Room) obj;
        return getName().equals(room.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
