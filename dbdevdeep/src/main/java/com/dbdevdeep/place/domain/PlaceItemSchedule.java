package com.dbdevdeep.place.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dbdevdeep.employee.domain.Employee;
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

@Entity
@Table(name="place_item_schedule")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class PlaceItemSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "place_schedule_no")
	private Long placeScheduleNo;
	
	@ManyToOne
	@JoinColumn(name = "place_no")
	private Place place;
	
	@ManyToOne
	@JoinColumn(name = "item_no")
	private Item item;
	
	@ManyToOne
	@JoinColumn(name = "teacher_no")
	private TeacherHistory teacherHistory;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	private Employee employee;
	
	@Column(name = "place_schedule_title")
	private String placeScheduleTitle;
	
	@Column(name = "place_schedule_content")
	private String placeScheduleContent;
	
	@Column(name = "start_date")
	private LocalDate startDate;
	
	@Column(name = "end_date")
	private LocalDate endDate;
	
	@Column(name = "management_no")
	private String managementNo;
	
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Column(name = "reg_date")
	@CreationTimestamp
	private LocalDateTime regDate;
	
	@Column(name = "mod_date")
	@UpdateTimestamp
	private LocalDateTime modDate;
	
	
	
	
	
}
