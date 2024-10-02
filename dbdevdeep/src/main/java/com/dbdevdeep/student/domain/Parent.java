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
@Table(name="parent_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Parent {
	@Id
	@Column(name="parent_no")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long parentNo;
	
	@ManyToOne
	@JoinColumn(name="student_no")
	private Student student;
	
	@Column(name="parent_name")
	private String parentName;
	
	@Column(name="parent_birth")
	private String parentBirth;
	
	@Column(name="parent_phone")
	private String parentPhone;
	
	@Column(name="parent_relation")
	private String parentRelation;
}
