package com.nrgmap.roombooking;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingConflictIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    private static final Long ROOM_ID = 1L;
    private static final Long OTHER_ROOM_ID = 2L;
    private LocalDateTime baseStart;
    private LocalDateTime baseEnd;

    @BeforeEach
    void setUp() throws Exception {
        bookingRepository.deleteAll();

        baseStart = LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0);
        baseEnd = baseStart.plusHours(2);

        BookingRequestDto baseBooking = new BookingRequestDto(
                ROOM_ID, "Test User", "test@test.com", baseStart, baseEnd);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(baseBooking)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Exact same time range in same room -> 409 Conflict")
    void exactSameTimeRange_shouldConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com", baseStart, baseEnd);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("New booking starts during existing -> 409 Conflict")
    void newStartsDuringExisting_shouldConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseStart.plusMinutes(30), baseEnd.plusHours(1));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("New booking ends during existing -> 409 Conflict")
    void newEndsDuringExisting_shouldConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseStart.minusHours(1), baseStart.plusMinutes(30));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("New booking fully wraps existing -> 409 Conflict")
    void newWrapsExisting_shouldConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseStart.minusHours(1), baseEnd.plusHours(1));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Existing booking fully wraps new -> 409 Conflict")
    void existingWrapsNew_shouldConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseStart.plusMinutes(15), baseEnd.minusMinutes(15));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Adjacent bookings (one ends when other starts) -> 201 OK")
    void adjacentBookings_shouldNotConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseEnd, baseEnd.plusHours(2));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Same time range but different room -> 201 OK")
    void sameTimeDifferentRoom_shouldNotConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                OTHER_ROOM_ID, "Other User", "other@test.com",
                baseStart, baseEnd);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("No overlap at all -> 201 OK")
    void noOverlap_shouldNotConflict() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(
                ROOM_ID, "Other User", "other@test.com",
                baseEnd.plusHours(1), baseEnd.plusHours(3));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

}
