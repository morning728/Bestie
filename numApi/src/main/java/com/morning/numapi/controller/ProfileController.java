package com.morning.numapi.controller;

import com.morning.numapi.model.DTO.ProfileDTO;
import com.morning.numapi.model.Record;
import com.morning.numapi.model.User;
import com.morning.numapi.service.JwtService;
import com.morning.numapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        String username = jwtService.extractUsername(
                token.substring(7)
        );
        return new ResponseEntity<>(userService.convertToProfileDTO(username), HttpStatusCode.valueOf(200));
    }

/*    @PutMapping("")
    public ResponseEntity updateRecord(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                       @PathVariable(name = "id") Long id,
                                       @RequestBody RecordDTO dto
    ) {
        dto.setUsername(jwtService.extractUsername(token.substring(7)));
        if(recordService.existsById(id)){
            return ResponseEntity.ok(recordService.updateRecord(dto.toRecord(id)));
        }
        throw new RecordNotFoundException(id);
    }*/
}

