package com.hj.learnrestapi.events;

import com.hj.learnrestapi.common.BaseControllerTest;
import com.hj.learnrestapi.common.TestDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {
//    @MockBean
    @Autowired
    EventRepository eventRepository;

    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
//    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    @Test
    @WithMockUser
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 10, 4, 12, 29))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 10, 5, 12, 29))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("당산동")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))

                //링크관련 검증은 docs 쪽에서 테스트를 하기 때문에 주석처리한다.
                //.andExpect(jsonPath("_links.self").exists())
                //.andExpect(jsonPath("_links.profile").exists())
                //.andExpect(jsonPath("_links.query-events").exists())
                //.andExpect(jsonPath("_links.update-event").exists())

                //REST DOCS 관련
                .andDo(document("create-event"
                        , links(
                                linkWithRel("self").description("link to self")
                                , linkWithRel("query-events").description("link to query events")
                                , linkWithRel("update-event").description("link to update an exist")
                                , linkWithRel("profile").description("link to profile")
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        )
                        , requestFields(
                                fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("base price of new event")
                                , fieldWithPath("maxPrice").description("max price of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        )
                        , responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        )
                        //응답의 일부분만 들어 있을 경우 ResponseFields 대신 relaxedResponseFields 사용 가능.
                        //장점 : 문서 일부분만 테스트 가능.
                        //단점 : 정확한 문서를 생성하지 못한다.
                        , responseFields(
                                fieldWithPath("id").description("identifier of new event")
                                , fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("base price of new event")
                                , fieldWithPath("maxPrice").description("max price of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                                , fieldWithPath("free").description("it tells if this event is free or not")
                                , fieldWithPath("offline").description("it tells if this event is offline event or not")
                                , fieldWithPath("eventStatus").description("event status")
                                , fieldWithPath("manager").description("event status")

                                , fieldWithPath("_links.self.href").description("link to self")
                                , fieldWithPath("_links.query-events.href").description("link to query event list")
                                , fieldWithPath("_links.update-event.href").description("link to update existing event")
                                , fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;

    }


    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100L)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 12, 4, 12, 29))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 12, 5, 12, 29))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("당산동")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

//        event.setId(10l);
//        Mockito.when(eventRepository.save(event)).thenReturn(event);
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON
                        )
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("id").exists())
//                        .andExpect(header().exists(HttpHeaders.LOCATION))
//                        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
//                .andExpect(jsonPath("id").value(Matchers.not(100)))
//                .andExpect(jsonPath("free").value(Matchers.not(true)));
    }



    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 12, 4, 12, 29))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 12, 3, 12, 29))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("당산동")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
//                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists());
                .andExpect(jsonPath("_links.index").exists())
        ;
    }


    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기.")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent );

        // When
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.self").exists())

                //REST DOCS 관련
                .andDo(document("query-events"
                        , links(
                                linkWithRel("self").description("link to self")
                                , linkWithRel("first").description("link to first page")
                                , linkWithRel("prev").description("link to prev page")
                                , linkWithRel("next").description("link to next page")
                                , linkWithRel("last").description("link to last page")
                                , linkWithRel("profile").description("link to profile")
                        )
                        , queryParameters(
                                parameterWithName("page").description("page")
                                , parameterWithName("size").description("size")
                                , parameterWithName("sort").description("sort")
                        )

                        , responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        )
                        //응답의 일부분만 들어 있을 경우 ResponseFields 대신 relaxedResponseFields 사용 가능.
                        //장점 : 문서 일부분만 테스트 가능.
                        //단점 : 정확한 문서를 생성하지 못한다.
                        , responseFields(
                                fieldWithPath("_embedded.eventResourceList[].id").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].name").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].description").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].beginEnrollmentDateTime").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].closeEnrollmentDateTime").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].beginEventDateTime").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].endEventDateTime").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].location").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].basePrice").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].maxPrice").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].limitOfEnrollment").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].offline").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].free").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].eventStatus").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[].manager").description("identifier of new event")
                                , fieldWithPath("_embedded.eventResourceList[]._links.self.href").description("identifier of new event")


                                , fieldWithPath("_links.first.href").description("link to first")
                                , fieldWithPath("_links.prev.href").description("link to prev page")
                                , fieldWithPath("_links.self.href").description("link to self page")
                                , fieldWithPath("_links.next.href").description("link to next page")
                                , fieldWithPath("_links.last.href").description("link to last page")
                                , fieldWithPath("_links.profile.href").description("link to profile")

                                , fieldWithPath("page.size").description("size of this page")
                                , fieldWithPath("page.totalElements").description("count of elements")
                                , fieldWithPath("page.totalPages").description("total pages")
                                , fieldWithPath("page.number").description("number of this page")

                        )
                ))

        ;

    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = generateEvent(100);

        //When
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())

                .andDo(document("resources-events-update"))
        ;

        //Then
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        //When & Then
        mockMvc.perform(get("/api/events/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정한다.")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(1234);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        //When
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andDo(document("update-event"))
        ;


        //Then
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 수정 실패")
    public void updateEvent404_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(1234);
        EventDto eventDto = EventDto.builder().build();

        //When
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    @Test
    @DisplayName("입력값이 잘못된 경우에 수정 실패")
    public void updateEvent404_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(1234);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //When
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        //Given
        Event event = this.generateEvent(1234);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When
        this.mockMvc.perform(put("/api/events/{id}", 12345678)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 10, 4, 12, 29))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 10, 5, 12, 29))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("당산동")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return this.eventRepository.save(event);
    }
}
