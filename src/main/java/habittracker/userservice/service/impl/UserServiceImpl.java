package habittracker.userservice.service.impl;

import habittracker.userservice.audit.UserActionLog;
import habittracker.userservice.model.Role;
import habittracker.userservice.model.User;
import habittracker.userservice.repository.RoleRepository;
import habittracker.userservice.repository.UserActionLogRepository;
import habittracker.userservice.repository.UserRepository;
import habittracker.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;
    private final RoleRepository roleRepository;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    private UserActionLogRepository userActionLogRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           UserActionLogRepository userActionLogRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userActionLogRepository = userActionLogRepository;
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE + username);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public void addUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        userRepository.save(user);

        UserActionLog userActionLog = new UserActionLog();
        userActionLog.setUser(user); // Теперь user имеет id
        user.setUserActionLog(userActionLog);

        userActionLogRepository.save(userActionLog);
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void updateUser(String username, String address, Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setName(username);
        user.setAddress(address);
        userRepository.save(user);
    }

    @Override
    public void banUser(Long id) {
        Optional<User> userBan = userRepository.findById(id);
        if (userBan.isPresent()) {
            userBan.get().setAccountNonLocked(false);
        } else {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        userRepository.save(userBan.get());
    }

    @Override
    public void unbanUser(Long id) {
        Optional<User> userBan = userRepository.findById(id);
        if (userBan.isPresent()) {
            userBan.get().setAccountNonLocked(true);
        } else {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        userRepository.save(userBan.get());
    }

    @Override
    public User getUserById(long id) {
        Optional<User> user = userRepository.findByIdWithRoles(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void assignRoleToUser(String email, Role role) {
        Role existingRole = roleRepository.findByName(role.getName());
        if (existingRole == null) {
            existingRole = roleRepository.save(role);
        }

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.getRoles().add(existingRole);
        userRepository.save(user);
    }

    @Override
    public void uploadAvatar(String username, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        Path filepath = Paths.get("uploads", filename);
        file.transferTo(filepath);

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build(), filepath);

        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        user.setAvatarUrl("s3://your-bucket-name/" + filename); // Пример хранения URL
        userRepository.save(user);
    }
}
