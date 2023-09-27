package com.morning.numapi.service.Impl;

import com.morning.numapi.model.Goal;
import com.morning.numapi.model.Record;
import com.morning.numapi.repository.RecordRepository;
import com.morning.numapi.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;


    @Override
    public List<Record> findByUsername(String username) {
        List<Record> records = recordRepository.findByUsername(username);
        if(records.isEmpty()){
            log.info("No one record was found from username: " + username);
            return null;
        }
        log.info(String.format("All records (%s) from user %s were found", String.valueOf(records.size()), username));
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
        return recordRepository.findByUsername(username)
                .stream()
                .sorted((o1, o2) -> {return o1.getCreated().compareTo(o2.getCreated());})
                .collect(Collectors.toList());
    }

    @Override
    public List<Record> findByUsernameSortedByDateReversed(String username) {
        return recordRepository.findByUsername(username)
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
            return null;
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
}
