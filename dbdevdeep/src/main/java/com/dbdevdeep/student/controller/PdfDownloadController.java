package com.dbdevdeep.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.service.PdfService;
import com.dbdevdeep.student.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PdfDownloadController {

    private final StudentService studentService;
    private final PdfService pdfService;

    @Autowired
    public PdfDownloadController(StudentService studentService, PdfService pdfService) {
        this.studentService = studentService;
        this.pdfService = pdfService;
    }

    @GetMapping("/student/pdf/{student_no}")
    public void generatePdf(@PathVariable("student_no") Long student_no, HttpServletResponse response) throws Exception {
        // 학생 정보와 학년 이력 정보 조회
    	StudentDto dto = studentService.selectStudentOne(student_no);
		List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
		List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
		List<SubjectDto> subjectList = studentService.mySubjectList();
		List<CurriculumDto> curriList = studentService.selectCurriAll();
		List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);

        // PDF 생성
        byte[] pdfBytes = pdfService.createPdf(dto, studentClassResultList,resultList,subjectList,curriList,scoreList);

        // PDF 응답 설정
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=student_" + student_no + ".pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
