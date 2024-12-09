package com.morning.taskapimain.service.feign;

import com.morning.taskapimain.entity.dto.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "security")
public interface ProfileServiceClient {

    @GetMapping("/security/v1/users/info")
    ProfileDTO getProfileByUsername(
            @RequestParam("username") String username,
            @RequestHeader("Authorization") String bearerToken
    );
}