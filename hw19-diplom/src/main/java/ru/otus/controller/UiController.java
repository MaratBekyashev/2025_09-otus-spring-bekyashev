package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UiController {

    @GetMapping("/projects")
    public String listProject(Model model) {
        return "projects";
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        return "tasks";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        return "users";
    }

}