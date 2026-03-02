package com.nrgmap.roombooking.service.impl;

import java.util.List;

import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.dto.BookingResponseDto;
import com.nrgmap.roombooking.exception.BookingConflictException;
import com.nrgmap.roombooking.exception.BookingNotFoundException;
import com.nrgmap.roombooking.exception.RoomNotFoundException;
import com.nrgmap.roombooking.exception.UnauthorizedDeletionException;
import com.nrgmap.roombooking.mapper.BookingMapper;
import com.nrgmap.roombooking.model.Booking;
import com.nrgmap.roombooking.model.Room;
import com.nrgmap.roombooking.repository.BookingRepository;
import com.nrgmap.roombooking.repository.RoomRepository;
import com.nrgmap.roombooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto dto) {
        Room room = roomRepository.findByIdWithLock(dto.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(dto.getRoomId()));

        boolean hasConflict = bookingRepository.existsOverlappingBooking(
                room.getId(), dto.getStartTime(), dto.getEndTime());

        if (hasConflict) {
            throw new BookingConflictException(room.getId());
        }

        Booking booking = BookingMapper.toEntity(dto, room);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByRoomId(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }

        return bookingRepository.findByRoomId(roomId)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBooking(Long id, String bookerEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!booking.getBookerEmail().equalsIgnoreCase(bookerEmail)) {
            throw new UnauthorizedDeletionException();
        }

        bookingRepository.delete(booking);
    }

}
