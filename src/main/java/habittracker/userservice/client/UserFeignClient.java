package habittracker.userservice.client;

import habittracker.userservice.dto.habit.HabitDto;
import habittracker.userservice.dto.habit.HabitEntityProjectionDto;
import habittracker.userservice.dto.habit.request.HabitFilterRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


import java.util.List;
//58084
@FeignClient(name = "habit-service", url = "${habit-service.ribbon.listOfServers}")
public interface UserFeignClient {

    @PostMapping
    ResponseEntity<HabitEntityProjectionDto> createHabit(@Valid @RequestBody HabitDto habitDto);

    @GetMapping("/{habitId}/progress")
    ResponseEntity<Double> getHabitProgress(@PathVariable Long habitId);

    @PutMapping("/{habitId}/progress")
    ResponseEntity<HabitDto> updateHabitProgress(@PathVariable Long habitId);

    @GetMapping("/{habitId}/reminder")
    ResponseEntity<Boolean> checkReminder(@PathVariable Long habitId);

    @GetMapping("/{id}")
    ResponseEntity<HabitDto> getHabitById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<List<HabitDto>> getAllHabits();

    @PutMapping("/{id}")
    ResponseEntity<HabitDto> updateHabit(@PathVariable Long id, @Valid @RequestBody HabitDto habitDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteHabits(@PathVariable Long id);

    @GetMapping("/filter")
    ResponseEntity<Page<HabitEntityProjectionDto>> getFilterHabits(
            @SpringQueryMap @ModelAttribute HabitFilterRequestDto habitFilterRequestDto);

}
