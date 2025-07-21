package com.amp.rotatoy.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemsDto {
    
private String name;

  private String category;

  private String status;

  private String notes;

  private LocalDate lastRotated;
    
  private String image;

}
