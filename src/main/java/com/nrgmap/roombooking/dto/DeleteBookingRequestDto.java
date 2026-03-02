package com.nrgmap.roombooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBookingRequestDto {

    @NotBlank(message = "Booker email is required")
    @Email(message = "Booker email must be a valid email address")
    private String bookerEmail;

}
