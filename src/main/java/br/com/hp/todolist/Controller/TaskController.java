package br.com.hp.todolist.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hp.todolist.Model.TaskModel;
import br.com.hp.todolist.Repository.ITaskRepository;
import br.com.hp.todolist.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;
    
    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.findByIdUser((UUID)idUser));
        
    }

    @PostMapping
    public ResponseEntity<String> creatTask(@RequestBody TaskModel taskModel, HttpServletRequest request){

        var idUser = request.getAttribute("idUser");
        taskModel.setUserId((UUID)idUser);

        var currentDate = LocalDateTime.now();

        
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio e (ou) término deve ser maior que a data atual.");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser anterior a data de término.");
        }
        
        this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tarefa criada com sucesso!");
    }

    @PutMapping("/{idTask}")
    public ResponseEntity<String> updateTask(@RequestBody TaskModel taskModel,  HttpServletRequest request, @PathVariable UUID idTask){
        var idUser = request.getAttribute("idUser");
        var task = this.taskRepository.findById(idTask).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        
        if(!task.getUserId().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para atualizar esta tarefa.");
        }
        
        Utils.copyNunNullProperties(taskModel, task);
        
        taskModel.setUserId((UUID) idUser);
        taskModel.setId(idTask);
        return ResponseEntity.status(HttpStatus.OK).body("Tarefa atualizada com sucesso!");
    }
}
