package com.nrgmap.roombooking.mapper;

import com.nrgmap.roombooking.dto.RoomResponseDto;
import com.nrgmap.roombooking.model.Room;

public class RoomMapper {

    private RoomMapper() {
    }

    public static RoomResponseDto toDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setFloor(room.getFloor());
        dto.setHasProjector(room.isHasProjector());
        dto.setHasWhiteboard(room.isHasWhiteboard());
        return dto;
    }

}
