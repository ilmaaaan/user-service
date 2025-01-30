package habittracker.userservice.controller;

import habittracker.userservice.model.Role;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

//контроллер для админов
@Tag(name = "Admin", description = "Операции доступные админам")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final UserActionLogService userActionLogService;

    @Autowired
    public AdminController(UserService userService, UserActionLogService userActionLogService) {
        this.userService = userService;
        this.userActionLogService = userActionLogService;
    }

    @Operation(description = "Добавление роли пользователю",
    parameters = {
            @Parameter(name = "email", description = "Email пользователя, для которого добавляем роль",
                    required = true),
            @Parameter(name = "role", description = "Роль, добавляемая пользователю", required = true),
            @Parameter(name = "principal", description = "Текущий пользователь вносящий изменения, для логирования",
                    required = true)
    },
    responses = {
            @ApiResponse(responseCode = "200", description = "Role assigned to user"),
            @ApiResponse(responseCode = "404", description = "Токен не найден"),
            @ApiResponse(responseCode = "400", description = "Не верные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRoleToUser(@RequestParam String email, @RequestParam Role role,
                                                   Principal principal) {
        userService.assignRoleToUser(email, role);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "ROLE_" + role.getName() + " assigned to user " + email);
        return ResponseEntity.ok("Role assigned to user");
    }

    @Operation(description = "Метод удаления пользователя админом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id, Principal principal) {
        userService.deleteUser(id);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "User " + id + " deleted from users");
        return ResponseEntity.ok("User deleted");
    }

    @Operation(description = "Метод блокировки пользователя админом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/block-user/{id}")
    public ResponseEntity<String> blockUser(@PathVariable long id, Principal principal) {
        userService.banUser(id);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "User " + id + " blocked");
        return ResponseEntity.ok("User blocked");
    }

    @Operation(description = "Метод разблокировки пользователя админом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/unblock-user/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable long id, Principal principal) {
        userService.unbanUser(id);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "User " + id + " unblocked");
        return ResponseEntity.ok("User unblocked");
    }

    @Operation(description = "Метод получения логов пользователя админом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @GetMapping("/logs/{id}")
    public Map<LocalDateTime, String> getLogs(@PathVariable long id, Principal principal) {
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "Reqested logs for User " + id);
        return userService.getUserById(id).getUserActionLog().getActions();
    }
}
