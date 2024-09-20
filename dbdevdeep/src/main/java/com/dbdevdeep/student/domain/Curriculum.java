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
@Table(name="curriculum")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Curriculum {
	@Id
	@Column(name="curriculum_no")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long curriculumNo;
	
	@ManyToOne
	@JoinColumn(name="subject_no")
	private Subject subject;
	
	@Column(name="curriculum_content")
	private String curriculumContent;
	
	@Column(name="curriculum_ratio")
	private String curriculumRatio;
	
	@Column(name="curriculum_max_score")
	private String curriculumMaxScore;
}
