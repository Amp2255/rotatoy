package com.amp.rotatoy.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class RotateActions {
    
    public String updateItemStatus(String status){
        if(status.equals("In Rotation")){
            return "Stored";
        }
        if(status.equals("Stored")){
             return "In Rotation";
        }
        return status;
    }

    public LocalDate updateLastRotatedDate(){
        return LocalDate.now();
    }
}
