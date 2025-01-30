package habittracker.userservice.service.impl;

import habittracker.userservice.model.Role;
import habittracker.userservice.repository.RoleRepository;
import habittracker.userservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByName(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            throw new UsernameNotFoundException("Role not found");
        }
        return role;
    }
}
