package com.amp.rotatoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amp.rotatoy.dto.ApiResponse;
import com.amp.rotatoy.dto.UserDto;


@RestController
public class MockLoginController{
    

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> userLogin(@RequestBody UserDto userDto){
        System.out.println("Login request ::"+ userDto.getEmail() + " password "+ userDto.getPassword());
        ApiResponse<String> apiResponse= new ApiResponse<>();
        if(userDto.getEmail().equalsIgnoreCase("user@rt.com") && userDto.getPassword().equalsIgnoreCase("user")){
            apiResponse.setMessage("login success!");
            apiResponse.setSuccess(true);
            return ResponseEntity.ok(apiResponse);
        }
        apiResponse.setMessage("login failed!");
        apiResponse.setSuccess(false);
        return ResponseEntity.status(401).body(apiResponse);
    }
}