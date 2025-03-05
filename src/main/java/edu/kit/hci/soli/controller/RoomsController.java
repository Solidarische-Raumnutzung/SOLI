package edu.kit.hci.soli.controller;

import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.dto.KnownError;
import edu.kit.hci.soli.dto.LayoutParams;
import edu.kit.hci.soli.dto.form.EditOrCreateRoomForm;
import edu.kit.hci.soli.service.RoomService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller class for viewing and managing rooms.
 */
@Controller("/")
@Slf4j
public class RoomsController {
    private final RoomService roomService;

    /**
     * Constructs a new RoomsController instance.
     *
     * @param roomService the room service to be used by this controller
     */
    public RoomsController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Shows the room list in edit mode.
     *
     * @param model the model to which attributes are added
     * @param layout the layout parameters
     * @return the name of the view to be rendered
     */
    @GetMapping("/admin/rooms")
    public String adminRoomList(Model model, @ModelAttribute("layout") LayoutParams layout) {
        List<Room> rooms = roomService.getAll();
        model.addAttribute("rooms", rooms);
        model.addAttribute("edit", true);
        return "admin/rooms/list";
    }

    /**
     * Shows the form for creating a new room.
     *
     * @param model the model to which attributes are added
     * @param layout the layout parameters
     * @return the name of the view to be rendered
     */
    @GetMapping("/admin/rooms/new")
    public String newRoom(Model model, @ModelAttribute("layout") LayoutParams layout) {
        model.addAttribute("id", null);
        model.addAttribute("name", "");
        model.addAttribute("description", "");
        model.addAttribute("location", "");
        return "admin/rooms/edit";
    }

    /**
     * Shows the form for editing a room.
     *
     * @param model the model to which attributes are added
     * @param response the HTTP response
     * @param roomId the ID of the room to be edited
     * @return the name of the view to be rendered
     */
    @GetMapping("/admin/rooms/{roomId:\\d+}")
    public String editRoom(Model model, HttpServletResponse response,
                           @PathVariable long roomId) {
        Optional<Room> room = roomService.getOptional(roomId);
        if (room.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("error", KnownError.NOT_FOUND);
            return "error/known";
        }
        model.addAttribute("id", roomId);
        model.addAttribute("name", room.get().getName());
        model.addAttribute("description", room.get().getDescription());
        model.addAttribute("location", room.get().getLocation());
        return "admin/rooms/edit";
    }

    /**
     * Shows the deletion dialog for a room.
     *
     * @param model the model to which attributes are added
     * @param response the HTTP response
     * @param roomId the ID of the room to be deleted
     * @return the name of the view to be rendered
     */
    @GetMapping("/admin/rooms/{roomId:\\d+}/delete")
    public String showDeletionDialog(Model model, HttpServletResponse response,
                                     @PathVariable long roomId) {
        Optional<Room> room = roomService.getOptional(roomId);
        if (room.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("error", KnownError.NOT_FOUND);
            return "error/known";
        }
        model.addAttribute("room", room.get());
        return "admin/confirmation/delete_room_page";
    }

    /**
     * Deletes a room.
     *
     * @param model the model to which attributes are added
     * @param response the HTTP response
     * @param roomId the ID of the room to be deleted
     * @return the name of the view to be rendered
     */
    @DeleteMapping("/admin/rooms/{roomId:\\d+}")
    public String deleteRoom(Model model, HttpServletResponse response,
                             @PathVariable long roomId,
                             @ModelAttribute("layout") LayoutParams layout) {
        Optional<Room> room = roomService.getOptional(roomId);
        if (room.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("error", KnownError.NOT_FOUND);
            return "error/known";
        }

        if (layout.getRoom() != null && layout.getRoom().getId() == roomId) {
            layout.setRoom(null);
        }

        roomService.delete(room.get());
        return "redirect:/admin/rooms";
    }

    /**
     * Handles applying changes to a room from modals created by this controller.
     *
     * @param model the model to which attributes are added
     * @param response the HTTP response
     * @param formData the form data for the room
     * @return the name of the view to be rendered
     */
    @PostMapping(value = "/admin/rooms", consumes = "application/x-www-form-urlencoded")
    public String editOrCreateRoom(Model model, HttpServletResponse response, @ModelAttribute EditOrCreateRoomForm formData) {
        String name = Objects.requireNonNullElse(formData.getName(), "New Room");
        String description = Objects.requireNonNullElse(formData.getDescription(), "");
        String location = Objects.requireNonNullElse(formData.getLocation(), "Unknown Location");
        Room room;
        if (formData.getTarget() != null) {
            // Some target was picked, edit it
            Optional<Room> optionalRoom = roomService.getOptional(formData.getTarget());
            if (optionalRoom.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                model.addAttribute("error", KnownError.NOT_FOUND);
                return "error/known";
            }
            room = optionalRoom.get();
            room.setName(name);
            room.setDescription(description);
            room.setLocation(location);
        } else {
            // No target was picked, create a new room
            room = new Room(null, name, description, location);
        }
        roomService.save(room);
        return "redirect:/admin/rooms";
    }
}