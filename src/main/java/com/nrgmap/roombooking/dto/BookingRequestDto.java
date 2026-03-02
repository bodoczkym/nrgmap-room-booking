package com.nrgmap.roombooking.dto;

import java.time.LocalDateTime;

import com.nrgmap.roombooking.dto.validation.ValidTimeRange;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeRange
public class BookingRequestDto {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private String bookerName;

    @NotBlank(message = "Booker email is required")
    @Email(message = "Booker email must be a valid email address")
    private String bookerEmail;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

}
