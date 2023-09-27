package com.morning.numapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordDTO {
    private Long id;
    private String username;
    private String description;
    private Integer mark;
    private Float weight;
    private Float height;
    private Integer moodMark;
    private Integer steps;
    private Integer sheets;
    private Integer income;

    public Record toRecord(){
        Record result = new Record();

        result.setId(id == null ? null : id);  //FIX
        result.setUsername(username);
        result.setDescription(description);
        result.setMark(mark == null ? 0 : mark);
        result.setWeight(weight == null ? 0 : weight);
        result.setHeight(height == null ? 0 : height);
        result.setMoodMark(moodMark == null ? 0 : moodMark);
        result.setSteps(steps == null ? 0 : steps);
        result.setSheets(sheets == null ? 0 : sheets);
        result.setIncome(income == null ? 0 : income);
        result.setCreated(new Date());
        result.setUpdated(new Date());
        result.setStatus(Status.ACTIVE);

        return result;
    }

    public Record toRecord(Long id){
        this.setId(id);
        return this.toRecord();
    }

    public RecordDTO fromRecord(Record record){
        RecordDTO dto = new RecordDTO();

        dto.setId(record.getId());
        dto.setUsername(record.getUsername());
        dto.setDescription(record.getDescription());
        dto.setMark(record.getMark());
        dto.setWeight(record.getWeight());
        dto.setHeight(record.getHeight());
        dto.setMoodMark(record.getMoodMark());
        dto.setSteps(record.getSteps());
        dto.setSheets(record.getSheets());
        dto.setIncome(record.getIncome());

        return dto;
    }
}
