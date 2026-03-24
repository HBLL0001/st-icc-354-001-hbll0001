package com.example.practicaspringboot.controllers;

import com.example.practicaspringboot.encapsulations.Student;
import com.example.practicaspringboot.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentServices studentServices;

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("students", studentServices.getStudents());
        return "index";
    }

    @GetMapping("/crear")
    public String createForm(Model model){
        model.addAttribute("student", new Student());
        return "crear";
    }

    @PostMapping("/crear")
    public String create(@ModelAttribute Student student){
        studentServices.addStudent(student);
        return "redirect:/student/";
    }

    @GetMapping("/editar/{matricula}")
    public String editForm(@PathVariable int matricula, Model model){
        Student student = studentServices.getStudent(matricula);
        if (student == null) {
            return "redirect:/student/";
        }
        model.addAttribute("student", student);
        return "editar";
    }

    @PatchMapping("/actualizar")
    public ResponseEntity<String> update(@RequestBody Student student) {
        if (studentServices.existsStudent(student.getMatricula())) {
            studentServices.updateStudent(student);
            return ResponseEntity.ok("Estudiante actualizado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estudiante no encontrado.");
        }
    }

    @DeleteMapping("/borrar/{matricula}")
    public ResponseEntity<String> delete(@PathVariable int matricula) {
        if (studentServices.existsStudent(matricula)) {
            studentServices.deleteStudent(matricula);
            return ResponseEntity.ok("Estudiante eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estudiante no encontrado.");
        }
    }
}

