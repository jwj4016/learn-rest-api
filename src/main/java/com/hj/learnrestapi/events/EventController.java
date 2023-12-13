package com.hj.learnrestapi.events;

import com.hj.learnrestapi.common.ErrorsResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
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
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event")); //put 요청. 어떤 메소드를 사용해야하는지 설정할 수는 없음.
        eventResource.add(Link.of("/docs/index.html#resources-events-created").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);  //objectmapper가 컨버팅. bean serializer를 사용해서 변환.
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedEntityModel = assembler.toModel(page, new RepresentationModelAssembler<Event, RepresentationModel<?>>() {
            @Override
            public RepresentationModel<?> toModel(Event entity) {
                return new EventResource(entity);
            }
        });
        pagedEntityModel.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedEntityModel);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);

    }

    @PutMapping("/{id}")
    public ResponseEntity modifyEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @PathVariable Long id) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();

        modelMapper.map(eventDto, event);
        event.update();
        //Using DynamicUpdate? Using Converter?
        eventRepository.save(event);

        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok().body(eventResource);
    }


    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

//    @GetMapping("/{id}")
//    public Class<?> getEvent(@PathVariable Long id) {
//
//        return null;
//    }

//    //dto가 validation을 통과하지 못하면, Errors에 에러 내용이 담긴다.
//    @PostMapping
//    public ResponseEntity temp(@RequestBody @Valid EventDto eventDto, Errors errors) {
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
//        ResponseEntity.created(linkTo(methodOn(EventController.class).getEvent(newEvent.getId())).withRel("query-events").toUri())
//                .body();
//
//        return ResponseEntity.created(createdUri).body(eventResource);  //objectmapper가 컨버팅. bean serializer를 사용해서 변환.
//    }

}
