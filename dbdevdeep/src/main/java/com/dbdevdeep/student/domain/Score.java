package com.dbdevdeep.student.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="score")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Score {
	@Id
	@Column(name="score_no")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long scoreNo;
	
	@ManyToOne
	@JoinColumn(name="curriculum_no")
	private Curriculum curriculum;
	
	@ManyToOne
	@JoinColumn(name="student_no")
	private Student student;
	
	@Column(name="score")
	private String score;
}
