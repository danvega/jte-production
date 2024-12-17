package dev.danvega.todo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TodoController {

    @GetMapping("/")
    public String listTodos(Model model) {
        List<Todo> todos = List.of(
                new Todo(1L, "Learn Spring Boot", "Complete Spring Boot tutorial", false),
                new Todo(2L, "Create YouTube Video", "Record and edit tutorial video", false),
                new Todo(3L, "Write Documentation", "Document the code and process", true)
        );

        model.addAttribute("todos", todos);
        model.addAttribute("pageTitle", "My Todo List");
        return "index";
    }
}
