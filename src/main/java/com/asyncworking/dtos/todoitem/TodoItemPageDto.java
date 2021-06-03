package com.asyncworking.dtos.todoitem;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemPageDto {

    private Long todoListId;

    private String todoListTitle;

    private Long projectId;

    private String projectName;

    private TodoItemGetDto todoItemGetDto;
}
