package habittracker.userservice.dto.habit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

//это DTO = Habit (entity) из habit-service
@Data
public class HabitEntityProjectionDto {
    private Long id;

    @NotBlank(message = "The name field cannot be empty")
    @Pattern(regexp = "^[A-Z0-9][a-z0-9].{3,50}$", message = "Name cannot be longer than 50 characters")
    private String name;

    @Pattern(regexp = "^[A-Z0-9][a-z0-9].{3,500}$", message = "Description cannot be longer than 500 characters")
    private String description;

    @NotNull(message = "Start date cannot be null")
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;

    @NotNull(message = "Status date cannot be null")
    private HabitStatusDto status;

    @NotNull(message = "Progress date cannot be null")
    private double progress;

    @NotNull(message = "Habit category date cannot be null")
    private HabitCategoryDto habitCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public HabitCategoryDto getHabitCategory() {
        return habitCategory;
    }

    public void setHabitCategory(HabitCategoryDto habitCategory) {
        this.habitCategory = habitCategory;
    }

    public HabitStatusDto getStatus() {
        return status;
    }

    public void setStatus(HabitStatusDto status) {
        this.status = status;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
