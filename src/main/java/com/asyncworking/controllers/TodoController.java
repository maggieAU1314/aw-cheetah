package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.services.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/projects/{projectId}")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todolists")
    public ResponseEntity<Long> createTodoList(@Valid @RequestBody TodoListDto todoListDto) {
        return ResponseEntity.ok(todoService.createTodoList(todoListDto));
    }

    @GetMapping("/todolists")
    public ResponseEntity<List<TodoListDto>> fetchTodoLists(@PathVariable Long projectId,
                                                                     @RequestParam("quantity") @NotNull Integer quantity) {
        return ResponseEntity.ok(todoService.findRequiredNumberTodoListsByProjectId(projectId, quantity));
    }

    @GetMapping("/todolists/{todolistId}")
    public ResponseEntity<TodoListDto> fetchSingleTodoList(@PathVariable Long todolistId) {
        log.info("todolistId:" + todolistId);
        return ResponseEntity.ok(todoService.fetchSingleTodoList(todolistId));
    }


    @PostMapping("/todolists/{todolistId}/todoitems")
    public ResponseEntity createTodoItem(@Valid @RequestBody TodoItemPostDto todoItemPostDto) {
        todoService.createTodoItem(todoItemPostDto);
        return ResponseEntity.ok("create todo item success");
    }

    @GetMapping("/todoitems/{todoitemId}")
    public ResponseEntity<TodoItemPageDto> getTodoItemPageInfo(@PathVariable Long todoitemId) {
        log.info("todoitemId:" + todoitemId);
        return ResponseEntity.ok(todoService.fetchTodoItemPageInfoByIds(todoitemId));
    }
}
