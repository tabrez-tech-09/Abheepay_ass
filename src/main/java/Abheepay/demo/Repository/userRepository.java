package Abheepay.demo.Repository;

import java.util.Optional;

import Abheepay.demo.dto.user;

public interface userRepository {
    Optional<user> findById(String id);
    void save(user user);
    

}
