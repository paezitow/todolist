package br.com.hp.todolist.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import br.com.hp.todolist.Model.TaskModel;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID>{
    
    List<TaskModel> findByIdUser(UUID userId);
    TaskModel findByIdanIdUser(UUID idTask, UUID userId);
}
