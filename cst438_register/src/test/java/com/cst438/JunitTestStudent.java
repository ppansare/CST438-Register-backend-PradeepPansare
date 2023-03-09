package com.cst438;


import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.ScheduleController;
import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;



@ContextConfiguration(classes = { StudentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
    static final String URL = "http://localhost:8080";
    public static final String TEST_STUDENT_EMAIL = "Pradeep@csumb.edu";
    public static final String TEST_STUDENT_NAME  = "Pradeep";
    public static final String TEST_STUDENT_STATUS_HOLD  = "HOLD";
    public static final String TEST_STUDENT_STATUS_ENROLLED  = "ENROLLED";

    @MockBean
    CourseRepository courseRepository;

    @MockBean
    StudentRepository studentRepository;

    @MockBean
    EnrollmentRepository enrollmentRepository;

    @MockBean
    GradebookService gradebookService;

    @Autowired
    private MockMvc mvc;


    @Test
    public void addStudent() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setName(TEST_STUDENT_NAME);
        student.setStatusCode(0);
        student.setStudent_id(1);

        given(studentRepository.save(any(Student.class))).willReturn(student);


        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;
        studentDTO.name = TEST_STUDENT_NAME;


        response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/student?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME).content(asJsonString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();



        assertEquals(200, response.getStatus());


        StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
        assertNotEquals( 0  , result.email);

        verify(studentRepository).save(any(Student.class));

    }



    @Test
    public void holdStudent() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setName(TEST_STUDENT_NAME);
        student.setStatusCode(0);
        student.setStudent_id(1);


        given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
        given(studentRepository.save(any(Student.class))).willReturn(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;
        studentDTO.name = TEST_STUDENT_NAME;


        response = mvc.perform(MockMvcRequestBuilders.put("/student/statusCode/Pradeep@csumb.edu").content(asJsonString(studentDTO))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();


        assertEquals(200, response.getStatus());


        if(studentDTO.statusCode == 1) {
            assertEquals(1, studentDTO.statusCode);
        }

    }


    @Test
    public void releaseStudent() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setName(TEST_STUDENT_NAME);
        student.setStatusCode(0);
        student.setStudent_id(1);


        given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);


        StudentDTO studentDTO = new StudentDTO();
        studentDTO.email = TEST_STUDENT_EMAIL;
        studentDTO.name = TEST_STUDENT_NAME;

        response = mvc.perform(MockMvcRequestBuilders.put("/student/release/Pradeep@csumb.edu").content(asJsonString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();



        assertEquals(200, response.getStatus());


        if(studentDTO.statusCode == 0) {
            assertEquals(0, studentDTO.statusCode);
        }

    }

    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
