package com.dbdevdeep.student.controller;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ExcelDownloadController {
    private final StudentService studentService;

    public ExcelDownloadController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/excel/download/{student_no}")
    @ResponseBody
    public void downloadExcel(HttpServletResponse response, @PathVariable("student_no") Long student_no) throws IOException {

    	// 학생 정보 및 성적 데이터 가져오기
    	StudentDto studentDto = studentService.selectStudentOne(student_no);
    	List<SubjectDto> subjectList = studentService.mySubjectList();
    	List<CurriculumDto> curriList = studentService.selectCurriAll();
    	List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);
    	// 응답의 컨텐츠 타입과 파일 이름 설정
    	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    	response.setHeader("Content-Disposition", "attachment; filename="+studentDto.getStudent_name()+"_"+".xlsx");
        // 워크북과 시트 생성
        Workbook workbook = new XSSFWorkbook();


        // 점수 맵핑
        Map<Long, String> scoreMap = new HashMap<>();
        for (ScoreDto score : scoreList) {
            scoreMap.put(score.getCurriculum_no(), score.getScore());
        }

        // 각 과목마다 별도의 시트를 생성하여 데이터 작성
        for (SubjectDto subject : subjectList) {
            Sheet sheet = workbook.createSheet(subject.getSubject_name() + "_" + studentDto.getStudent_name());

            // 첫 번째 행에 제목 작성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("과목");
            headerRow.createCell(1).setCellValue("1차수행 반영비율(%)");
            headerRow.createCell(2).setCellValue("1차수행 과목만점");
            headerRow.createCell(3).setCellValue("1차수행 점수");
            headerRow.createCell(4).setCellValue("중간고사 반영비율(%)");
            headerRow.createCell(5).setCellValue("중간고사 과목만점");
            headerRow.createCell(6).setCellValue("중간고사 점수");
            headerRow.createCell(7).setCellValue("2차수행 반영비율(%)");
            headerRow.createCell(8).setCellValue("2차수행 과목만점");
            headerRow.createCell(9).setCellValue("2차수행 점수");
            headerRow.createCell(10).setCellValue("기말고사 반영비율(%)");
            headerRow.createCell(11).setCellValue("기말고사 과목만점");
            headerRow.createCell(12).setCellValue("기말고사 점수");

            // 각 과목 및 교육과정 데이터를 Excel에 작성
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(subject.getSubject_name()); // 과목명
            
            for (CurriculumDto curri : curriList) {
                if (curri.getSubject().getSubjectNo().equals(subject.getSubject_no())) {
                    String curriculumContent = curri.getCurriculum_content();

                    // 1차수행
                    if (curriculumContent.contains("1차수행")) {
                        row.createCell(1).setCellValue(curri.getCurriculum_ratio()); // 반영비율
                        row.createCell(2).setCellValue(curri.getCurriculum_max_score()); // 과목 만점
                        row.createCell(3).setCellValue(scoreMap.get(curri.getCurriculum_no())); // 학생 점수
                    }
                    // 중간고사
                    else if (curriculumContent.contains("중간고사")) {
                        row.createCell(4).setCellValue(curri.getCurriculum_ratio()); // 반영비율
                        row.createCell(5).setCellValue(curri.getCurriculum_max_score()); // 과목 만점
                        row.createCell(6).setCellValue(scoreMap.get(curri.getCurriculum_no())); // 학생 점수
                    }
                    // 2차수행
                    else if (curriculumContent.contains("2차수행")) {
                        row.createCell(7).setCellValue(curri.getCurriculum_ratio()); // 반영비율
                        row.createCell(8).setCellValue(curri.getCurriculum_max_score()); // 과목 만점
                        row.createCell(9).setCellValue(scoreMap.get(curri.getCurriculum_no())); // 학생 점수
                    }
                    // 기말고사
                    else if (curriculumContent.contains("기말고사")) {
                        row.createCell(10).setCellValue(curri.getCurriculum_ratio()); // 반영비율
                        row.createCell(11).setCellValue(curri.getCurriculum_max_score()); // 과목 만점
                        row.createCell(12).setCellValue(scoreMap.get(curri.getCurriculum_no())); // 학생 점수
                    }
                }
            }
        }

        // 엑셀 파일을 HTTP 응답으로 출력
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
