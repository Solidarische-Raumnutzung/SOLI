package edu.kit.hci.soli.controller;

import edu.kit.hci.soli.config.SoliConfiguration;
import edu.kit.hci.soli.domain.Room;
import edu.kit.hci.soli.dto.KnownError;
import edu.kit.hci.soli.dto.LayoutParams;
import edu.kit.hci.soli.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.PATH;

/**
 * Main controller for miscellaneous status requests.
 */
@Controller("/misc")
@Slf4j
public class MainController extends AbstractErrorController {
    private final SoliConfiguration soliConfiguration;
    private final RoomService roomService;

    /**
     * Constructs a MainController with the specified {@link DefaultErrorAttributes}.
     *
     * @param errorAttributes   the default error attributes
     * @param soliConfiguration the configuration of the application
     * @param roomService       the service for managing rooms
     */
    public MainController(DefaultErrorAttributes errorAttributes, SoliConfiguration soliConfiguration, RoomService roomService) {
        super(errorAttributes);
        this.soliConfiguration = soliConfiguration;
        this.roomService = roomService;
    }

    /**
     * Handles errors and returns the appropriate error view.
     *
     * @param model   the model to be used in the view
     * @param request the HTTP request
     * @return the view name
     */
    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.of(
                STATUS,
                ERROR,
                MESSAGE,
                PATH
        ));
        if (errorAttributes.get("status").equals(404)) {
            model.addAttribute("error", KnownError.NOT_FOUND);
            return "error/known";
        }

        model.addAttribute("timestamp", errorAttributes.get("timestamp"));
        model.addAttribute("status", errorAttributes.get("status"));
        model.addAttribute("error", errorAttributes.get("error"));
        model.addAttribute("message", errorAttributes.get("message"));
        model.addAttribute("path", errorAttributes.get("path"));
        return "error/unknown";
    }

    /**
     * Returns the view for disabled users.
     *
     * @param model the model to be used in the view
     * @return the view name
     */
    @RequestMapping("/disabled")
    public String getDisabled(Model model) {
        return "error/disabled_user";
    }

    /**
     * Returns the security.txt file content.
     *
     * @param response the Http response
     * @return the security.txt content
     */
    @GetMapping("/.well-known/security.txt")
    @ResponseBody
    public String securityTxt(HttpServletResponse response) {
        response.setContentType("text/plain");
        return """
                Contact: soli@iar.kit.edu
                Expires: 2099-12-31T23:00:00.000Z
                Preferred-Languages: de, en
                Canonical: %s.well-known/security.txt
                """.formatted(soliConfiguration.getHostname());
    }

    /**
     * Handles GET requests to the root endpoint.
     * If there is only one room, redirect to that room.
     *
     * @param model the model to which attributes are added
     * @param layout the layout parameters
     * @return the name of the view to be rendered
     */
    @GetMapping("/")
    public String index(Model model, @ModelAttribute("layout") LayoutParams layout) {
        List<Room> rooms = roomService.getAll();
        if (rooms.size() == 1) {
            return "redirect:/" + rooms.getFirst().getId();
        }
        layout.setRoom(null);
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomsFirst", true);
        return "index";
    }

    /**
     * Returns the view for the publisher information (impressum).
     *
     * @param model the model to be used in the view
     * @return the view name
     */
    @RequestMapping("/publisher")
    public String getPublisher(Model model) {
        List<Room> rooms = roomService.getAll();
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomsFirst", false);
        return "index";
    }
}
