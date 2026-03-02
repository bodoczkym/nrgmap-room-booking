package com.nrgmap.roombooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {

    private Long id;
    private String name;
    private int capacity;
    private int floor;
    private boolean hasProjector;
    private boolean hasWhiteboard;

}
