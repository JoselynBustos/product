package com.example.products.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DiscountRequestDTO {
    @NotNull
   @Min(value = 1)
    @Max(value = 100)
    private Double percentage;

    public DiscountRequestDTO() {}

    public DiscountRequestDTO(Double percentage) {
        this.percentage = percentage;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}


