package com.morning.numapi.service.Impl;

import com.morning.numapi.model.DTO.ProfileDTO;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import com.morning.numapi.repository.UserRepository;
import com.morning.numapi.service.RecordService;
import com.morning.numapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RecordService recordService;


    @Override
    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    @Override
    public ProfileDTO convertToProfileDTO(String username) {
        ProfileDTO result = new ProfileDTO();
        User user = findByUsername(username);

        result.setUsername(user.getUsername());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setBirthday(user.getBirthday());
        result.setWeight(recordService
                .findLastRecordFromUser(username)
                .getWeight()
        );
        result.setHeight(recordService.
                findLastRecordFromUser(username)
                .getHeight()
        );
        result.setAverageMark(recordService.extractAverage(username, Record::getMark));
        result.setAverageMoodMark(recordService.extractAverage(username, Record::getMoodMark));
        result.setAverageIncomePerDay(recordService.extractAverage(username, Record::getIncome));
        result.setAverageStepsPerDay(recordService.extractAverage(username, Record::getSteps));
        result.setAverageSheetsPerDay(recordService.extractAverage(username, Record::getSheets));
        result.setAverageSymbolsPerDescription(recordService.extractAverageNumberOfSymbolsInDescription(username));

        return result;
    }

}