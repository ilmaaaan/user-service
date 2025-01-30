package habittracker.userservice.mapper;

import habittracker.userservice.dto.habit.HabitDto;
import habittracker.userservice.dto.habit.HabitEntityProjectionDto;
import habittracker.userservice.dto.habit.response.CreationHabitResponse;
import habittracker.userservice.dto.habit.response.ProgressionHabitResponse;
import habittracker.userservice.dto.habit.response.DeleteHabitResponse;
import habittracker.userservice.dto.habit.response.FilterHabitsResponse;
import habittracker.userservice.dto.habit.response.UpdatedHabitResponse;
import habittracker.userservice.dto.habit.response.ReminderHabitResponse;
import habittracker.userservice.dto.habit.response.GetByIdHabitResponse;
import habittracker.userservice.dto.habit.response.GetAllHabitsResponse;
import habittracker.userservice.dto.habit.response.UpdateFullHabitResponse;
import habittracker.userservice.dto.habit.response.HabitResponseStatus;
import org.apache.http.HttpStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

import static habittracker.userservice.utils.HabitIntegrationConstants.CREATION_SUCCESS_HABIT_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.DELETE_HABITS_SUCCESS_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.FILTER_HABITS_SUCCESS_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.GET_ALL_HABITS_SUCCESS_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.GET_BY_ID_SUCCESS_HABIT_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.REMINDER_SUCCESS_HABIT_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.UPDATED_SUCCESS_HABIT_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.PROGRESSION_SUCCESS_HABIT_DESCRIPTION;
import static habittracker.userservice.utils.HabitIntegrationConstants.UPDATE_ALL_HABITS_SUCCESS_DESCRIPTION;

@Mapper(componentModel = "spring", imports = HttpStatus.class)
public interface UserHabitMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "habitCategory", source = "habitCategory")
    HabitDto mapHabitDtoToProjectionDto(HabitEntityProjectionDto dto);

    @Mapping(target = "habitName", source = "name")
    @Mapping(target = "status", expression = "java(toHabitResponseStatus())")
    CreationHabitResponse mapToCreationHabitResponse(HabitDto responseBody);

    default HabitResponseStatus toHabitResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, CREATION_SUCCESS_HABIT_DESCRIPTION);
    }

    @Mapping(target = "progress", source = "habitProgress")
    @Mapping(target = "status", expression = "java(toProgressionResponseStatus())")
    ProgressionHabitResponse mapToProgressionHabitResponse(Double habitProgress);

    default HabitResponseStatus toProgressionResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, PROGRESSION_SUCCESS_HABIT_DESCRIPTION);
    }

    @Mapping(target = "habitName", source = "name")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "status", expression = "java(toUpdatedHabitResponseStatus())")
    UpdatedHabitResponse mapToUpdatedHabitResponse(HabitDto habitDto);

    default HabitResponseStatus toUpdatedHabitResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, UPDATED_SUCCESS_HABIT_DESCRIPTION);
    }

    @Mapping(target = "needsReminder", source = "needsReminder")
    @Mapping(target = "status", expression = "java(toReminderStatusResponseStatus())")
    ReminderHabitResponse mapToReminderHabitResponse(Boolean needsReminder);

    default HabitResponseStatus toReminderStatusResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, REMINDER_SUCCESS_HABIT_DESCRIPTION);
    }

    @Mapping(target = "habit", source = "habitById")
    @Mapping(target = "status", expression = "java(toGetByIdHabitResponseStatus())")
    GetByIdHabitResponse mapToGetByIdHabitResponse(HabitDto habitById);

    default HabitResponseStatus toGetByIdHabitResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, GET_BY_ID_SUCCESS_HABIT_DESCRIPTION);
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "status", expression = "java(toGetAllHabitsResponse())")
    GetAllHabitsResponse mapToGetAllHabitsResponse(HabitDto habitDto);

    List<GetAllHabitsResponse> mapToGetAllHabitsResponseList(List<HabitDto> allHabits);

    default HabitResponseStatus toGetAllHabitsResponse() {
        return new HabitResponseStatus(HttpStatus.SC_OK, GET_ALL_HABITS_SUCCESS_DESCRIPTION);
    }

    @Mapping(target = "habitName", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", expression = "java(toUpdateFullHabitResponseStatus())")
    UpdateFullHabitResponse mapToUpdateFullHabitResponse(HabitDto responseBody);

    default HabitResponseStatus toUpdateFullHabitResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, UPDATE_ALL_HABITS_SUCCESS_DESCRIPTION);
    }

    @Mapping(target = "habitId", source = "habitId")
    @Mapping(target = "status", expression = "java(toDeleteHabitResponseStatus())")
    DeleteHabitResponse mapToDeleteHabitResponse(Long habitId);

    default HabitResponseStatus toDeleteHabitResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_NO_CONTENT, DELETE_HABITS_SUCCESS_DESCRIPTION);
    }

    @Mapping(target = "habits", expression = "java(toHabitDtoList(habits.getContent()))")
    @Mapping(target = "status", expression = "java(toFilterResponseStatus())")
    FilterHabitsResponse mapToFilterHabitsResponse(Page<HabitEntityProjectionDto> habits);

    default List<HabitDto> toHabitDtoList(List<HabitEntityProjectionDto> habitEntities) {
        return habitEntities.stream()
                .map(this::mapHabitDtoToProjectionDto)
                .toList();
    }

    default HabitResponseStatus toFilterResponseStatus() {
        return new HabitResponseStatus(HttpStatus.SC_OK, FILTER_HABITS_SUCCESS_DESCRIPTION);
    }

}
