package br.com.guilhermedg.todolist.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/")
    public String getUser() {
        return "User1";
    }

    @PostMapping("/")
    public void create(@RequestBody UserModel userModel) {
        System.out.println("User: " + userModel.getName() + " created!");
    }
}
