package com.example.practicaspringboot.services;

import com.example.practicaspringboot.encapsulations.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServices {
    private List<Student> students = new ArrayList<>();

    @PostConstruct
    public void crearInformacionInicial() {
        students.add(new Student(20011136, "Juan", "Perez", "809-555-5555"));
        students.add(new Student(20021136, "Pedro", "Perez", "809-555-5555"));
        students.add(new Student(20031136, "Maria", "Perez", "809-555-5555"));
        students.add(new Student(20041136, "Jose", "Perez", "809-555-5555"));
        students.add(new Student(20051136, "Ana", "Perez", "809-555-5555"));
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void deleteStudent(int matricula) {
        for (Student student : students) {
            if (student.getMatricula() == matricula) {
                students.remove(student);
                break;
            }
        }
    }

    public void updateStudent(Student student) {
        for (Student s : students) {
            if (s.getMatricula() == student.getMatricula()) {
                s.setNombre(student.getNombre());
                s.setApellido(student.getApellido());
                s.setTelefono(student.getTelefono());
                break;
            }
        }
    }

    public Student getStudent(int matricula) {
        for (Student student : students) {
            if (student.getMatricula() == matricula) {
                return student;
            }
        }
        return null;
    }

    public boolean existsStudent(int matricula) {
        for (Student student : students) {
            if (student.getMatricula() == matricula) {
                return true;
            }
        }
        return false;
    }
}
