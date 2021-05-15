package com.asyncworking.repositories;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.models.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    @Query(nativeQuery = true, value =
            "select *\n" +
                    "from todo_list\n" +
                    "where project_id = :projectId\n" +
                    "order by created_time desc" +
                    " limit :quantity")
    List<TodoList> findTodoListsByProjectIdOrderByCreatedTime(@Param("projectId") Long projectId, @Param("quantity") Integer quantity);


    @Query(nativeQuery = true, value =
            "select *\n" +
                    "from todo_list\n" +
                    "where id = :todoListId" +
                    "order by created_time desc" +
                    " limit :quantity")
    List<TodoList> findTodoItemAndList(@Param("todoListId") Long todoListId, @Param("quantity") Integer quantity);

//    @Query(nativeQuery = true, value =
//    "SELECT * FROM todo_list WHERE id = :todoListId")
//    TodoList findById(Long todoListId);
}
