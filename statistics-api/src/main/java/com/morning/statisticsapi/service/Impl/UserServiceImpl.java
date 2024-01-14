package com.morning.statisticsapi.service.Impl;


import com.morning.statisticsapi.model.DTO.ProfileDTO;
import com.morning.statisticsapi.model.Record;
import com.morning.statisticsapi.model.User;
import com.morning.statisticsapi.repository.UserRepository;
import com.morning.statisticsapi.service.interf.RecordService;
import com.morning.statisticsapi.service.interf.UserService;
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
        com.morning.statisticsapi.model.User result = userRepository.findByUsername(username);
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
        try {
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
        } catch (Exception e){
            log.info(e.getMessage());
        }
        return result;
    }

}