package com.nrgmap.roombooking.mapper;

import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.dto.BookingResponseDto;
import com.nrgmap.roombooking.model.Booking;
import com.nrgmap.roombooking.model.Room;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponseDto toDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setRoom(RoomMapper.toDto(booking.getRoom()));
        dto.setBookerName(booking.getBookerName());
        dto.setBookerEmail(booking.getBookerEmail());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    public static Booking toEntity(BookingRequestDto dto, Room room) {
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setBookerName(dto.getBookerName());
        booking.setBookerEmail(dto.getBookerEmail());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        return booking;
    }

}
