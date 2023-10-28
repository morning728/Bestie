package com.morning.numapi.repository;

import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUsername(String name);
    List<Record> findByCreatedBetweenAndUsername(Date created,
                                                 Date created2,
                                                 String username);

    @Query(
            value = "SELECT * " +
                    "FROM num_note.record " +
                    "WHERE created = (SELECT MAX(created) FROM num_note.record where username = :username) AND username = :username",
            nativeQuery = true)
    Optional<Record> findLastRecordFromUser(String username);
}
