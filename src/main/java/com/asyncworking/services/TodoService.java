package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoItemNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoListRepository todoListRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    private final TodoMapper todoMapper;

    @Transactional
    public Long createTodoList(TodoListDto todoListDto) {
        TodoList newTodoList = todoMapper.toTodoListEntity(todoListDto,
                findProjectById(todoListDto.getProjectId()));
        log.info("create a new TodoList: " + newTodoList.getTodoListTitle());
        todoListRepository.save(newTodoList);
        return newTodoList.getId();
    }

    public List<TodoListDto> findRequiredNumberTodoListsByCompanyIdAndProjectId(Long companyId, Long projectId, Integer quantity) {
        return todoListRepository.findTodolistWithTodoItems(companyId, projectId, PageRequest.of(0, quantity)).stream()
                .map(todoList -> todoMapper.fromTodoListEntity(todoList, todoMapper.todoItemsToTodoItemGetDtos(todoList.getTodoItems())))
                .collect(Collectors.toList());
    }

    public TodoListDto fetchSingleTodoList(Long companyId, Long projectId, Long id) {
        return todoMapper.fromTodoListEntity(findTodoListByCompanyIdAndProjectIdAndId(companyId, projectId, id),
                findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc
                (companyId, projectId, id));
    }

    @Transactional
    public Long createTodoItem(Long companyId, Long projectId, @Valid TodoItemPostDto todoItemPostDto) {
        TodoItem savedTodoItem = todoItemRepository.save(
                todoMapper.toTodoItemEntity(todoItemPostDto, findTodoListByCompanyIdAndProjectIdAndId
                        (companyId, projectId, todoItemPostDto.getTodolistId())));
        log.info("created a item with id " + savedTodoItem.getId());
        return savedTodoItem.getId();
    }

    @Transactional
    public Boolean changeTodoItemCompleted(Long companyId, Long projectId, Long id) {
        TodoItem todoItem = findTodoItemByCompanyIdAndProjectIdAndId(companyId, projectId, id);
        log.info("todoItem origin completed status: " + todoItem.getCompleted());
        todoItem.setCompleted(!todoItem.getCompleted());
        todoItemRepository.save(todoItem);
        return todoItem.getCompleted();
    }

    public List<TodoItemGetDto> findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(Long companyId,
                                                                                               Long projectId, Long todoListId) {
        return todoItemRepository.findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(companyId, projectId, todoListId)
                .stream()
                .map(todoMapper::fromTodoItemEntity)
                .collect(Collectors.toList());
    }


    public TodoItemPageDto fetchTodoItemPageInfoByIds(Long companyId, Long projectId, Long todoItemId) {
        TodoItem todoItem = findTodoItemByCompanyIdAndProjectIdAndId(companyId, projectId, todoItemId);
        return todoMapper.fromTodoItemToTodoItemPageDto(todoItem,
                findProjectById(todoItem.getProjectId()),
                userService.findUserById(todoItem.getCreatedUserId()));
    }

    @Transactional
    public void updateTodoItemDetails(Long companyId, Long projectId, Long todoItemId, TodoItemPutDto todoItemPutDto) {
        int res = todoItemRepository.updateTodoItem(todoItemId,
                todoItemPutDto.getDescription(),
                todoItemPutDto.getNotes(),
                todoItemPutDto.getOriginNotes(),
                todoItemPutDto.getDueDate(),
                companyId, projectId);
        if (res != 1) {
            throw new TodoItemNotFoundException("There is no todoItem id is " + todoItemId);
        }
    }

    private Project findProjectById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Cannot find project by id:" + projectId));
    }

    private TodoList findTodoListByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long todoListId) {
        return todoListRepository.findByCompanyIdAndProjectIdAndId(companyId, projectId, todoListId)
                .orElseThrow(() -> new TodoListNotFoundException("Cannot find todoList by id: " + todoListId));
    }

    private TodoItem findTodoItemByCompanyIdAndProjectIdAndId(Long companyId, Long projectId, Long todoItemId) {
        return todoItemRepository
                .findByCompanyIdAndProjectIdAndId(companyId, projectId, todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Cannot find TodoItem by id: " + todoItemId));
    }
}
