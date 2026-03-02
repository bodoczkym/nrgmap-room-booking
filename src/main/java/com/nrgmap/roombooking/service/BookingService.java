package com.nrgmap.roombooking.service;

import java.util.List;

import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.dto.BookingResponseDto;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto dto);

    List<BookingResponseDto> getAllBookings();

    List<BookingResponseDto> getBookingsByRoomId(Long roomId);

    void deleteBooking(Long id, String bookerEmail);

}
