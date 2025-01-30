package habittracker.userservice.dto.habit.request;

import habittracker.userservice.dto.habit.HabitCategoryDto;
import lombok.Data;

import java.time.LocalDate;

//это DTO = HabitFilterRequest из habit-service
@Data
public class HabitFilterRequestDto {
    private HabitCategoryDto category;
    private Double minProgress;
    private Double maxProgress;
    private LocalDate startDate;
    private LocalDate endDate;
    private static final String SORT_FIELD = "startDate";
    private static final boolean ASCENDING = true;
    private static final int PAGE = 0;
    private static final int SIZE = 10;
}
