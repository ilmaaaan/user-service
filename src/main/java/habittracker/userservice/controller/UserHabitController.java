package habittracker.userservice.controller;

import habittracker.userservice.dto.habit.HabitDto;
import habittracker.userservice.dto.habit.HabitEntityProjectionDto;
import habittracker.userservice.dto.habit.request.HabitFilterRequestDto;
import habittracker.userservice.dto.habit.response.CreationHabitResponse;
import habittracker.userservice.dto.habit.response.ProgressionHabitResponse;
import habittracker.userservice.dto.habit.response.DeleteHabitResponse;
import habittracker.userservice.dto.habit.response.FilterHabitsResponse;
import habittracker.userservice.dto.habit.response.UpdatedHabitResponse;
import habittracker.userservice.dto.habit.response.ReminderHabitResponse;
import habittracker.userservice.dto.habit.response.GetByIdHabitResponse;
import habittracker.userservice.dto.habit.response.GetAllHabitsResponse;
import habittracker.userservice.dto.habit.response.UpdateFullHabitResponse;

import habittracker.userservice.mapper.UserHabitMapper;
import habittracker.userservice.service.HabitIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;



import java.util.List;

@RestController
@RequestMapping("api/habit-integration")
public class UserHabitController {

    private final HabitIntegrationService service;

    private final UserHabitMapper mapper;

    public UserHabitController(HabitIntegrationService service, UserHabitMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping()
    @Operation(summary = "Create a new habit", description = "Creates a new habit based on the provided HabitDto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Habit created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HabitDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreationHabitResponse> createHabit(@Valid @RequestBody HabitDto habitDto) {
        HabitEntityProjectionDto createdHabit = service.createHabit(habitDto);
        HabitDto responseBody = mapper.mapHabitDtoToProjectionDto(createdHabit);
        return new ResponseEntity<>(mapper.mapToCreationHabitResponse(responseBody), HttpStatus.CREATED);
    }

    @GetMapping("/{habitId}/progress")
    @Operation(summary = "Get habit progress", description = "Retrieves the progress of a habit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit progress retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "integer"))),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<ProgressionHabitResponse> getHabitProgress(@PathVariable Long habitId) {
        Double habitProgress = service.getHabitProgress(habitId);
        return new ResponseEntity<>(mapper.mapToProgressionHabitResponse(habitProgress), HttpStatus.OK);
    }

    @PutMapping("/{habitId}/progress")
    @Operation(summary = "Update habit progress", description = "Updates the progress of a habit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit progress updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HabitDto.class))),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<UpdatedHabitResponse> updateHabitProgress(@PathVariable Long habitId) {
        HabitDto habitDto = service.updateHabitProgress(habitId);
        return new ResponseEntity<>(mapper.mapToUpdatedHabitResponse(habitDto), HttpStatus.OK);
    }

    @GetMapping("/{habitId}/reminder")
    @Operation(summary = "Check if reminder is needed",
            description = "Checks if a reminder is needed for a habit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder status retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<ReminderHabitResponse> checkReminder(@PathVariable Long habitId) {
        Boolean needsReminder = service.checkReminder(habitId);
        return new ResponseEntity<>(mapper.mapToReminderHabitResponse(needsReminder), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get habit by ID", description = "Retrieves a habit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Habit retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HabitDto.class))),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<GetByIdHabitResponse> getHabitById(@PathVariable Long id) {
        HabitDto habitById = service.getHabitById(id);
        return new ResponseEntity<>(mapper.mapToGetByIdHabitResponse(habitById), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all habits", description = "Retrieves a list of all habits.")
    @ApiResponse(responseCode = "200", description = "List of habits retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = HabitDto.class, type = "array")))
    public ResponseEntity<List<GetAllHabitsResponse>> getAllHabits() {
        List<HabitDto> allHabits = service.getAllHabits();
        List<GetAllHabitsResponse> response = mapper.mapToGetAllHabitsResponseList(allHabits);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update habit", description = "Updates an existing habit based on the provided HabitDto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HabitDto.class))),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<UpdateFullHabitResponse> updateHabit(
            @PathVariable Long id, @Valid @RequestBody HabitDto habitDto) {
        HabitDto responseBody = service.updateHabit(id, habitDto);
        return new ResponseEntity<>(mapper.mapToUpdateFullHabitResponse(responseBody), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete habit", description = "Deletes a habit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Habit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    public ResponseEntity<DeleteHabitResponse> deleteHabit(@PathVariable Long id) {
        service.deleteHabits(id);
        return new ResponseEntity<>(mapper.mapToDeleteHabitResponse(id), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter habits",
            description = "Retrieves a list of habits based on provided filters and sorting options.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of habits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HabitDto.class, type = "array"))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
            @ApiResponse(responseCode = "404", description = "No habits found matching the criteria")
    })
    public ResponseEntity<FilterHabitsResponse> getFilterHabits(@ModelAttribute HabitFilterRequestDto filterRequest) {
        Page<HabitEntityProjectionDto> habits = service.getFilterHabits(filterRequest);
        return new ResponseEntity<>(mapper.mapToFilterHabitsResponse(habits), HttpStatus.OK);
    }
}
