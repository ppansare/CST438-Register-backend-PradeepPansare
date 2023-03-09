package com.cst438.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;



@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/student")
    @Transactional
    public StudentDTO addStudent(@RequestBody StudentDTO student) {
        Student addedStudent = studentRepository.findByEmail(student.email);

        while(addedStudent == null) {
            Student NewStudents = new Student();
            NewStudents.setName(student.name);
            NewStudents.setEmail(student.email);
            Student newStudent = studentRepository.save(NewStudents);
            StudentDTO ans = createStudentDTO(newStudent);

            return ans;
        }

        if(addedStudent != null) {
            throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Email already exists");
        }
        return null;
    }


    @PutMapping("/student/statusCode/{email}")
    @Transactional
    public void holdStudent(@PathVariable String email) {
        Student newStudent = studentRepository.findByEmail(email);

        if(newStudent==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found");
        }

        if(newStudent != null) {
            newStudent.setStatusCode(1);
            newStudent.setStatus("Hold");
        }

    }


    @PutMapping("/student/release/{email}")
    @Transactional
    public void releaseStudent(@PathVariable String email) {
        Student newStudent = studentRepository.findByEmail(email);

        if(newStudent != null ) {
            newStudent.setStatusCode(0);
            newStudent.setStatus(null);

        }
        else {
            throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student was not found.");
        }

    }


    private StudentDTO createStudentDTO(Student addition) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.name = addition.getName();
        studentDTO.student_id = addition.getStudent_id();
        studentDTO.email = addition.getEmail();
        studentDTO.statusCode = addition.getStatusCode();
        studentDTO.status = addition.getStatus();

        return studentDTO;
    }




}

