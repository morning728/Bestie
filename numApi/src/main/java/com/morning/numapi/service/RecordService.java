package com.morning.numapi.service;


import com.morning.numapi.model.Record;
import com.morning.numapi.repository.RecordRepository;

import java.util.Date;
import java.util.List;

public interface RecordService {
    List<Record> findByUsername(String username);
    List<Record> findByUsernameWithParam(String username, String param);

    Record findById(Long id);
    List<Record> findAll();

    Record addRecord(Record record);
    Record updateRecord(Record record);
    void deleteRecord(Long id);

    List<Record> findByUsernameSortedByDate(String username);
    List<Record> findByUsernameSortedByDateReversed(String username);

    List<Record> findByCreatedBetweenAndUsername(Date created,
                                                 Date created2,
                                                 String username);

    Boolean existsById(Long id);
    Record findLastRecordFromUser(String username);
}
