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
@Table(name="timetable")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TimeTable {
	@Id
	@Column(name="timetable_no")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long timetableNo;
	
	@ManyToOne
	@JoinColumn(name="subject_no")
	private Subject subject;
	
	@Column(name="period")
	private String period;
	
	@Column(name="day")
	private String day;
	
}
