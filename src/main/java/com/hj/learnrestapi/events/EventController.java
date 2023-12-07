package com.hj.learnrestapi.events;

import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;


    //dto가 validation을 통과하지 못하면, Errors에 에러 내용이 담긴다.
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);

        //링크 만들기
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class)
                .slash(newEvent.getId());
        URI createdUri = selfLinkBuilder
                .toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event")); //put 요청. 어떤 메소드를 사용해야하는지 설정할 수는 없음.

        return ResponseEntity.created(createdUri).body(eventResource);  //objectmapper가 컨버팅. bean serializer를 사용해서 변환.
    }

//    //dto가 validation을 통과하지 못하면, Errors에 에러 내용이 담긴다.
//    @PostMapping
//    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
//        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        eventValidator.validate(eventDto, errors);
//        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        Event event = modelMapper.map(eventDto, Event.class);
//        event.update();
//        Event newEvent = eventRepository.save(event);
//
//        //링크 만들기
//        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class)
//                .slash(newEvent.getId());
//        URI createdUri = selfLinkBuilder
//                .toUri();
//        EventResource eventResource = new EventResource(event);
//        eventResource.add(linkTo(EventController.class).withRel("query-events"));
//        eventResource.add(selfLinkBuilder.withSelfRel());
//        eventResource.add(selfLinkBuilder.withRel("update-event")); //put 요청. 어떤 메소드를 사용해야하는지 설정할 수는 없음.
//
//        return ResponseEntity.created(createdUri).body(eventResource);  //objectmapper가 컨버팅. bean serializer를 사용해서 변환.
//    }

}
