package br.com.hp.todolist.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hp.todolist.Model.UserModel;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID>{
    UserModel findByUsername(String username);

}

