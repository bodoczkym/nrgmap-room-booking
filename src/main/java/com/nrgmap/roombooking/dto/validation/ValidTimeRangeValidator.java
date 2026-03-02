package com.nrgmap.roombooking.dto.validation;

import com.nrgmap.roombooking.dto.BookingRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, BookingRequestDto> {

    @Override
    public boolean isValid(BookingRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return true; // @NotNull handles null checks separately
        }
        return dto.getEndTime().isAfter(dto.getStartTime());
    }

}
