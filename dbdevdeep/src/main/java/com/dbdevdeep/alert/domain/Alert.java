package com.dbdevdeep.alert.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.dbdevdeep.employee.domain.Employee;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "alarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Alert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_no")
	private Long alarmNo;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	@JsonBackReference
	private Employee employee;

	@Column(name = "reference_name")
	private String referenceName;

	@Column(name = "reference_no")
	private Long referenceNo;

	@Column(name = "alarm_content")
	private String alarmContent;

	@Column(name = "read_yn")
	private String readYn;

	@Column(name = "alarm_time")
	@CreationTimestamp
	private LocalDateTime alarmTime;
}
