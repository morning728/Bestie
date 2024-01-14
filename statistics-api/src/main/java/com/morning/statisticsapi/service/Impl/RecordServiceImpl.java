package com.morning.statisticsapi.service.Impl;


import com.morning.statisticsapi.exception.RecordNotFoundException;
import com.morning.statisticsapi.model.Record;
import com.morning.statisticsapi.repository.RecordRepository;
import com.morning.statisticsapi.service.interf.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;


    public  <T> Double extractAverage(String username, Function<Record, T> recordResolver) {
        List<Record> records = findAllByUsername(username);
        return records
                .stream()
                .mapToDouble(record -> (double) (int)recordResolver.apply(record))
                .average()
                .orElse(Double.NaN);
    }

    public  <T> Double extractAverage(String username, List<Record> records, Function<Record, T> recordResolver) {
        return records
                .stream()
                .mapToDouble(record -> (double) (int)recordResolver.apply(record))
                .average()
                .orElse(Double.NaN);
    }

    @Override
    public Double extractAverageNumberOfSymbolsInDescription(String username) {
        List<Record> records = findAllByUsername(username);
        return records
                .stream()
                .mapToDouble(record -> record.getDescription().length())
                .average()
                .orElse(Double.NaN);
    }

    @Override
    public List<Record> findAllByUsername(String username) {
        List<Record> records = recordRepository.findAllByUsername(username);
        if(records.isEmpty()){
            log.info("No one record was found from username: " + username);
            return null;
        }
        log.info(String.format("All records (%s) from user %s were found", String.valueOf(records.size()), username));
        return records;
    }

    @Override
    public List<Record> findAllByUsernameWithParam(String username, String param) {
        List<Record> records = recordRepository.findAllByUsername(username);
        records = records.stream().filter(record ->
                    record.getDescription().contains(param) ||
                    record.getCreated().toString().contains(param))
                .toList();
        if(records.isEmpty()){
            log.info("No one record was found from username: " + username + " with parameter " + param);
            return null;
        }
        log.info(String.format("All records (%s)" + " with parameter " + param +
                " from user %s were found", String.valueOf(records.size()), username));
        return records;
    }

    @Override
    public Record findById(Long id) {
        return recordRepository.findById(id).orElse(null);
    }

    @Override
    public List<Record> findAll() {
        return recordRepository.findAll();
    }

    @Override
    public Record addRecord(Record record) {
        Record result = null;
        try{
            result = recordRepository.save(record);
        } catch (Exception e) {
            log.error("The record was not added due to the error: " + e.toString());
            return null;
        }
        log.info(String.format("%s's record was successfully added!", record.getUsername()));
        return result;
    }

    @Override
    public Record updateRecord(Record record) {
        Record result = null;
        Record toUpdate = recordRepository.findById(record.getId()).orElse(null);
        if(toUpdate == null){
            log.error(String.format("The record with id = %s was not found", record.getId()));
            return null;
        }
        toUpdate.setUsername(record.getUsername());
        toUpdate.setDescription(record.getDescription());
        toUpdate.setWeight(record.getWeight());
        toUpdate.setMark(record.getMark());
        toUpdate.setSheets(record.getSheets());
        toUpdate.setIncome(record.getIncome());
        toUpdate.setHeight(record.getHeight());
        toUpdate.setMoodMark(record.getMoodMark());
        toUpdate.setSteps(record.getSteps());
        toUpdate.setUpdated(new Date());
        try{
            result = recordRepository.save(toUpdate);
        } catch (Exception e) {
            log.error("The record was not updated due to the error: " + e.toString());
            return null;
        }
        log.info(String.format("%s's record with id = %s was successfully updated!",
                record.getUsername(), record.getId()));
        return result;
    }

    @Override
    public void deleteRecord(Long id) {
        Record toDelete = recordRepository.findById(id).orElse(null);
        if(toDelete == null){
            log.error(String.format("The record with id = %s was not found", id));
            return;
        }
        try{
            recordRepository.delete(toDelete);
        } catch (Exception e) {
            log.error("The record with id = " + id
                    + " was not deleted due to the error: "
                    + e.toString());
            return;
        }
        log.info(String.format("Record with id = %s was successfully deleted!", id));
    }

    @Override
    public List<Record> findByUsernameSortedByDate(String username) {
        return recordRepository.findAllByUsername(username)
                .stream()
                .sorted((o1, o2) -> {return o1.getCreated().compareTo(o2.getCreated());})
                .collect(Collectors.toList());
    }

    @Override
    public List<Record> findByUsernameSortedByDateReversed(String username) {
        return recordRepository.findAllByUsername(username)
                .stream()
                .sorted((o1, o2) -> {return o1.getCreated().compareTo(o2.getCreated()) > 0 ? -1 : 1;})
                .collect(Collectors.toList());
    }

    @Override
    public List<Record> findByCreatedBetweenAndUsername(Date firstDate,
                                                        Date secondDate,
                                                        String username) {
        List<Record> records;
        if(firstDate.before(secondDate)) {
            records = recordRepository
                    .findByCreatedBetweenAndUsername(firstDate, secondDate, username);
        }
        else {
            records = recordRepository
                    .findByCreatedBetweenAndUsername(secondDate, firstDate, username);
        }
        if(records.isEmpty()){
            log.info(String.format("No one record was found from %s and between dates: %s and %s: ",
                    username,
                    firstDate,
                    secondDate));
            return records;
        }
        log.info(String.format("All records (%s) from user %s and between dates: %s and %s were found",
                String.valueOf(records.size()),
                username, firstDate, secondDate));
        return records;
    }

    @Override
    public Boolean existsById(Long id) {
        return recordRepository.existsById(id);
    }

    @Override
    public Record findLastRecordFromUser(String username) {
        Optional<Record> record = recordRepository.findLastRecordFromUser(username);
        if(record.isEmpty()){
            log.info("The last record was not found from user " + username);
            throw new RecordNotFoundException("The last record was not found from user " + username);
        }
        log.info("The last record was found from user " + username);
        return record.get();
    }


//    @Override
//    public Double getAverageMark(String username) {
//        List<Record> records = findAllByUsername(username);
//        return records
//                .stream()
//                .mapToDouble(record -> Double.valueOf(record.getMark()))
//                .average()
//                .orElse(Double.NaN);
//    }
//
//    @Override
//    public Double getAverageMoodMark(String username) {
//        List<Record> records = findAllByUsername(username);
//        return records
//                .stream()
//                .mapToDouble(record -> Double.valueOf(record.getMoodMark()))
//                .average()
//                .orElse(Double.NaN);
//    }


}
