package com.morning.numapi.service;


import com.morning.numapi.model.Record;
import com.morning.numapi.repository.RecordRepository;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public interface RecordService {
    public <T> Double extractAverage(String username, Function<Record, T> recordResolver);

    List<Record> findAllByUsername(String username);
    List<Record> findAllByUsernameWithParam(String username, String param);

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

    Double extractAverageNumberOfSymbolsInDescription(String username);

//    Double getAverageMark(String username);
//    Double getAverageMoodMark(String username);
}
