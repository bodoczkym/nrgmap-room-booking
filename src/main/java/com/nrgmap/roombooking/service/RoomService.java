package com.nrgmap.roombooking.service;

import java.util.List;

import com.nrgmap.roombooking.dto.RoomResponseDto;

public interface RoomService {

    List<RoomResponseDto> getAllRooms();

}
