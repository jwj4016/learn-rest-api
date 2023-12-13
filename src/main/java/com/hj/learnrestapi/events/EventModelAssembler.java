package com.hj.learnrestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.Errors;

public class EventModelAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {
    @Override
    public EntityModel<Event> toModel(Event event) {
        //Unconditional links to single-item resource and aggregate root
//        EntityModel<Event> eventModel = EntityModel.of(event
//                , WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EventController.class).createEvent(event, new Errors())));
        return null;
    }
}
