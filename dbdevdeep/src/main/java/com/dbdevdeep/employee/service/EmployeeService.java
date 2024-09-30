package com.dbdevdeep.employee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dbdevdeep.employee.domain.AuditLog;
import com.dbdevdeep.employee.domain.AuditLogDto;
import com.dbdevdeep.employee.domain.Department;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.EmployeeStatus;
import com.dbdevdeep.employee.domain.EmployeeStatusDto;
import com.dbdevdeep.employee.domain.Job;
import com.dbdevdeep.employee.domain.MySign;
import com.dbdevdeep.employee.domain.MySignDto;
import com.dbdevdeep.employee.domain.Transfer;
import com.dbdevdeep.employee.domain.TransferDto;
import com.dbdevdeep.employee.mybatis.mapper.EmployeeVoMapper;
import com.dbdevdeep.employee.repository.AuditLogRepository;
import com.dbdevdeep.employee.repository.DepartmentRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.EmployeeStatusRepository;
import com.dbdevdeep.employee.repository.JobRepository;
import com.dbdevdeep.employee.repository.MySignRepository;
import com.dbdevdeep.employee.repository.TransferRepository;
import com.dbdevdeep.employee.vo.EmployeeVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;
	private final JobRepository jobRepository;
	private final DepartmentRepository departmentRepository;
	private final EmployeeVoMapper employeeVoMapper;
	private final MySignRepository mySignRepository;
	private final TransferRepository transferRepository;
	private final EmployeeStatusRepository employeeStatusRepository;
	private final ObjectMapper objectMapper;
	private final AuditLogRepository auditLogRepository;

	// 교육청관리번호 중복 확인
	public EmployeeDto govIdCheck(String govId) {

		EmployeeDto dto = null;

		Employee e = employeeRepository.findBygovId(govId);

		if (e != null) {
			dto = new EmployeeDto().toDto(e);
		}

		return dto;
	}

	// 직원 등록
	public Employee addEmployee(EmployeeDto dto) {

		Employee result = null;

		String dept_code = dto.getDept_code();
		String job_code = dto.getJob_code();

		Department dept = departmentRepository.findByDeptCode(dept_code);
		Job job = jobRepository.findByJobCode(job_code);

		try {
			dto.setEmp_pw(passwordEncoder.encode(dto.getEmp_pw()));

			dto.setAccount_status(0);
			dto.setLogin_yn("N");
			dto.setEnt_status("Y");
			dto.setVacation_hour(120);

			if (dto.getEmp_id() == null) {
				String currentYear = String.valueOf(dto.getHire_date()).substring(0, 4);

				int count = Integer.parseInt(employeeRepository.findByempIdWhen(currentYear));

				int empYearCount = count + 1;
				String emp_id = "";

				if (empYearCount < 10) {
					emp_id = currentYear + "00" + empYearCount;
				} else if (empYearCount < 100) {
					emp_id = currentYear + "0" + empYearCount;
				} else {
					emp_id = currentYear + empYearCount;
				}
				dto.setEmp_id(emp_id);
			}

			Employee e = dto.toEntityWithJoin(dept, job);

			result = employeeRepository.save(e);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 재직 상태가 Y인 직원 목록
	public List<EmployeeDto> selectYEmployeeList() {
		List<Employee> employeeList = employeeRepository.selectYEmployeeList();

		List<EmployeeDto> employeeDtoList = new ArrayList<EmployeeDto>();
		for (Employee e : employeeList) {
			EmployeeDto dto = new EmployeeDto().toDto(e);
			dto.setDept_code(e.getDepartment().getDeptCode());
			dto.setDept_name(e.getDepartment().getDeptName());
			dto.setJob_code(e.getJob().getJobCode());
			dto.setJob_name(e.getJob().getJobName());
			employeeDtoList.add(dto);
		}
		return employeeDtoList;
	}

	// 전체 직원 목록
	public List<EmployeeDto> selectEmployeeList() {
		List<Employee> employeeList = employeeRepository.findAll();

		List<EmployeeDto> employeeDtoList = new ArrayList<EmployeeDto>();
		for (Employee e : employeeList) {
			EmployeeDto dto = new EmployeeDto().toDto(e);
			dto.setDept_code(e.getDepartment().getDeptCode());
			dto.setDept_name(e.getDepartment().getDeptName());
			dto.setJob_code(e.getJob().getJobCode());
			dto.setJob_name(e.getJob().getJobName());
			employeeDtoList.add(dto);
		}
		return employeeDtoList;
	}

	public EmployeeDto selectEmployeeOne(String writer_id) {
		Employee employee = employeeRepository.findByempId(writer_id);
		EmployeeDto dto = null;
		if (employee != null) {
			dto = new EmployeeDto().toDto(employee);
		}
		return dto;
	}

	// 반을 담당하지 않은 교무부 직원 노출
	public List<EmployeeDto> selectEmployeeByNotTeacher(String t_year) {
		List<EmployeeVo> EmployeeByNotTeach = employeeVoMapper.selectEmployeeByNotTeacher(t_year);

		List<EmployeeDto> resultList = new ArrayList<EmployeeDto>();

		for (EmployeeVo e : EmployeeByNotTeach) {
			EmployeeDto dto = new EmployeeDto().toDto(e);

			Department dept = departmentRepository.findByDeptCode(dto.getDept_code());
			Job job = jobRepository.findByJobCode(dto.getJob_code());

			dto.setDept_name(dept.getDeptName());
			dto.setJob_name(job.getJobName());

			resultList.add(dto);
		}

		return resultList;
	}

	// 비밀번호 찾기
	public int checkPw(String pwd) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();

		int result = -1;

		Employee employee = employeeRepository.findByempId(user.getUsername());

		if (passwordEncoder.matches(pwd, employee.getEmpPw())) {
			result = 1;
		}

		return result;
	}

	// 서명 찾기
	public List<MySignDto> employeeSignGet(String emp_id) {
		List<MySignDto> resultList = new ArrayList<MySignDto>();

		try {
			List<MySign> signList = mySignRepository.mySignfindAllByEmpid(emp_id);

			for (MySign sign : signList) {
				MySignDto dto = new MySignDto().toDto(sign);
				resultList.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}

	// 서명 등록
	public int employeeSignAdd(MySignDto dto) {
		int result = -1;

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();

			// login 계정 내용
			Employee employee = employeeRepository.findByempId(user.getUsername());

			// 현재 시간
			LocalDateTime ldt = LocalDateTime.now();

			// 서명 등록 시 대표 여부 Y이며 DB에 대표 여부 Y 존재 시 DB의 Y는 N으로 변경
			if (dto.getRep_yn() != null) {
				List<MySign> signList = mySignRepository.findByEmpIdAndRepYn(user.getUsername());

				if (signList != null) {
					for (int i = 0; i < signList.size(); i++) {
						MySignDto msDto = new MySignDto().toDto(signList.get(i));

						MySign ms = MySign.builder().signNo(msDto.getSign_no()).signTitle(msDto.getSign_title())
								.signType(msDto.getSign_type()).oriPicName(msDto.getOri_pic_name())
								.newPicName(msDto.getNew_pic_name()).regTime(msDto.getReg_time())
								.modTime(msDto.getMod_time()).repYn("N").employee(employee).build();

						mySignRepository.save(ms);
					}
				}
			}

			// update 시
			if (dto.getSign_no() != null) {
				dto.setMod_time(ldt);
			}

			MySign ms = MySign.builder().signNo(dto.getSign_no()).signTitle(dto.getSign_title())
					.signType(dto.getSign_type()).oriPicName(dto.getOri_pic_name()).newPicName(dto.getNew_pic_name())
					.regTime(dto.getReg_time()).modTime(dto.getMod_time()).repYn(dto.getRep_yn() == null ? "N" : "Y")
					.employee(employee).build();

			mySignRepository.save(ms);

			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int deleteSign(Long sign_no) {
		int result = 0;
		try {
			mySignRepository.deleteById(sign_no);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 상태메세지 수정
	@Transactional
	public int editChatStatus(String emp_id, String chat_status_msg) {
		int result = -1;
		result = employeeRepository.updateByEmpIdToStatus(emp_id, chat_status_msg);
		return result;
	}

	// 내 정보 수정
	public Employee editMyInfo(EmployeeDto dto) {
		EmployeeDto temp = selectEmployeeOne(dto.getEmp_id());

		temp.setEmp_phone(dto.getEmp_phone());
		temp.setEmp_detail_addr(dto.getEmp_detail_addr());
		temp.setEmp_addr(dto.getEmp_addr());
		temp.setEmp_post_code(dto.getEmp_post_code());

		if (dto.getOri_pic_name() != null) {
			temp.setOri_pic_name(dto.getOri_pic_name());
			temp.setNew_pic_name(dto.getNew_pic_name());
		}

		Department dept = departmentRepository.findByDeptCode(temp.getDept_code());
		Job job = jobRepository.findByJobCode(temp.getJob_code());

		Employee emp = Employee.builder().empId(temp.getEmp_id()).empPw(temp.getEmp_pw()).govId(temp.getGov_id())
				.empName(temp.getEmp_name()).empRrn(temp.getEmp_rrn()).empPhone(temp.getEmp_phone())
				.oriPicName(temp.getOri_pic_name()).newPicName(temp.getNew_pic_name())
				.empPostCode(temp.getEmp_post_code()).empAddr(temp.getEmp_addr())
				.empDetailAddr(temp.getEmp_detail_addr()).empInternalPhone(temp.getEmp_internal_phone())
				.vacationHour(temp.getVacation_hour()).hireDate(temp.getHire_date()).endDate(temp.getEnd_date())
				.entStatus(temp.getEnt_status()).loginYn(temp.getLogin_yn()).accountStatus(temp.getAccount_status())
				.chatStatusMsg(temp.getChat_status_msg()).job(job).department(dept).build();

		Employee result = employeeRepository.save(emp);

		return result;
	}

	// 내 비밀번호 수정
	public Employee editMyPw(String newPw) {
		Employee e = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();

		Employee employee = employeeRepository.findByempId(user.getUsername());

		EmployeeDto temp = new EmployeeDto().toDto(employee);

		Department dept = departmentRepository.findByDeptCode(temp.getDept_code());
		Job job = jobRepository.findByJobCode(temp.getJob_code());

		Employee emp = Employee.builder().empId(temp.getEmp_id()).empPw(passwordEncoder.encode(newPw))
				.govId(temp.getGov_id()).empName(temp.getEmp_name()).empRrn(temp.getEmp_rrn())
				.empPhone(temp.getEmp_phone()).oriPicName(temp.getOri_pic_name()).newPicName(temp.getNew_pic_name())
				.empPostCode(temp.getEmp_post_code()).empAddr(temp.getEmp_addr())
				.empDetailAddr(temp.getEmp_detail_addr()).empInternalPhone(temp.getEmp_internal_phone())
				.vacationHour(temp.getVacation_hour()).hireDate(temp.getHire_date()).endDate(temp.getEnd_date())
				.entStatus(temp.getEnt_status()).loginYn(temp.getLogin_yn()).accountStatus(temp.getAccount_status())
				.chatStatusMsg(temp.getChat_status_msg()).job(job).department(dept).build();

		e = employeeRepository.save(emp);

		return e;
	}

	// 직원 비밀번호 초기화
	public Employee resetPw(String emp_id, String emp_pw) {
		Employee e = null;

		Employee employee = employeeRepository.findByempId(emp_id);

		EmployeeDto temp = new EmployeeDto().toDto(employee);

		Department dept = departmentRepository.findByDeptCode(temp.getDept_code());
		Job job = jobRepository.findByJobCode(temp.getJob_code());

		Employee emp = Employee.builder().empId(temp.getEmp_id()).empPw(passwordEncoder.encode(emp_pw))
				.govId(temp.getGov_id()).empName(temp.getEmp_name()).empRrn(temp.getEmp_rrn())
				.empPhone(temp.getEmp_phone()).oriPicName(temp.getOri_pic_name()).newPicName(temp.getNew_pic_name())
				.empPostCode(temp.getEmp_post_code()).empAddr(temp.getEmp_addr())
				.empDetailAddr(temp.getEmp_detail_addr()).empInternalPhone(temp.getEmp_internal_phone())
				.vacationHour(temp.getVacation_hour()).hireDate(temp.getHire_date()).endDate(temp.getEnd_date())
				.entStatus(temp.getEnt_status()).loginYn(temp.getLogin_yn()).accountStatus(temp.getAccount_status())
				.chatStatusMsg(temp.getChat_status_msg()).job(job).department(dept).build();

		e = employeeRepository.save(emp);

		return e;
	}

	// 직원 정보 수정
	@Transactional
	public Employee employeeInfoEdit(EmployeeDto dto, AuditLogDto alDto) {

		EmployeeDto temp = selectEmployeeOne(dto.getEmp_id());

		if (dto.getOri_pic_name() != null) {
			temp.setOri_pic_name(dto.getOri_pic_name());
			temp.setNew_pic_name(dto.getNew_pic_name());
		}

		Department dept = departmentRepository.findByDeptCode(dto.getDept_code());
		Job job = jobRepository.findByJobCode(dto.getJob_code());

		Employee emp = Employee.builder().empId(dto.getEmp_id()).empPw(temp.getEmp_pw()).govId(dto.getGov_id())
				.empName(dto.getEmp_name()).empRrn(dto.getEmp_rrn()).empPhone(dto.getEmp_phone())
				.oriPicName(temp.getOri_pic_name()).newPicName(temp.getNew_pic_name())
				.empPostCode(dto.getEmp_post_code()).empAddr(dto.getEmp_addr()).empDetailAddr(dto.getEmp_detail_addr())
				.empInternalPhone(dto.getEmp_internal_phone()).vacationHour(temp.getVacation_hour())
				.hireDate(temp.getHire_date()).endDate(temp.getEnd_date()).entStatus(temp.getEnt_status())
				.loginYn(temp.getLogin_yn()).accountStatus(temp.getAccount_status())
				.chatStatusMsg(temp.getChat_status_msg()).job(job).department(dept).build();

		Employee result = employeeRepository.save(emp);

		if (result != null) {
			Employee admin = employeeRepository.findByempId(alDto.getAdmin_id());
			AuditLog auditLog = alDto.toEntityWithJoin(result, admin);
			auditLogRepository.save(auditLog);
		}

		return result;
	}

	// 직원 전근 등록
	public Transfer employeeTransfer(TransferDto dto) {
		Transfer transfer = null;

		Employee employee = employeeRepository.findByempId(dto.getEmp_id());
		Employee admin = employeeRepository.findByempId(dto.getAdmin_id());

		// 직원의 재직 상태가 재직인 경우에만 전출 가능
		if (employee.getEntStatus().equals("Y")) {
			transfer = transferRepository.save(dto.toEntityWithJoin(employee, admin)); // transfer 테이블에 전근 데이터 저장
		}

		return transfer;
	}

	// 전근 중복 방지
	public int findBySameTransfer(TransferDto dto) {
		int result = -1;

		try {
			result = transferRepository.findBySameTransfer(dto.getEmp_id(), dto.getTrans_date(),
					dto.getTrans_school_id(), dto.getTrans_type());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 직원 별 전근 기록
	public List<TransferDto> selectTransferHistoryByEmployee(String emp_id) {
		List<TransferDto> tDtoList = new ArrayList<TransferDto>();

		List<Transfer> tList = transferRepository.selectTransferHistoryByEmployee(emp_id);

		for (Transfer t : tList) {
			TransferDto dto = new TransferDto().toDto(t);

			tDtoList.add(dto);
		}

		return tDtoList;
	}

	// 휴직 등록
	public EmployeeStatus employeeRest(EmployeeStatusDto dto) {
		EmployeeStatus employeeStatus = null;

		Employee employee = employeeRepository.findByempId(dto.getEmp_id());
		Employee admin = employeeRepository.findByempId(dto.getAdmin_id());

		// 직원의 재직 상태가 재직인 경우에만 전출 가능
		if (employee.getEntStatus().equals("Y")) {
			dto.setStatus_type("R");

			employeeStatus = employeeStatusRepository.save(dto.toEntityWithJoin(employee, admin)); // employee_status
																								   // 테이블에 전근
			// 데이터 저장
		}

		return employeeStatus;
	}

	// 휴직 중복 방지
	public int findBySameRest(EmployeeStatusDto dto) {
		int result = -1;

		try {
			result = employeeStatusRepository.findBySameRest(dto.getEmp_id(), dto.getStop_date(),
					dto.getExcepted_date(), dto.getStop_reason(), dto.getStatus_type());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 직원 별 휴직 기록
	public List<EmployeeStatusDto> selectRestHistoryByEmployee(String emp_id) {
		List<EmployeeStatusDto> esDtoList = new ArrayList<EmployeeStatusDto>();

		List<EmployeeStatus> esList = employeeStatusRepository.selectRestHistoryByEmployee(emp_id);

		for (EmployeeStatus es : esList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(es);

			esDtoList.add(dto);
		}

		return esDtoList;
	}

	// 복직 등록
	public EmployeeStatus employeeReturn(EmployeeStatusDto dto) {
		EmployeeStatus employeeStatus = null;

		Employee employee = employeeRepository.findByempId(dto.getEmp_id());
		Employee admin = employeeRepository.findByempId(dto.getAdmin_id());

		// 직원의 재직 상태가 휴직인 경우에만 전출 가능
		if (employee.getEntStatus().equals("R")) {
			dto.setStatus_type("Y");

			employeeStatus = employeeStatusRepository.save(dto.toEntityWithJoin(employee, admin)); // employee_status
																								   // 테이블에 전근
			// // 데이터 저장
		}

		return employeeStatus;
	}

	// 퇴직 등록
	public EmployeeStatus employeeLeave(EmployeeStatusDto dto) {
		EmployeeStatus employeeStatus = null;

		Employee employee = employeeRepository.findByempId(dto.getEmp_id());
		Employee admin = employeeRepository.findByempId(dto.getAdmin_id());

		// 직원의 재직 상태가 재직인 경우에만 전출 가능
		if (employee.getEntStatus().equals("Y")) {
			dto.setStatus_type("N");

			employeeStatus = employeeStatusRepository.save(dto.toEntityWithJoin(employee, admin)); // employee_status
																								   // 테이블에 전근
			// // 데이터 저장
		}

		return employeeStatus;
	}

	// 퇴직 중복 방지
	public int findBySameLeave(EmployeeStatusDto dto) {
		int result = -1;

		try {
			result = employeeStatusRepository.findBySameLeave(dto.getEmp_id(), dto.getStatus_type());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 직원 별 퇴직 기록
	public List<EmployeeStatusDto> selectLeaveHistoryByEmployee(String emp_id) {
		List<EmployeeStatusDto> esDtoList = new ArrayList<EmployeeStatusDto>();

		List<EmployeeStatus> esList = employeeStatusRepository.selectLeaveHistoryByEmployee(emp_id);

		for (EmployeeStatus es : esList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(es);

			esDtoList.add(dto);
		}

		return esDtoList;
	}

	public List<TransferDto> findTransfersToProcess(LocalDate now) { // 전근 예정인 직원 목록을 조회
		List<TransferDto> transferDtoList = new ArrayList<TransferDto>();

		LocalDate yesterday = now.minusDays(1);

		List<Transfer> transferList = transferRepository.findTransfersToProcess(yesterday);

		for (Transfer t : transferList) {
			TransferDto dto = new TransferDto().toDto(t);

			transferDtoList.add(dto);
		}

		return transferDtoList;
	}

	public void processTransfer(TransferDto dto) { // 직원 상태를 "전근"으로 업데이트
		EmployeeVo vo = new EmployeeVo();
		vo.setEmp_id(dto.getEmp_id());
		vo.setEnt_status("L");
		employeeVoMapper.updateEntStatus(vo);
	}

	public List<EmployeeStatusDto> findRestsToProcess(LocalDate now) { // 휴직 예정인 직원 목록을 조회합니다.
		List<EmployeeStatusDto> restDtoList = new ArrayList<EmployeeStatusDto>();

		LocalDate yesterday = now.minusDays(1);

		List<EmployeeStatus> restList = employeeStatusRepository.findRestsToProcess(yesterday);

		for (EmployeeStatus r : restList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(r);

			restDtoList.add(dto);
		}

		return restDtoList;
	}

	public void processRest(EmployeeStatusDto dto) { // 직원 상태를 "휴직"으로 업데이트합니다.
		EmployeeVo vo = new EmployeeVo();
		vo.setEmp_id(dto.getEmp_id());
		vo.setEnt_status("R");
		employeeVoMapper.updateEntStatus(vo);
	}

	public List<EmployeeStatusDto> findLeavesToProcess(LocalDate now) { // 퇴직 예정인 직원 목록을 조회합니다.
		List<EmployeeStatusDto> leaveDtoList = new ArrayList<EmployeeStatusDto>();

		LocalDate yesterday = now.minusDays(1);

		List<EmployeeStatus> leaveList = employeeStatusRepository.findLeavesToProcess(yesterday);

		for (EmployeeStatus l : leaveList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(l);

			leaveDtoList.add(dto);
		}

		return leaveDtoList;
	}

	public void processLeave(EmployeeStatusDto dto) { // 직원 상태를 "퇴사"로 업데이트합니다.
		EmployeeVo vo = new EmployeeVo();
		vo.setEmp_id(dto.getEmp_id());
		vo.setEnt_status("N");
		employeeVoMapper.updateEntStatus(vo);
	}

	public List<EmployeeStatusDto> findReturnsToProcess(LocalDate now) { // 복귀 예정인 직원 목록을 조회합니다.
		List<EmployeeStatusDto> returnDtoList = new ArrayList<EmployeeStatusDto>();

		LocalDate yesterday = now.minusDays(1);

		List<EmployeeStatus> returnList = employeeStatusRepository.findReturnsToProcess(yesterday);

		for (EmployeeStatus r : returnList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(r);

			returnDtoList.add(dto);
		}

		return returnDtoList;
	}

	public void processReturn(EmployeeStatusDto dto) { // 직원 상태를 "재직"으로 업데이트합니다.
		EmployeeVo vo = new EmployeeVo();
		vo.setEmp_id(dto.getEmp_id());
		vo.setEnt_status("Y");
		employeeVoMapper.updateEntStatus(vo);
	}

	// Dto를 Json 형태로 변환
	public String convertDtoToJson(EmployeeDto employeeDto) {
		try {
			return objectMapper.writeValueAsString(employeeDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String compareJson(String oriDataJson, String newDataJson) {
		try {
			Map<String, Object> oriMap = objectMapper.readValue(oriDataJson, Map.class);
			Map<String, Object> newMap = objectMapper.readValue(newDataJson, Map.class);

			Map<String, Object> differences = new HashMap<>();

			for (String key : oriMap.keySet()) {
				if (!oriMap.get(key).equals(newMap.get(key))) {
					differences.put(key, newMap.get(key));
				}
			}

			for (String key : newMap.keySet()) {
				if (!oriMap.containsKey(key)) {
					differences.put(key, newMap.get(key));
				}
			}

			return objectMapper.writeValueAsString(differences);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 사원 정보 변경 기록 전부
	public List<AuditLogDto> selectAuditLogDtoList() {
		List<AuditLogDto> logDtoList = new ArrayList<>();

		List<AuditLog> logList = auditLogRepository.findAll();

		for (AuditLog log : logList) {
			AuditLogDto dto = new AuditLogDto().toDto(log);

			logDtoList.add(dto);
		}

		return logDtoList;
	}

	// 사원정보 변경 기록 하나
	public AuditLogDto selectAuditLogDto(Long audit_no) {
		AuditLog log = auditLogRepository.selectByAuditNoOne(audit_no);

		AuditLogDto logDto = new AuditLogDto().toDto(log);

		return logDto;
	}

	// 사원 정보 변경 기록 등록
	public void insertAuditLog(Employee employee, AuditLogDto alDto) {

		Employee admin = employeeRepository.findByempId(alDto.getAdmin_id());
		AuditLog auditLog = alDto.toEntityWithJoin(employee, admin);

		auditLogRepository.save(auditLog);
	}

	// 전근 기록
	public List<TransferDto> findByTransferAll() {
		List<TransferDto> dtoList = new ArrayList<TransferDto>();

		List<Transfer> transList = transferRepository.findAll();

		for (Transfer t : transList) {
			TransferDto dto = new TransferDto().toDto(t);

			dtoList.add(dto);
		}

		return dtoList;
	}

	// 휴직 기록
	public List<EmployeeStatusDto> findByRestAll() {
		List<EmployeeStatusDto> dtoList = new ArrayList<EmployeeStatusDto>();

		List<EmployeeStatus> restList = employeeStatusRepository.selectRestLogAll();

		for (EmployeeStatus r : restList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(r);

			dtoList.add(dto);
		}

		return dtoList;
	}

	// 퇴직 기록
	public List<EmployeeStatusDto> findByLeaveAll() {
		List<EmployeeStatusDto> dtoList = new ArrayList<EmployeeStatusDto>();

		List<EmployeeStatus> leaveList = employeeStatusRepository.selectLeaveLogAll();

		for (EmployeeStatus r : leaveList) {
			EmployeeStatusDto dto = new EmployeeStatusDto().toDto(r);

			dtoList.add(dto);
		}

		return dtoList;
	}

	// 모든 신청인(직원) 조회
	public List<Employee> findAllEmployees() {
		return employeeRepository.findAll();
	}

	// 특정 직원 조회 (필요 시 사용)
	public Employee findEmployeeById(String empId) {
		return employeeRepository.findById(empId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));
	}

}
