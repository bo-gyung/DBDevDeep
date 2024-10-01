package com.dbdevdeep.student.controller;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
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

    @GetMapping("/excel/download/student/{student_no}")
    @ResponseBody
    public void downloadExcel(HttpServletResponse response, @PathVariable("student_no") Long student_no) throws IOException {

        // 학생 정보 및 성적 데이터 가져오기
    	StudentDto studentDto = studentService.selectStudentOne(student_no);
		StudentClassDto studentClassResult= studentService.selectStudentClass(student_no);
		List<SubjectDto> subjectList = studentService.studentSubjectRecentList(studentClassResult);
		List<CurriculumDto> curriList = studentService.selectCurriAll();
		List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(studentDto.getStudent_name());

        // 스타일: 테두리 및 가운데 정렬 설정
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        int rowIndex = 0;

        for (SubjectDto subject : subjectList) {
            // 해당 subject_no에 맞는 curriculum 필터링 (subject.getSubject_no()가 Long 타입이라고 가정)
            List<CurriculumDto> filteredCurriList = curriList.stream()
                    .filter(curri -> curri.getSubject().getSubjectNo().equals(subject.getSubject_no()))
                    .collect(Collectors.toList());

         // 교육과정 정보가 없으면 스킵
            if (filteredCurriList.isEmpty()) {
                continue; // 교육과정이 없는 경우, 다음 과목으로 넘어감
            }

            // 두 번째 행: 헤더 구성 - 이름, 커리큘럼
            Row subjectRow2 = sheet.createRow(rowIndex++);
            Cell nameHeaderCell = subjectRow2.createCell(0);
            nameHeaderCell.setCellValue("과목");
            nameHeaderCell.setCellStyle(style);

            int colIndex = 1; // 1번째 열부터 시작
            for (CurriculumDto curri : filteredCurriList) {
                Cell curriculumCell = subjectRow2.createCell(colIndex);
                curriculumCell.setCellValue(curri.getCurriculum_content());
                curriculumCell.setCellStyle(style);  // 테두리 및 정렬 스타일 적용
                sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, colIndex, colIndex + 2));
                colIndex += 3;
            }

            // 세 번째 행: 반영비율, 과목만점, 점수 구성
            Row subjectRow3 = sheet.createRow(rowIndex++);
            colIndex = 1; // 1번째 열부터 시작
            for (CurriculumDto curri : filteredCurriList) {
                Cell ratioCell = subjectRow3.createCell(colIndex);
                ratioCell.setCellValue("반영비율(%)");
                ratioCell.setCellStyle(style);

                Cell maxScoreCell = subjectRow3.createCell(colIndex + 1);
                maxScoreCell.setCellValue("과목만점");
                maxScoreCell.setCellStyle(style);

                Cell scoreCell = subjectRow3.createCell(colIndex + 2);
                scoreCell.setCellValue("점수");
                scoreCell.setCellStyle(style);

                colIndex += 3;
            }

            // 학생 정보 및 성적 데이터 출력
            Row dataRow = sheet.createRow(rowIndex++);
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue(subject.getSubject_name());
            nameCell.setCellStyle(style);

            colIndex = 1; // 데이터 시작 열
            for (CurriculumDto curri : filteredCurriList) {
                Cell ratioDataCell = dataRow.createCell(colIndex);
                ratioDataCell.setCellValue(curri.getCurriculum_ratio() + "%");
                ratioDataCell.setCellStyle(style);

                Cell maxScoreDataCell = dataRow.createCell(colIndex + 1);
                maxScoreDataCell.setCellValue(curri.getCurriculum_max_score() + "점");
                maxScoreDataCell.setCellStyle(style);

                // 점수 입력
                String scoreValue = "";
                if (scoreList != null) {
                    ScoreDto score = scoreList.stream()
                            .filter(s -> s.getCurriculum_no().equals(curri.getCurriculum_no()))
                            .findFirst()
                            .orElse(null);
                    scoreValue = score != null ? String.valueOf(score.getScore()) : "";
                }
                Cell scoreDataCell = dataRow.createCell(colIndex + 2);
                scoreDataCell.setCellValue(scoreValue);
                scoreDataCell.setCellStyle(style);

                colIndex += 3;
            }

            // 테이블 간 여백 (두 행 띄우기)
            rowIndex += 2;
        }
        
     // 파일 제목 설정
        String fileName = studentClassResult.getTeacher_history().getTYear()+"학년도 "+
							        		studentClassResult.getTeacher_history().getGrade() + "학년 " +
							        		studentClassResult.getTeacher_history().getGradeClass() + "반 " +
							                studentDto.getStudent_name() + ".xlsx";
     
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "_");
        
        // 엑셀 파일 다운로드 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    @GetMapping("/excel/download/subject/{subject_no}")
    @ResponseBody
    public void downloadSubjectExcel(HttpServletResponse response, @PathVariable("subject_no") Long subject_no) throws IOException {
    	// 과목 및 관련 데이터 가져오기
        SubjectDto subjectDto = studentService.selectSubjectOne(subject_no);
        List<CurriculumDto> curriList = studentService.selectCurriOne(subject_no);
        List<StudentClassDto> studentList = studentService.selectStudentListBySubject(subject_no);  // 해당 과목에 수강하는 학생 리스트
        List<ScoreDto> scoreList = studentService.selectScoreBySubject(subject_no);  // 과목 별 학생 성적 정보
        
        Workbook workbook = new XSSFWorkbook();
        // 시트 이름을 과목명으로 설정
        Sheet sheet = workbook.createSheet(subjectDto.getSubject_name());

        // 스타일: 테두리 및 가운데 정렬 설정
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        int rowIndex = 0;

        // 과목명 출력
        Row subjectRow1 = sheet.createRow(rowIndex++);
        Cell subjectCell = subjectRow1.createCell(0);
        subjectCell.setCellValue("과목");
        subjectCell.setCellStyle(style);

        Cell subjectValueCell = subjectRow1.createCell(1);
        subjectValueCell.setCellValue(subjectDto.getSubject_name());
        subjectValueCell.setCellStyle(style);

        // 커리큘럼 정보가 없을 때 처리
        if (curriList.isEmpty()) {
            Row emptyRow = sheet.createRow(rowIndex++);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("교육과정 정보가 없습니다.");
            emptyCell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 2));  // 3개의 셀을 병합하여 출력
            return;
        }

        // 두 번째 행: 이름 및 커리큘럼 구성
        Row subjectRow2 = sheet.createRow(rowIndex++);
        Cell nameHeaderCell = subjectRow2.createCell(0);
        nameHeaderCell.setCellValue("이름");
        nameHeaderCell.setCellStyle(style);

        int colIndex = 1; // 커리큘럼 정보가 시작되는 열 인덱스
        for (CurriculumDto curri : curriList) {
            Cell curriculumCell = subjectRow2.createCell(colIndex);
            curriculumCell.setCellValue(curri.getCurriculum_content());
            curriculumCell.setCellStyle(style);  // 테두리 및 정렬 스타일 적용
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, colIndex, colIndex + 2));
            colIndex += 3;
        }

        // 세 번째 행: 반영비율, 과목만점, 점수 구성
        Row subjectRow3 = sheet.createRow(rowIndex++);
        colIndex = 1;
        for (CurriculumDto curri : curriList) {
            Cell ratioCell = subjectRow3.createCell(colIndex);
            ratioCell.setCellValue("반영비율(%)");
            ratioCell.setCellStyle(style);

            Cell maxScoreCell = subjectRow3.createCell(colIndex + 1);
            maxScoreCell.setCellValue("과목만점");
            maxScoreCell.setCellStyle(style);

            Cell scoreCell = subjectRow3.createCell(colIndex + 2);
            scoreCell.setCellValue("점수");
            scoreCell.setCellStyle(style);

            colIndex += 3;
        }

        // 학생 정보 및 성적 데이터 출력
        for (StudentClassDto student : studentList) {
            Row dataRow = sheet.createRow(rowIndex++);
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue(student.getStudent().getStudentName());  // 학생 이름 출력
            nameCell.setCellStyle(style);

            colIndex = 1; // 데이터 시작 열
            for (CurriculumDto curri : curriList) {
                Cell ratioDataCell = dataRow.createCell(colIndex);
                ratioDataCell.setCellValue(curri.getCurriculum_ratio() + "%");
                ratioDataCell.setCellStyle(style);

                Cell maxScoreDataCell = dataRow.createCell(colIndex + 1);
                maxScoreDataCell.setCellValue(curri.getCurriculum_max_score() + "점");
                maxScoreDataCell.setCellStyle(style);

                // 점수 입력
                String scoreValue = "";
                if (scoreList != null) {
                    ScoreDto score = scoreList.stream()
                            .filter(s -> s.getStudent_no().equals(student.getStudent().getStudentNo()) && s.getCurriculum_no().equals(curri.getCurriculum_no()))
                            .findFirst()
                            .orElse(null);
                    scoreValue = (score != null) ? String.valueOf(score.getScore()) : "";
                }
                Cell scoreDataCell = dataRow.createCell(colIndex + 2);
                scoreDataCell.setCellValue(scoreValue);
                scoreDataCell.setCellStyle(style);

                colIndex += 3;
            }
        }

        // 파일 제목 설정
        String fileName = subjectDto.getTeacher_history().getTYear() + "학년도 " + 
                          subjectDto.getTeacher_history().getGrade() + "학년 " +
                          subjectDto.getTeacher_history().getGradeClass() + "반 " +
                          subjectDto.getSubject_semester() + "학기 " + 
                          subjectDto.getSubject_name() + " 성적.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "_");

        // 엑셀 파일 다운로드 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}