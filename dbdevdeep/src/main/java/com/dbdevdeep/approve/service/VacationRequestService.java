package com.dbdevdeep.approve.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import com.dbdevdeep.FileService;
import com.dbdevdeep.alert.config.AlertMessageHandler;
import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.alert.domain.AlertDto;
import com.dbdevdeep.alert.repository.AlertRepository;
import com.dbdevdeep.approve.domain.ApproFile;
import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.domain.ApproveDto;
import com.dbdevdeep.approve.domain.ApproveLine;
import com.dbdevdeep.approve.domain.ApproveLineDto;
import com.dbdevdeep.approve.domain.Reference;
import com.dbdevdeep.approve.domain.ReferenceDto;
import com.dbdevdeep.approve.domain.TempEdit;
import com.dbdevdeep.approve.domain.VacationRequest;
import com.dbdevdeep.approve.domain.VacationRequestDto;
import com.dbdevdeep.approve.repository.ApproFileRepository;
import com.dbdevdeep.approve.repository.ApproveLineRepository;
import com.dbdevdeep.approve.repository.ApproveRepository;
import com.dbdevdeep.approve.repository.ReferenceRepository;
import com.dbdevdeep.approve.repository.TempEditRepository;
import com.dbdevdeep.approve.repository.VacationRequestRepository;
import com.dbdevdeep.employee.domain.Department;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.Job;
import com.dbdevdeep.employee.repository.DepartmentRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.JobRepository;
import com.dbdevdeep.employee.repository.MySignRepository;
import com.dbdevdeep.websocket.config.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class VacationRequestService {

	private final ApproveRepository approveRepository;
	private final VacationRequestRepository vacationRequestRepository;
	private final ApproveLineRepository approveLineRepository;
	private final ApproFileRepository approFileRepository;
	private final ReferenceRepository referenceRepository;
	private final EmployeeRepository employeeRepository;
	private final FileService fileService;
	private final DepartmentRepository departmentRepository;
	private final JobRepository jobRepository;
	private final TempEditRepository tempEditRepository;
	private final MySignRepository mySignRepository;
	private final AlertRepository alertRepository;
	private final ObjectMapper objectMapper;
	private final WebSocketHandler webSocketHandler;
	private final AlertMessageHandler alertMessageHandler;
	private final RestHolidayService restHolidayService;
	
	@Autowired
	public VacationRequestService(ApproveRepository approveRepository, VacationRequestRepository vacationRequestRepository,
			ApproveLineRepository approveLineRepository, ApproFileRepository approFileRepository,
			ReferenceRepository referenceRepository, EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository, JobRepository jobRepository,
			TempEditRepository tempEditRepository, FileService fileService, MySignRepository mySignRepository,
			AlertRepository alertRepository, AlertMessageHandler alertMessageHandler, ObjectMapper objectMapper,
			WebSocketHandler webSocketHandler, AlertMessageHandler alertMessageHandler1,
			RestHolidayService restHolidayService) {
		this.approveRepository = approveRepository;
		this.vacationRequestRepository = vacationRequestRepository;
		this.approveLineRepository = approveLineRepository;
		this.approFileRepository = approFileRepository;
		this.referenceRepository = referenceRepository;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.jobRepository = jobRepository;
		this.tempEditRepository = tempEditRepository;
		this.fileService = fileService;
		this.mySignRepository = mySignRepository;
		this.alertRepository = alertRepository;
		this.objectMapper = objectMapper;
		this.webSocketHandler = webSocketHandler;
		this.alertMessageHandler = alertMessageHandler1;
		this.restHolidayService = restHolidayService;
	}
	
	// 휴가 삭제
		@Transactional
		public int deleteApprove(Long appro_no) {
			int result = 0;
			try {
				// 결재 정보 가져오기
				Approve approve = approveRepository.findById(appro_no).orElse(null);
				if (approve == null) {
					return result; // 결재 정보가 없으면 아무 작업도 하지 않음
				}

				// 관련된 휴가 요청 가져오기 minusVac
				VacationRequest vacationRequest = vacationRequestRepository.findByApprove(approve);
				if (vacationRequest != null) {
					// 휴가 유형 확인 및 복구할 시간 계산
					int vacType = vacationRequest.getVacType();
					if (vacType == 0 || vacType == 3 || vacType == 6 || vacType == 7) {
						// 복구할 시간 계산
						int hoursToRestore = minusVac(new VacationRequestDto().toDto(vacationRequest));

						// 사용자 정보 가져오기
						Employee employee = approve.getEmployee();

						// 부서와 직급 가져오기
						Department department = approve.getDepartment();
						Job job = approve.getJob();

						// 사용자의 vacation_hour 복구
						EmployeeDto employeeDto = new EmployeeDto().toDto(employee);
						int updatedVacationHour = employeeDto.getVacation_hour() + hoursToRestore;
						employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); 

						// 부서와 직급 설정
						employeeDto.setDepartment(department);
						employeeDto.setJob(job);

						// 업데이트된 DTO를 엔티티로 변환하여 저장
						employeeRepository.save(employeeDto.toEntity());
					}
				}

				// 관련된 참조, 휴가 요청, 승인 라인, 결재 정보를 삭제
				referenceRepository.deleteByApprove(approve);
				vacationRequestRepository.deleteByApprove(approve);
				approveLineRepository.deleteByApprove(approve);
				approveRepository.deleteById(appro_no);
				
				// 휴가 삭제 후 alert_status X로 설정
				List<Alert> alertList = alertRepository.findByreferenceNameandreferenceNo("approve", appro_no);
				for(Alert alert : alertList) {
					AlertDto alertDto = new AlertDto().toDto(alert);
					
					alertDto.setAlarm_status("X");
					Alert a = alertDto.toEntity(alert.getEmployee());
					
					try {
						alertRepository.delete(a);
						
						webSocketHandler.sendAlert(a);
					} catch (IOException except) {
						except.printStackTrace();
					}
				}
				
				result = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	
	// 휴가 반려 처리
		
		public int backApproveLine(ApproveLineDto approveLineDto, String empId, int vacType, LocalDateTime startDate,
				LocalDateTime endDate, String deptCode, String jobCode) {
			// 1. 필수 엔티티 조회
			Approve approve = approveRepository.findByApproNo(approveLineDto.getAppro_no());
			Employee employee = employeeRepository.findByempId(empId); // 문서 직원
			Employee principalEmployee = employeeRepository.findByempId(approveLineDto.getEmp_id()); // 결재를 하는 직원
			Department department = departmentRepository.findByDeptCode(deptCode);
			Job job = jobRepository.findByJobCode(jobCode);
			ApproveLine approveLine = approveLineRepository.findByApproveIdAndEmpId(approveLineDto.getAppro_no(),
					approveLineDto.getEmp_id());

			// 2. Approve 엔티티 업데이트
			ApproveDto approveDto = new ApproveDto().toDto(approve); // 기존 Approve를 DTO로 변환
			approveDto.setAppro_status(2); // 반려 상태로 설정
			Approve updatedApprove = approveDto.toEntity(employee, department, job, null); // DTO를 다시 엔티티로 변환
			Approve alertApprove = approveRepository.save(updatedApprove); // 업데이트된 엔티티 저장

			// 휴가 반려 시 alert에 저장
			// 반려 시 결재 요청자에게 alert
			if (alertApprove != null) {
				AlertDto alertDto = new AlertDto();
				alertDto.setReference_name("approve");
				alertDto.setReference_no(alertApprove.getApproNo());
				alertDto.setAlarm_status("N");
				alertDto.setAlarm_title("휴가 결재 반려");
				alertDto.setAlarm_content(alertApprove.getApproTitle());

				Alert alert = alertDto.toEntity(alertApprove.getEmployee());
				try {
					webSocketHandler.sendAlert(alertRepository.save(alert));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			String backSign = "반려";
			// 3. ApproveLine 엔티티 업데이트
			ApproveLineDto alDto = new ApproveLineDto().toDto(approveLine);
			alDto.setAppro_line_no(approveLine.getApproLineNo());
			alDto.setAppro_line_status(3); // 반려 상태
			alDto.setReason_back(approveLineDto.getReason_back()); // 반려 사유 설정
			alDto.setAppro_line_sign(backSign);
			ApproveLine updateAl = alDto.toEntity(approve, principalEmployee);
			approveLineRepository.save(updateAl); // 업데이트된 엔티티 저장

			// 4. Employee 엔티티 업데이트 (휴가 시간 계산 및 반영)

			// 6. 휴가 시간 차감 로직 추가 plusVac
			VacationRequest vacationRequest = vacationRequestRepository.findByApprove(approve);
			if (vacationRequest != null) {
				// 휴가 유형 확인 및 복구할 시간 계산
				if (vacType == 0 || vacType == 3 || vacType == 6 || vacType == 7) {
					// 복구할 시간 계산
					int hoursToRestore = minusVac(new VacationRequestDto().toDto(vacationRequest));


					// 사용자의 vacation_hour 복구
					EmployeeDto employeeDto = new EmployeeDto().toDto(employee);
					int updatedVacationHour = employeeDto.getVacation_hour() + hoursToRestore;
					employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); 

					// 부서와 직급 설정
					employeeDto.setDepartment(department);
					employeeDto.setJob(job);

					// 업데이트된 DTO를 엔티티로 변환하여 저장
					employeeRepository.save(employeeDto.toEntity());
				}
			}
			


			return 1;
		}
	
	// 결재 수정
		@Transactional
		public int approUpdate(ApproveDto approveDto, List<VacationRequestDto> vacationRequestDtos,
				List<ApproveLineDto> approveLineDtos, List<ReferenceDto> referenceDto, ApproFileDto approFileDto,
				MultipartFile file) {

			Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
			Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
			Job job = jobRepository.findByJobCode(approveDto.getJob_code());

			TempEdit tempEdit = null;
			if (approveDto.getTemp_no() != null) { // temp_no가 null이 아닐 때만 findById를 호출합니다.
				tempEdit = tempEditRepository.findById(approveDto.getTemp_no()).orElse(null);
			}

			// 1. approve 테이블에 저장
			Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
			approve = approveRepository.save(approve);

			// 2. vacation_request 테이블에 저장
			VacationRequest oriVacRequest = vacationRequestRepository.findByApprove(approve);
			if (oriVacRequest != null) {
				vacationRequestRepository.delete(oriVacRequest);
			}

			VacationRequestDto oriVacDto = vacationRequestDtos.get(0);
			VacationRequestDto newVacDto = vacationRequestDtos.get(1);

			newVacDto.setAppro_no(approveDto.getAppro_no());
			newVacDto.setVac_yn("Y");
			VacationRequest vacationRequest = newVacDto.toEntity(approve);
			vacationRequestRepository.save(vacationRequest);

			// 3. approve_Line 테이블에 저장
			approveLineRepository.deleteByApprove(approve);
			
			List<Alert> alertList = alertRepository.findByreferenceNameandreferenceNo("approve", approve.getApproNo());
			
			for(Alert alert : alertList) {
				AlertDto alertDto = new AlertDto().toDto(alert);
				
				alertDto.setAlarm_status("X");
				Alert a = alertDto.toEntity(alert.getEmployee());
				
				try {
					alertRepository.save(a);
					
					webSocketHandler.sendAlert(a);
				} catch (IOException except) {
					except.printStackTrace();
				}
			}
			
			for (ApproveLineDto lineDto : approveLineDtos) {
				lineDto.setAppro_no(approve.getApproNo());

				// 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
				Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
				if (lineEmployee == null) {
					continue; // 또는 오류를 처리합니다.
				}

				ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
				
				ApproveLine a = approveLineRepository.save(approveLine);
				
				if (a != null) {
					AlertDto alertDto = new AlertDto();
					alertDto.setReference_name("approve");
					alertDto.setReference_no(a.getApprove().getApproNo());
					if(a.getApprove().getApproType() == 0) {
						alertDto.setAlarm_title("휴가 결재 요청");						
					} else {
						alertDto.setAlarm_title("보고서 결재 요청");
					}
					alertDto.setAlarm_content(a.getApprove().getApproTitle());
					alertDto.setAlarm_status("N");

					// alert 저장 후 웹 소켓에 데이터 전송
					Alert alert = alertDto.toEntity(a.getEmployee());
					try {
						webSocketHandler.sendAlert(alertRepository.save(alert));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			// 4. reference 테이블에 저장
			referenceRepository.deleteByApprove(approve);
			for (ReferenceDto refDto : referenceDto) {
				refDto.setAppro_no(approve.getApproNo());

				// 각 Reference에 맞는 Employee 객체를 가져옵니다.
				Employee refEmployee = employeeRepository.findByempId(refDto.getEmp_id());
				if (refEmployee == null) {
					continue; // 또는 오류를 처리합니다.
				}

				Reference reference = refDto.toEntity(approve, refEmployee);
				referenceRepository.save(reference);
			}

			// 5. appro_file 테이블에 저장
			if (approFileDto != null && approFileDto.getOri_file() != null && !approFileDto.getOri_file().isEmpty()) {
				approFileDto.setAppro_no(approveDto.getAppro_no());
				ApproFile approFile = approFileDto.toEntity(approve);
				approFileRepository.save(approFile);
			}

			// 6. 휴가 시간 차감 로직 추가
			int vacHours = 0;

			// 특정 휴가 유형에 대해 차감 로직을 수행
			if ((oriVacDto.getVac_type() == 0 || oriVacDto.getVac_type() == 3 || oriVacDto.getVac_type() == 6
					|| oriVacDto.getVac_type() == 7)
					&& (oriVacDto.getVac_type() != newVacDto.getVac_type()
							|| !oriVacDto.getStart_time().equals(newVacDto.getStart_time())
							|| !oriVacDto.getEnd_time().equals(newVacDto.getEnd_time()))) {

				// 변경 전 시간 복구
				vacHours += minusVac(oriVacDto);
			}

			// 새로운 휴가 유형에 대해 차감 로직을 수행
			if (newVacDto.getVac_type() == 0 || newVacDto.getVac_type() == 3 || newVacDto.getVac_type() == 6
					|| newVacDto.getVac_type() == 7) {

				// 변경 후 시간 차감
				vacHours -= minusVac(newVacDto);
			}

			// 기존 vacation_hour에서 차감
			EmployeeDto employeeDto = new EmployeeDto().toDto(employee); // Employee 객체를 DTO로 변환
			int updatedVacationHour = employeeDto.getVacation_hour() + vacHours;
			employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); // 0 미만으로 가지 않도록 설정

			employeeDto.setDepartment(department); // Department 엔티티 설정
			employeeDto.setJob(job); // Job 엔티티 설정

			// 업데이트된 DTO를 엔티티로 변환하여 저장
			employeeRepository.save(employeeDto.toEntity());

			return 1;

		}
	
	// 휴가 결재 요청
		@Transactional
		public int approUp(ApproveDto approveDto, VacationRequestDto vacationRequestDto,
				List<ApproveLineDto> approveLineDtos, List<ReferenceDto> referenceDto, ApproFileDto approFileDto,
				MultipartFile file) {

			// Employee, Department, Job 정보 가져오기
			Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
			if (employee == null) {
			}

			Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
			Job job = jobRepository.findByJobCode(approveDto.getJob_code());

			// TempEdit 처리
			TempEdit tempEdit = null;
			if (approveDto.getTemp_no() != null) {
				tempEdit = tempEditRepository.findById(approveDto.getTemp_no()).orElse(null);
			}

			// 1. approve 테이블에 저장
			Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
			approve = approveRepository.save(approve);
			Long approNo = approve.getApproNo(); // 저장 후 생성된 appro_no 가져오기

			// 2. vacation_request 테이블에 저장
			vacationRequestDto.setAppro_no(approNo);
			VacationRequest vacationRequest = vacationRequestDto.toEntity(approve);
			vacationRequestRepository.save(vacationRequest);

			ApproveLine alertApproveLine = null; // 처음 결재 요청 시 해당하는 결재자에게 alert

			// 3. approve_Line 테이블에 저장
			for (ApproveLineDto lineDto : approveLineDtos) {
				lineDto.setAppro_no(approNo); // Approve의 appro_no 설정

				// 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
				Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
				if (lineEmployee == null) {
					continue; // 또는 오류를 처리합니다.
				}

				ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
				ApproveLine a = approveLineRepository.save(approveLine);

				if (a.getApproLineStatus() == 1) {
					alertApproveLine = a; // approve_status == 1인 경우 emp_id에 alert
				}
			}

			// alertApproveLine != null이면 alert에 저장
			// 첫 결재자에게 alert
			if (alertApproveLine != null) {
				AlertDto alertDto = new AlertDto();
				alertDto.setReference_name("approve");
				alertDto.setReference_no(alertApproveLine.getApprove().getApproNo());
				alertDto.setAlarm_status("N");
				if (alertApproveLine.getApprove().getApproType() == 0) {
					alertDto.setAlarm_title("휴가 결재 요청");
				} else {
					alertDto.setAlarm_title("보고서 결재 요청");
				}
				alertDto.setAlarm_content( alertApproveLine.getApprove().getApproTitle());
	 
				// alert 저장 후 웹 소켓에 데이터 전송
				Alert alert = alertDto.toEntity(alertApproveLine.getEmployee());
				
				try {
					webSocketHandler.sendAlert(alertRepository.save(alert));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 4. reference 테이블에 저장
			for (ReferenceDto refDto : referenceDto) {
				refDto.setAppro_no(approNo); // Approve의 appro_no 설정

				// 각 Reference에 맞는 Employee 객체를 가져옵니다.
				Employee refEmployee = employeeRepository.findByempId(refDto.getEmp_id());
				if (refEmployee == null) {
					continue; // 또는 오류를 처리합니다.
				}

				Reference reference = refDto.toEntity(approve, refEmployee);
				Reference r = referenceRepository.save(reference);

				// 참조자 알림 구현 필요
			}

			// 5. appro_file 테이블에 저장
			if (approFileDto != null) { // approFileDto가 null이 아닐 때만 저장
				approFileDto.setAppro_no(approNo); // Approve의 appro_no 설정
				ApproFile approFile = approFileDto.toEntity(approve);
				approFileRepository.save(approFile);
			}

			// 6. 휴가 시간 차감 로직 추가
			int vacationType = vacationRequestDto.getVac_type();
			if (vacationType == 0 || vacationType == 3 || vacationType == 6 || vacationType == 7) {
				// 사용자가 선택한 휴가 유형에 따른 차감 시간 계산
				int minusHour = minusVac(vacationRequestDto);

				// 기존 vacation_hour에서 차감
				EmployeeDto employeeDto = new EmployeeDto().toDto(employee); // Employee 객체를 DTO로 변환
				int updatedVacationHour = employeeDto.getVacation_hour() - minusHour;
				employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); // 0 미만으로 가지 않도록 설정

				employeeDto.setDepartment(department); // Department 엔티티 설정
				employeeDto.setJob(job); // Job 엔티티 설정

				// 업데이트된 DTO를 엔티티로 변환하여 저장
				employeeRepository.save(employeeDto.toEntity());
			}

			return 1;
		}
	
	private boolean isWeekendOrHoliday(LocalDate date, List<LocalDate> holidays) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || holidays.contains(date);
    }
	
	// 휴가 시간 계산 메서드
	public int minusVac(VacationRequestDto vacationRequestDto) {
	    LocalDateTime startDate = vacationRequestDto.getStart_time();
	    LocalDateTime endDate = vacationRequestDto.getEnd_time();
	    int vacType = vacationRequestDto.getVac_type();
	    int hoursToDeduct = 0;

	    // 공휴일 정보 가져오기 (중복 제거를 위해 Set 사용)
	    Set<LocalDate> holidays = new HashSet<>();
	    for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
	        int startMonth = (year == startDate.getYear()) ? startDate.getMonthValue() : 1;
	        int endMonth = (year == endDate.getYear()) ? endDate.getMonthValue() : 12;
	        
	        for (int month = startMonth; month <= endMonth; month++) {
	            holidays.addAll(restHolidayService.getHolidays(year, month));
	        }
	    }

	    // 시작일과 종료일이 동일한 경우
	    if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
	        if (!isWeekendOrHoliday(startDate.toLocalDate(), new ArrayList<>(holidays))) {
	            // 같은 날이면 시간 계산
	            hoursToDeduct = endDate.getHour() - startDate.getHour();
	            if (startDate.toLocalTime().equals(endDate.toLocalTime())) {
	                hoursToDeduct = 8; // 동일한 시간이면 하루(8시간)
	            }
	        }
	    } else {
	        // 날짜가 다를 경우 일자별로 주말 및 공휴일 체크
	        long daysBetween = java.time.Duration
	                .between(startDate.toLocalDate().atStartOfDay(), endDate.toLocalDate().atStartOfDay()).toDays() + 1;

	        for (int i = 0; i < daysBetween; i++) {
	            LocalDate currentDate = startDate.toLocalDate().plusDays(i);
	            if (!isWeekendOrHoliday(currentDate, new ArrayList<>(holidays))) {
	                hoursToDeduct += 8; // 주말과 공휴일이 아니면 8시간 차감
	            }
	        }
	    }

	    // 반차 처리 (반차일 경우 4시간 차감)
	    if (vacType == 6 || vacType == 7) {
	        hoursToDeduct = 4;
	    }

	    return hoursToDeduct;
	}

	
	public List<VacationRequestDto> selectApprovedVacationRequest(String empId) {
		List<VacationRequest> vacationRequestList = vacationRequestRepository
				.findByApprove_ApproStatusAndApprove_Employee_EmpId(1, empId);

		List<VacationRequestDto> vacationRequestDtoList = new ArrayList<VacationRequestDto>();
		for (VacationRequest vr : vacationRequestList) {
			VacationRequestDto dto = new VacationRequestDto().toDto(vr);
			vacationRequestDtoList.add(dto);
		}

		return vacationRequestDtoList;
	}

	public List<VacationRequestDto> selectAllApprovedVacationRequest() {
		List<VacationRequest> vacationRequestList = vacationRequestRepository.findByApprove_ApproStatus(1);

		List<VacationRequestDto> vacationRequestDtoList = new ArrayList<VacationRequestDto>();
		for (VacationRequest vr : vacationRequestList) {
			VacationRequestDto dto = new VacationRequestDto().toDto(vr);
			vacationRequestDtoList.add(dto);
		}

		return vacationRequestDtoList;
	}
	
}
