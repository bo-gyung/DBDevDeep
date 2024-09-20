package com.dbdevdeep.student.domain;

import com.dbdevdeep.employee.domain.TeacherHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SubjectDto {
	private Long subject_no;
	private TeacherHistory teacher_history;
	private String subject_name;
	private String subject_semester;
	
	public Subject toEntity() {
		return Subject.builder()
				.subjectNo(subject_no)
				.subjectName(subject_name)
				.teacherHistory(teacher_history)
				.subjectSemester(subject_semester)
				.build();
	}
	
	public SubjectDto toDto(Subject sub) {
		return SubjectDto.builder()
				.subject_no(sub.getSubjectNo())
				.subject_name(sub.getSubjectName())
				.subject_semester(sub.getSubjectSemester())
				.teacher_history(sub.getTeacherHistory())
				.build();
	}
	
	
	
}
