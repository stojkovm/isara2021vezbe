package rs.ac.uns.ftn.informatika.spring.security.service;

import java.util.List;

import rs.ac.uns.ftn.informatika.spring.security.model.Role;

public interface RoleService {
	Role findById(Long id);
	List<Role> findByName(String name);
}
