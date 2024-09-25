package com.dbdevdeep.employee.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import groovy.transform.ToString;
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
@Table(name="audit_log")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
@ToString
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="audit_no")
	private Long auditNo;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="admin_id")
	private Employee admin;
	
	@Column(name="reg_time")
	@CreationTimestamp
	private LocalDateTime regTime;
	
	@Column(name="audit_type")
	private String audit_type;
	
	@Column(name="changed_item")
	private String changedItem;
	
	@Column(name="ori_data", columnDefinition = "json")
	private String oriData;
	
	@Column(name="new_data", columnDefinition = "json")
	private String newData;
}
