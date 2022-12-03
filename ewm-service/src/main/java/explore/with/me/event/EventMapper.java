package explore.with.me.event;

import explore.with.me.State;
import explore.with.me.category.CategoryMapper;
import explore.with.me.category.model.Category;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.event.dto.EventUpdateDto;
import explore.with.me.event.dto.NewEventDto;
import explore.with.me.event.location.Location;
import explore.with.me.event.model.Event;
import explore.with.me.user.UserMapper;
import explore.with.me.user.model.User;

import java.time.LocalDateTime;


public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                0,
                event.getEventDate(),
                UserMapper.toUserDtoShort(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0,
                event.getCreatedOn(),
                event.getDescription(),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState()


        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                0,
                event.getEventDate(),
                UserMapper.toUserDtoShort(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0

        );
    }

    public static Event toEvent(NewEventDto newEventDto, User user, Category category) {
        return new Event(
                null,
                newEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                user,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration(),
                State.PENDING,
                newEventDto.getTitle()
        );
    }

    public static Event toEventUpdate(EventUpdateDto eventUpdateDto, User user, Category category) {
        return new Event(
                eventUpdateDto.getEventId(),
                eventUpdateDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                eventUpdateDto.getDescription(),
                eventUpdateDto.getEventDate(),
                user,
                null,
                null,
                eventUpdateDto.getPaid(),
                eventUpdateDto.getParticipantLimit(),
                null,
                false,
                State.PENDING,
                eventUpdateDto.getTitle()
        );
    }
}


