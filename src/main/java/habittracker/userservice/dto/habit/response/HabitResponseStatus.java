package habittracker.userservice.dto.habit.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HabitResponseStatus {

    private int code;
    private String description;

}
