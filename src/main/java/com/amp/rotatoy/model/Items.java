package com.amp.rotatoy.model;



import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "items")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Items {

@Id @GeneratedValue private String id;

  private String name;

  private String category;

  // @Enumerated(EnumType.String)
  // private Status status;
  private String status;

  private String notes;

  private LocalDate lastRotated;

  private String image;

}
