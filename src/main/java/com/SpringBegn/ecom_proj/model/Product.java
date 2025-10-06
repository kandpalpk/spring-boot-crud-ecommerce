package com.SpringBegn.ecom_proj.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @JsonProperty("description")
    private String desc;

    private String brand;
    private BigDecimal price;
    private String category;

    /*@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")*/
    private LocalDate releaseDate;
    private boolean productAvailable;

    @JsonProperty("stockQuantity")
    private int quantity;

    private String imageName;
    private String imageType;

    @Lob
    private byte[] imageData;

}
