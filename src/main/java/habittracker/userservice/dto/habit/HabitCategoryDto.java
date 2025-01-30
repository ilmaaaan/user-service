package habittracker.userservice.dto.habit;

import lombok.Data;

import java.util.List;

// это DTO = HabitCategory (entity) из  habit-service
@Data
public class HabitCategoryDto {

    private Long id; // Первичный ключ

    private String name; // Название категории

    private String description; // Описание

    private List<HabitDto> habits; // Список привычек, относящихся к категории

}
