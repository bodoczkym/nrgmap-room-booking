package com.nrgmap.roombooking;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrgmap.roombooking.dto.BookingRequestDto;
import com.nrgmap.roombooking.dto.DeleteBookingRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequestDto createValidBooking(Long roomId, int daysFromNow) {
        LocalDateTime start = LocalDateTime.now().plusDays(daysFromNow).withHour(14).withMinute(0).withSecond(0).withNano(0);
        return new BookingRequestDto(roomId, "Mike B", "mike@test.com", start, start.plusHours(1));
    }

    @Test
    @DisplayName("Create booking -> 201 with correct response body")
    void createBooking_shouldReturn201() throws Exception {
        BookingRequestDto dto = createValidBooking(1L, 20);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.room.id").value(1))
                .andExpect(jsonPath("$.bookerName").value("Mike B"))
                .andExpect(jsonPath("$.bookerEmail").value("mike@test.com"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Get all bookings -> 200")
    void getAllBookings_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get bookings by valid roomId -> 200")
    void getBookingsByRoomId_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/bookings").param("roomId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get bookings by invalid roomId -> 404")
    void getBookingsByInvalidRoomId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/bookings").param("roomId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete booking with correct email -> 204")
    void deleteBooking_withCorrectEmail_shouldReturn204() throws Exception {
        BookingRequestDto booking = createValidBooking(3L, 30);
        MvcResult result = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andReturn();

        long bookingId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        DeleteBookingRequestDto deleteDto = new DeleteBookingRequestDto("mike@test.com");

        mockMvc.perform(delete("/api/bookings/" + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete booking with wrong email -> 403")
    void deleteBooking_withWrongEmail_shouldReturn403() throws Exception {
        BookingRequestDto booking = createValidBooking(4L, 31);
        MvcResult result = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andReturn();

        long bookingId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        DeleteBookingRequestDto deleteDto = new DeleteBookingRequestDto("wrong@test.com");

        mockMvc.perform(delete("/api/bookings/" + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete non-existent booking -> 404")
    void deleteBooking_notFound_shouldReturn404() throws Exception {
        DeleteBookingRequestDto deleteDto = new DeleteBookingRequestDto("mike@test.com");

        mockMvc.perform(delete("/api/bookings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create booking with missing fields -> 400 with field errors")
    void createBooking_missingFields_shouldReturn400() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors.roomId").exists())
                .andExpect(jsonPath("$.fieldErrors.bookerEmail").exists())
                .andExpect(jsonPath("$.fieldErrors.startTime").exists())
                .andExpect(jsonPath("$.fieldErrors.endTime").exists());
    }

    @Test
    @DisplayName("Create booking with end before start -> 400")
    void createBooking_endBeforeStart_shouldReturn400() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(25).withHour(14).withMinute(0).withSecond(0).withNano(0);
        BookingRequestDto dto = new BookingRequestDto(1L, "Mike B", "mike@test.com", start, start.minusHours(1));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}
