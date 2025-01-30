package habittracker.userservice.controller;

import habittracker.userservice.service.PasswordResetService;
import habittracker.userservice.service.RoleService;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//контроллер для неавторизованных пользователей
@Tag(name = "NonAutorized", description = "Операции доступные неавторизованным пользователям")
@RestController
@RequestMapping("/auth")
public class NonAutorizedController {
    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private final UserActionLogService userActionLogService;
    private final RoleService roleService;

    @Autowired
    public NonAutorizedController(PasswordResetService passwordResetService,
                                  UserService userService,
                                  UserActionLogService userActionLogService, RoleService roleService) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.userActionLogService = userActionLogService;
        this.roleService = roleService;
    }

    @Operation(description = "Метод восстановления пароля для всех пользователей",
            parameters = {
                    @Parameter(name = "email", description = "Email для сброса пароля", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok("Forgot Password");
    }

    @Operation(description = "Метод сброса пароля для всех пользователей",
            parameters = {
                    @Parameter(name = "token", description = "Токен для сброса пароля", required = true),
                    @Parameter(name = "newPassword", description = "Новый пароль пользователя", required = true)
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Reset Password");
    }

    @Operation(description = "Метод добавления пользователя всех пользователей",
    parameters = {
            @Parameter(name = "user", description = "Модель пользователя для добавления", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/new-user")
    public ResponseEntity<String> newUser(
            @RequestParam String userName,
            @RequestParam String password,
            @RequestParam String email) {
        userService.addUser(userName, password, email);
        userActionLogService.logAction(userService.getUserByName(userName).getId(),
                "User was created with id: " + userService.getUserByName(userName).getId());
        userService.assignRoleToUser(email, roleService.getRoleByName("USER"));
        userActionLogService.logAction(userService.getUserByName(userName).getId(),
                userName + "assigned role: USER");
        return ResponseEntity.ok("New user added");
    }
}
