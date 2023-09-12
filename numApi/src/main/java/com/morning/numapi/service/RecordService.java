package com.morning.numapi.service;


import com.morning.numapi.model.Record;
import com.morning.numapi.repository.RecordRepository;

import java.util.Date;
import java.util.List;

public interface RecordService {
    List<Record> findByUsername(String username);

    Record findById(Long id);
    List<Record> findAll();

    Record addRecord(Record record);
    Record updateRecord(Record record);
    void deleteRecord(Long id);

    List<Record> findByUsernameSortedByDate(String username);
    List<Record> findByUsernameSortedByDateReversed(String username);
}
