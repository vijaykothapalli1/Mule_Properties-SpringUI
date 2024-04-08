package com.prowesssoft.t2m.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prowesssoft.t2m.entity.Role;
import com.prowesssoft.t2m.entity.User;
import com.prowesssoft.t2m.repository.RoleRepository;
import com.prowesssoft.t2m.repository.UsersRepository;
import com.prowesssoft.t2m.res.ApiResponse;

@Service
public class UsersService {
	  @Autowired
	  private UsersRepository userRepository;
	  
	  @Autowired
	  private RoleRepository roleRepository;
	
	public ApiResponse saveUser(User user) {
	        Role role = roleRepository.findByName("ROLE_ADMIN");
	        if(role == null){
	            role = checkRoleExist();
	        }
	        user.setRoles(Arrays.asList(role));
	        userRepository.save(user);
		return new ApiResponse(true, null, null);
	} 
	
	public ApiResponse getUsers(){
		List<User> list = userRepository.findAll();
		return new ApiResponse(true,list, null);
	}
	
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}
