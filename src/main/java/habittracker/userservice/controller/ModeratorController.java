package habittracker.userservice.controller;

import habittracker.userservice.model.User;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

//контроллер для модераторов
@Tag(name = "Moderator", description = "Операции доступные модераторам")
@RestController
@RequestMapping("/moderator")
public class ModeratorController {
    private final UserService userService;
    private final UserActionLogService userActionLogService;

    public ModeratorController(UserService userService, UserActionLogService userActionLogService) {
        this.userService = userService;
        this.userActionLogService = userActionLogService;
    }

    @Operation(description = "Метод просмотра всех пользователей модератором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users != null ? users : List.of();
    }

    @Operation(description = "Метод блокировки пользователя модератором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/users/block/{id}")
    public ResponseEntity<String> blockUser(@PathVariable long id, Principal principal) {
        userService.banUser(id);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "User " + id + " blocked");
        return ResponseEntity.ok("User blocked");
    }

    @Operation(description = "Метод разблокировки пользователя модератором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/users/unblock/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable Long id, Principal principal) {
        userService.unbanUser(id);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "User " + id + " blocked");
        return ResponseEntity.ok("User blocked");
    }
}
