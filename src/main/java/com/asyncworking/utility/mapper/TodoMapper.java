package com.asyncworking.utility.mapper;


import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {

//    TodoListDto fromEntity(TodoList todoList);

    TodoItem toEntity(TodoItemPostDto todoItemPostDto);

    @Mapping(source = "id", target = "todoItemId")
    TodoItemGetDto fromEntity(TodoItem todoItem);
}
