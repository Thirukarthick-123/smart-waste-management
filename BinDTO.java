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

    @NotNull(message = "lat is required")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double lat;

    @NotNull(message = "lng is required")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double lng;

    @NotNull(message = "status is required")
    private BinStatus status;
}
