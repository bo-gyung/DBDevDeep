package com.dbdevdeep.student.domain;

import com.dbdevdeep.employee.domain.TeacherHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="subject")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="subject_no")
	private Long subjectNo;
	
	@ManyToOne
	@JoinColumn(name="teacher_no")
	private TeacherHistory teacherHistory;
	
	@Column(name="subject_name")
	private String subjectName;
	
	
	@Column(name="subject_semester")
	private String subjectSemester;
	
}
