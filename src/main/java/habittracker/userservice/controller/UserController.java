package habittracker.userservice.controller;

import habittracker.userservice.model.User;
import habittracker.userservice.service.UserActionLogService;
import habittracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

//контроллер для авторизованных пользователей
@Tag(name = "Autorized", description = "Операции доступные авторизованным пользователям")
@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;
    private final UserActionLogService userActionLogService;

    @Autowired
    public UserController(UserService userService,
                          UserActionLogService userActionLogService) {
        this.userService = userService;
        this.userActionLogService = userActionLogService;
    }

    @Operation(description = "Метод загрузки аватара для авторизованных пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PostMapping("/avatar/{username}")
    public ResponseEntity<String> uploadAvatar(@PathVariable String username,
                                               @RequestParam("file") MultipartFile file,
                                               Principal principal) throws IOException {
        userService.uploadAvatar(username, file);
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "avatar uploaded successfully");
        return ResponseEntity.ok("avatar uploaded");
    }

    @Operation(description = "Метод обновления профиля для авторизованных пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление пользователя"),
            @ApiResponse(responseCode = "401", description = "Не достаточно прав для удаления пользователя"),
            @ApiResponse(responseCode = "500", description = "Непредвиденная ошибка сервера")
    })
    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestParam String name,
                                                @RequestParam String address, Principal principal) {
        User user = userService.getUserByName(principal.getName());
        userService.updateUser(name, address, user.getId());
        userActionLogService.logAction(userService.getUserByName(principal.getName()).getId(),
                "profile updated successfully");
        return ResponseEntity.ok("User updated successfully");
    }
}
