package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository") //repo inherits from JPA interface to where our User with its properties is stored
public interface UserRepository extends JpaRepository<User, Long> {
  User findByToken(String token);
  User findByUsername(String username);
  User findByUserId(Long id);
}
