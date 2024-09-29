package com.dbdevdeep.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.repository.AttendanceRepository;
import com.dbdevdeep.employee.domain.AuditLog;
import com.dbdevdeep.employee.domain.AuditLogDto;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.repository.AuditLogRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final EmployeeRepository employeeRepository;
	private final ObjectMapper objectMapper;
	private final AuditLogRepository auditLogRepository;

	@Autowired
	public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository,
			ObjectMapper objectMapper,
			AuditLogRepository auditLogRepository) {
		this.attendanceRepository = attendanceRepository;
		this.employeeRepository = employeeRepository;
		this.objectMapper = objectMapper;
		this.auditLogRepository = auditLogRepository;
	}

	public int employeeCheckIn(String empId) {

		int result = -1;
		int overtime = 0;

		try {
			Employee employee = employeeRepository.findByempId(empId);

			LocalDate attendDate = LocalDate.now();
			LocalDateTime checkInTime = LocalDateTime.now();
			int year = attendDate.getYear();
			int month = attendDate.getMonthValue();
			int date = attendDate.getDayOfMonth();
			if (date == 1) {
				overtime = 0;
			} else {
				overtime = attendanceRepository.findByLastInfo(employee, year, month).orElse(0);
			}

			LocalDateTime checkOutTime = LocalDateTime.now();

			LocalTime thresholdTime = LocalTime.of(8, 0); // 8시 기준
			boolean isLate = checkInTime.toLocalTime().isAfter(thresholdTime);
			String lateStatus = isLate ? "Y" : "N";

			Attendance attendance = Attendance.builder().employee(employee).attendDate(attendDate)
					.checkInTime(checkInTime).checkOutTime(null).workStatus(1).lateStatus(lateStatus)
					.overtimeSum(overtime).build();

			attendanceRepository.save(attendance);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public AttendanceDto findByTodayCheckTime() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String empId = user.getUsername();

		AttendanceDto attendDto = new AttendanceDto();

		try {
			Employee employee = employeeRepository.findByempId(empId);

			try {
				LocalDate ld = LocalDate.now();

				Attendance attend = attendanceRepository.findByTodayCheckTime(employee, ld);

				if (attend != null) {
					attendDto = attendDto.toDto(attend);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return attendDto;
	}

	public int employeeCheckOut(AttendanceDto dto) {
		int result = -1;
		int overtime = 0;
		int overtimeSum = 0;
		try {
			Employee employee = employeeRepository.findByempId(dto.getEmp_id());

			LocalDate attendDate = dto.getAttend_date();
			int year = attendDate.getYear();
			int month = attendDate.getMonthValue();
			LocalDateTime checkInTime = dto.getCheck_in_time();
			LocalDateTime checkOutTime = LocalDateTime.now();

			int overtimeEnd = checkOutTime.getHour() - 16;

			overtime = attendanceRepository.findByLastInfo(employee, year, month).orElse(0);
			if (overtime > 67) {
				overtimeSum = overtime;
			} else {
				overtimeSum = overtime + overtimeEnd;
			}

			LocalTime thresholdTime = LocalTime.of(8, 0); // 8시 기준
			boolean isLate = checkInTime.toLocalTime().isAfter(thresholdTime);
			String lateStatus = isLate ? "Y" : "N";

			Attendance attendance = Attendance.builder().employee(employee).attendNo(dto.getAttend_no())
					.attendDate(attendDate).checkInTime(checkInTime).checkOutTime(checkOutTime).workStatus(2)
					.lateStatus(lateStatus).overtimeSum(overtimeSum).build();

			attendanceRepository.save(attendance);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public List<String> selectByToDayList() {
		List<String> dtoList = new ArrayList<>();

		LocalDate now = LocalDate.now();

		List<Attendance> attendList = attendanceRepository.selectByToDayList(now);

		for (Attendance attend : attendList) {
			String list = attend.getEmployee().getEmpId();

			dtoList.add(list);
		}

		return dtoList;
	}

	// vacationHour 출력
	public EmployeeDto employeeInfo(String empId) {
		Employee employee = employeeRepository.findByempId(empId);
		EmployeeDto dto = EmployeeDto.builder().vacation_hour(employee.getVacationHour()).build();

		return dto;
	}

	// 월별 기본 정보 조회
	public List<AttendanceDto> findByYearAndMonth(String empId, int year, int month) {
		List<Attendance> aList = null;
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
		Employee employee = employeeRepository.findByempId(empId);
		aList = attendanceRepository.findByYearAndMonth(employee, startDate, endDate);
		List<AttendanceDto> aDtoList = new ArrayList<AttendanceDto>();
		for (Attendance a : aList) {
			AttendanceDto dto = new AttendanceDto().toDto(a);
			aDtoList.add(dto);
		}
		return aDtoList;
	}

	// 직원 상세 페이지 근태기록 출력
	public List<AttendanceDto> findByempId(String empId) {
		List<AttendanceDto> attendDtoList = new ArrayList<AttendanceDto>();
		Employee employee = employeeRepository.findByempId(empId);

		List<Attendance> attendList = attendanceRepository.findByEmpIdList(employee);

		for (Attendance a : attendList) {
			AttendanceDto dto = new AttendanceDto().toDto(a);

			attendDtoList.add(dto);
		}

		return attendDtoList;
	}

	public Attendance employeeAttendanceChange(AttendanceDto dto, String admin_id) {
		Employee employee = employeeRepository.findByempId(dto.getEmp_id());
		Employee admin = employeeRepository.findByempId(admin_id);

		Attendance attend = dto.toEntityWithJoin(employee);

		Attendance oriDataEntity = attendanceRepository.findByattendNo(dto.getAttend_no());
		AttendanceDto oriDataDto = new AttendanceDto().toDto(oriDataEntity);
		
		Attendance attendance = attendanceRepository.save(attend);
		
		String oriData = convertDtoToJson(oriDataDto);
		String newData = convertDtoToJson(dto);
		
		AuditLogDto auditDto = new AuditLogDto();
		
		auditDto.setAudit_type("U");
		auditDto.setChanged_item("attend");
		auditDto.setNew_data(newData);
		auditDto.setOri_data(oriData);
		
		AuditLog auditLog = auditDto.toEntityWithJoin(employee, admin);
		
		auditLogRepository.save(auditLog);
 
		return attendance;
	}

	// Dto를 Json 형태로 변환
	public String convertDtoToJson(AttendanceDto attendDto) {
		try {
			return objectMapper.writeValueAsString(attendDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
