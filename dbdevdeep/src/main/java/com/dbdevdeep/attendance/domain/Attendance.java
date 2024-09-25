package com.dbdevdeep.attendance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dbdevdeep.employee.domain.Employee;

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
@Table(name="attendance")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="attend_no")
	private Long attendNo;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	private Employee employee;
	
	@Column(name="attend_date")
	private LocalDate attendDate;
	
	@Column(name="check_in_time")
	private LocalDateTime checkInTime;
	
	@Column(name="check_out_time")
	private LocalDateTime checkOutTime;
	
	@Column(name="work_status")
	private int workStatus;
	
	@Column(name="late_status")
	private String lateStatus;
	
	@Column(name="overtime_sum")
	private int overtimeSum;
}
