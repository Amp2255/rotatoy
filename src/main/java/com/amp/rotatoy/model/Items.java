package com.amp.rotatoy.model;



import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {

@Id private String id;

  private String name;

  private String category;

  private String status;

  private String notes;

  private LocalDate lastRotated;

  private String image;

}
