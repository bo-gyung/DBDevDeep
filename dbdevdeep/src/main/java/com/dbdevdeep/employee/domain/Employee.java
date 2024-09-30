package com.dbdevdeep.employee.domain;

import java.time.LocalDate;
import java.util.List;

import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.domain.ApproveLine;
import com.dbdevdeep.approve.domain.Reference;
import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.chat.domain.ChatMemberInfo;
import com.dbdevdeep.chat.domain.ChatMemberStatusHistory;
import com.dbdevdeep.chat.domain.ChatReadCheck;
import com.dbdevdeep.notice.domain.Notice;
import com.dbdevdeep.notice.domain.NoticeComment;
import com.dbdevdeep.notice.domain.NoticeReadCheck;
import com.dbdevdeep.schedule.domain.Category;
import com.dbdevdeep.schedule.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Employee {

	@Id
	@Column(name = "emp_id")
	private String empId;

	@Column(name = "emp_pw")
	private String empPw;

	@Column(name = "gov_id")
	private String govId;

	@Column(name = "emp_name")
	private String empName;

	@Column(name = "emp_rrn")
	private String empRrn;

	@Column(name = "emp_phone")
	private String empPhone;

	@Column(name = "ori_pic_name")
	private String oriPicName;

	@Column(name = "new_pic_name")
	private String newPicName;

	@Column(name = "emp_post_code")
	private String empPostCode;

	@Column(name = "emp_addr")
	private String empAddr;

	@Column(name = "emp_detail_addr")
	private String empDetailAddr;

	@ManyToOne
	@JoinColumn(name = "dept_code")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "job_code")
	private Job job;

	@Column(name = "emp_internal_phone")
	private String empInternalPhone;

	@Column(name = "vacation_hour")
	private int vacationHour;

	@Column(name = "hire_date")
	private LocalDate hireDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "ent_status")
	private String entStatus;

	@Column(name = "login_yn")
	private String loginYn;

	@Column(name = "account_status")
	private int accountStatus;

	@Column(name = "chat_status_msg")
	private String chatStatusMsg;

	// 직원 반 배정 관련
	@OneToMany(mappedBy = "employee")
	private List<TeacherHistory> teacherHistorys;

	// 일정 관련
	@OneToMany(mappedBy = "employee")
	private List<Schedule> schedules;

	// 범주 관련
	@OneToMany(mappedBy = "employee")
	private List<Category> categories;

	@OneToMany(mappedBy = "employee")
	private List<Approve> approves;

	@OneToMany(mappedBy = "employee")
	private List<ApproveLine> approveLines;

	@OneToMany(mappedBy = "employee")
	private List<Reference> references;

	@OneToMany(mappedBy = "employee")
	private List<ApproDraft> approveDrafts;

	// 근태관리 관련
	@OneToMany(mappedBy = "employee")
	private List<Attendance> attendances;

	// 공지사항
	@OneToMany(mappedBy = "employee")
	private List<Notice> notice;
	// 공지사항 댓글
	@OneToMany(mappedBy = "employee")
	private List<NoticeComment> noticeComment;
	// 공지사항 읽음확인
	@OneToMany(mappedBy = "employee")
	private List<NoticeReadCheck> noticeReadCheck;

	// 채팅 참여자 정보
	@OneToMany(mappedBy = "employee")
	private List<ChatMemberInfo> chatMemberInfo;
	// 채팅 참여자 상태이력 (member_id)
	@OneToMany(mappedBy = "member")
	private List<ChatMemberStatusHistory> chatMemberStatusHistoryMember;
	// 채팅 참여자 상태이력 (change_by_id)
	@OneToMany(mappedBy = "changeBy")
	private List<ChatMemberStatusHistory> chatMemberStatusHistoryChangeBy;
	// 채팅읽음확인
	@OneToMany(mappedBy = "employee")
	private List<ChatReadCheck> chatReadCheck;
	
	// 알람 관련
	@JsonManagedReference
	@OneToMany(mappedBy = "employee")
	private List<Alert> alerts;

}
