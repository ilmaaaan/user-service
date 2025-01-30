package habittracker.userservice.service;

import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService extends UserDetailsService {
    void addUser(String username, String password, String email);

    void updateUser(String username, String adress, Long id);

    void banUser(Long id);

    void unbanUser(Long id);

    User getUserById(long id);

    User getUserByName(String name);

    List<User> getAllUsers();

    void deleteUser(long id);

    void assignRoleToUser(String email, Role role);

    void uploadAvatar(String username, MultipartFile file) throws IOException;
}
