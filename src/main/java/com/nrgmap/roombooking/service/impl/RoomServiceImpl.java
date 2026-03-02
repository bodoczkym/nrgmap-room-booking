package com.nrgmap.roombooking.service.impl;

import java.util.List;

import com.nrgmap.roombooking.dto.RoomResponseDto;
import com.nrgmap.roombooking.mapper.RoomMapper;
import com.nrgmap.roombooking.repository.RoomRepository;
import com.nrgmap.roombooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<RoomResponseDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomMapper::toDto)
                .toList();
    }

}
