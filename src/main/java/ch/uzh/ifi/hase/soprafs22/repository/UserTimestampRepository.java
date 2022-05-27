package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.UserTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("userTimestampRepository") //repo inherits from JPA interface to where our UserTimestamp with its properties is stored
public interface UserTimestampRepository extends JpaRepository<UserTimestamp, Long> {
    UserTimestamp findByUserId(Long id);
}
