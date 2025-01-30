package habittracker.userservice.service;

import habittracker.userservice.model.Role;

public interface RoleService {
    Role getRoleByName(String name);
}
