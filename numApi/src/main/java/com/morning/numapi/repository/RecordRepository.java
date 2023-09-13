package com.morning.numapi.repository;

import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUsername(String name);
    List<Record> findByCreatedBetween(Date created,
                                                 Date created2);
}
