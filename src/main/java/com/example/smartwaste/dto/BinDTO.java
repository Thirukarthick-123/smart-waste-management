package com.example.smartwaste.dto;

import com.example.smartwaste.model.BinStatus;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinDTO {

    private Long id; // optional – used for updates only

    @NotBlank(message = "binId is required")
    private String binId;

    @NotNull(message = "fill is required")
    @Min(0)
    @Max(100)
    private Integer fill;

    private Double lat;

    private Double lng;

    private BinStatus status;
}
