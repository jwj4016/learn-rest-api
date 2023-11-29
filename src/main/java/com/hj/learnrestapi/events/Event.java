package com.hj.learnrestapi.events;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
public class Event {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;    //(optional) 이게 없으면 온라인 모임
    private int basePrice;      //(optional)
    private int maxPrice;       //(optional)
    private int limitOfEnrollment;
    private Boolean offline;
    private boolean free;
    private EventStatus eventStatus;
}
