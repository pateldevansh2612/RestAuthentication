package com.security.demo;

import com.security.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
    User findByPhoneNumber(String phoneNumber);
}
