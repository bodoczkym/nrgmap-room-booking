package com.nrgmap.roombooking.controller;

import java.util.List;

import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.dto.BookingResponseDto;
import com.nrgmap.roombooking.dto.DeleteBookingRequestDto;
import com.nrgmap.roombooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto dto) {
        BookingResponseDto created = bookingService.createBooking(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookings(
            @RequestParam(required = false) Long roomId) {
        if (roomId == null) {
            return ResponseEntity.ok(bookingService.getAllBookings());
        }
        return ResponseEntity.ok(bookingService.getBookingsByRoomId(roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long id,
            @Valid @RequestBody DeleteBookingRequestDto dto) {
        bookingService.deleteBooking(id, dto.getBookerEmail());
        return ResponseEntity.noContent().build();
    }

}
