package com.dbdevdeep.employee.domain;

import java.time.LocalDate;

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
@Table(name="employee_status")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class EmployeeStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="status_no")
	private Long statusNo;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	private Employee employee;
	
	@Column(name="stop_reason")
	private String stopReason;
	
	@Column(name="stop_date")
	private LocalDate stopDate;
	
	@Column(name="excepted_date")
	private LocalDate exceptedDate;
	
	@Column(name="return_date")
	private LocalDate returnDate;
	
	@Column(name="status_type")
	private String statusType;
	
	@ManyToOne
	@JoinColumn(name="admin_id")
	private Employee admin;
}
