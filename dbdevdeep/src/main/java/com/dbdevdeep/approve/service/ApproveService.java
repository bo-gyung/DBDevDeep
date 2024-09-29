package com.dbdevdeep.approve.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.attendance.repository.AttendanceRepository;
import com.dbdevdeep.employee.domain.Department;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.Job;
import com.dbdevdeep.employee.domain.MySign;
import com.dbdevdeep.employee.domain.MySignDto;
import com.dbdevdeep.employee.repository.DepartmentRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.JobRepository;
import com.dbdevdeep.employee.repository.MySignRepository;
import com.dbdevdeep.websocket.config.WebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class ApproveService {

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
	private final AttendanceRepository attendanceRepository; 

	@Autowired
	public ApproveService(ApproveRepository approveRepository, VacationRequestRepository vacationRequestRepository,
			ApproveLineRepository approveLineRepository, ApproFileRepository approFileRepository,
			ReferenceRepository referenceRepository, EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository, JobRepository jobRepository,
			TempEditRepository tempEditRepository, FileService fileService, MySignRepository mySignRepository,
			AlertRepository alertRepository, AlertMessageHandler alertMessageHandler, ObjectMapper objectMapper,
			WebSocketHandler webSocketHandler, AlertMessageHandler alertMessageHandler1,
			RestHolidayService restHolidayService, AttendanceRepository attendanceRepository) {
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
		this.attendanceRepository = attendanceRepository;
	}

	// 보고서 삭제
	@Transactional
	public int deleteDocuApprove(Long appro_no) {
		int result = 0;
		try {
			Approve approve = approveRepository.findByApproNo(appro_no);
			approveLineRepository.deleteByApprove(approve);
			approveRepository.deleteById(appro_no);
			
			// 보고서 삭제 시 alert_status x로 변경
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


	// 승인 처리 + 최종 승인 처리
	@Transactional
	public int agreeApproveLine(Long approNo, String empId, String principalId, String deptCode, String jobCode,
			String signImage) {
		Approve approve = approveRepository.findByApproNo(approNo);
		Employee employee = employeeRepository.findByempId(empId); // 문서 직원
		Employee principalEmployee = employeeRepository.findByempId(principalId); // 결재를 하는 직원
		Department department = departmentRepository.findByDeptCode(deptCode);
		Job job = jobRepository.findByJobCode(jobCode);
		ApproveLine approveLine = approveLineRepository.findByApproveIdAndEmpId(approNo, principalId);
		
		ApproveLineDto alDto = new ApproveLineDto().toDto(approveLine);
		alDto.setAppro_line_no(approveLine.getApproLineNo());
		alDto.setAppro_line_status(2); // 승인 상태
		alDto.setAppro_line_sign(signImage);
		ApproveLine updateAl = alDto.toEntity(approve, principalEmployee);
		approveLineRepository.save(updateAl);

		int nowEmpNo = approveLine.getApproLineOrder();

		int maxEmpNo = approveLineRepository.findMaxOrderByApproNo(approNo);

		if (nowEmpNo < maxEmpNo) {
			int nextEmpNo = nowEmpNo + 1;
			ApproveLine nextApproveLine = approveLineRepository.findByApproNoAndOrder(approNo, nextEmpNo);
			if (nextApproveLine != null) {
				ApproveLineDto nextDto = new ApproveLineDto().toDto(nextApproveLine);
				nextDto.setAppro_line_no(nextApproveLine.getApproLineNo());
				nextDto.setAppro_line_status(1);
				Employee nextEmployee = employeeRepository.findByempId(nextApproveLine.getEmployee().getEmpId());
				ApproveLine nextEmpDto = nextDto.toEntity(approve, nextEmployee);
				ApproveLine a = approveLineRepository.save(nextEmpDto);

				// 승인 시 alert에 저장
				// 다음 결재자에게 alert
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
		} else { // 최종 승인
			if (approve != null) {
				ApproveDto aDto = new ApproveDto().toDto(approve);
				aDto.setAppro_no(approNo);
				aDto.setAppro_status(1);
				Approve finalApprove = aDto.toEntity(employee, department, job, null);
				Approve a = approveRepository.save(finalApprove);

				// 승인 시 alert에 저장
				// 최종 승인 시 결재 요청자에게 alert
				if (a != null) {
					AlertDto alertDto = new AlertDto();
					alertDto.setReference_name("approve");
					alertDto.setReference_no(a.getApproNo());
					if (a.getApproType() == 0) {
						if (a.getApproStatus() == 1) {
							alertDto.setAlarm_title("휴가 결재 완료");
						} else if (a.getApproStatus() == 2) {
							alertDto.setAlarm_title("휴가 결재 반려");
						}
					} else {
						if (a.getApproStatus() == 1) {
							alertDto.setAlarm_title("보고서 결재 완료");
						} else if (a.getApproStatus() == 2) {
							alertDto.setAlarm_title("보고서 결재 반려");
						}
					}
					alertDto.setAlarm_content(a.getApproTitle());
					alertDto.setAlarm_status("N");

					Alert alert = alertDto.toEntity(a.getEmployee());
					try {
						webSocketHandler.sendAlert(alertRepository.save(alert));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return 1;
	}

	// 보고서 반려 처리
	@Transactional
	public int backDocuLine(ApproveLineDto approveLineDto, String empId, String deptCode, String jobCode) {
		Approve approve = approveRepository.findByApproNo(approveLineDto.getAppro_no());
		Employee employee = employeeRepository.findByempId(empId); // 문서 직원
		Employee principalEmployee = employeeRepository.findByempId(approveLineDto.getEmp_id()); // 결재를 하는 직원
		Department department = departmentRepository.findByDeptCode(deptCode);
		Job job = jobRepository.findByJobCode(jobCode);
		ApproveLine approveLine = approveLineRepository.findByApproveIdAndEmpId(approveLineDto.getAppro_no(),
				approveLineDto.getEmp_id());

		// Approve 엔티티 업데이트
		ApproveDto approveDto = new ApproveDto().toDto(approve); // 기존 Approve를 DTO로 변환
		approveDto.setAppro_status(2); // 반려 상태로 설정
		Approve updatedApprove = approveDto.toEntity(employee, department, job, null); // DTO를 다시 엔티티로 변환
		Approve alertApprove = approveRepository.save(updatedApprove); // 업데이트된 엔티티 저장

		// 보고서 반려 시 alert에 저장
		// 반려 시 결재 요청자에게 alert
		if (alertApprove != null) {
			AlertDto alertDto = new AlertDto();
			alertDto.setReference_name("approve");
			alertDto.setReference_no(alertApprove.getApproNo());
			alertDto.setAlarm_status("N");
			alertDto.setAlarm_title("보고서 결재 반려");
			alertDto.setAlarm_content(alertApprove.getApproTitle());

			Alert alert = alertDto.toEntity(alertApprove.getEmployee());
			try {
				webSocketHandler.sendAlert(alertRepository.save(alert));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String backSign = "반려";

		// ApproveLine 엔티티 업데이트
		ApproveLineDto alDto = new ApproveLineDto().toDto(approveLine);
		alDto.setAppro_line_no(approveLine.getApproLineNo());
		alDto.setAppro_line_status(3); // 반려 상태
		alDto.setReason_back(approveLineDto.getReason_back()); // 반려 사유 설정
		alDto.setAppro_line_sign(backSign);
		ApproveLine updateAl = alDto.toEntity(approve, principalEmployee);
		approveLineRepository.save(updateAl); // 업데이트된 엔티티 저장

		return 1;
	}



	// 내가 결재 요청한 보고서 목록 조회
	public List<ApproveDto> selectApproveDocuList(String empId) {
		List<Approve> approDocuList = approveRepository.findByAnotherTypeAndEmpId(empId);
		List<ApproveDto> approDocuListDto = new ArrayList<ApproveDto>();
		for (Approve a : approDocuList) {
			ApproveDto dto = new ApproveDto().toDto(a);
			approDocuListDto.add(dto);
		}
		return approDocuListDto;
	}

	public List<ApproveDto> completeDocuList() {
		List<Approve> approveList = approveRepository.findCompleteDocu();
		List<ApproveDto> approveDtoList = new ArrayList<ApproveDto>();

		for (Approve a : approveList) {
			ApproveDto dto = new ApproveDto().toDto(a);
			approveDtoList.add(dto);
		}
		return approveDtoList;
	}

	// 내가 결재 요청한 목록 조회
	public List<ApproveDto> selectApproveList(String empId) {
		List<Approve> approveList = approveRepository.findByTypeAndEmpId(empId);
		List<ApproveDto> approveDtoList = new ArrayList<ApproveDto>();

		for (Approve a : approveList) {

			ApproveDto dto = ApproveDto.builder().appro_no(a.getApproNo()).emp_id(a.getEmployee().getEmpId())
					.dept_code(a.getDepartment().getDeptCode()).job_code(a.getJob().getJobCode())
					.appro_time(a.getApproTime()).appro_type(a.getApproType()).appro_status(a.getApproStatus())
					.appro_title(a.getApproTitle()).appro_content(a.getApproContent()).build();

			approveDtoList.add(dto);
		}

		return approveDtoList;
	}

	// 요청 받은 보고서 내역
	public List<ApproveDto> comeDocuRequestList(String empId) {
		List<Object[]> results = approveRepository.findDocuRequestsForUser(empId);

		List<ApproveDto> approvalList = new ArrayList<>();
		for (Object[] result : results) {
			// 결과 배열에서 데이터 추출
			LocalDateTime approTime = (LocalDateTime) result[2];

			ApproveDto dto = ApproveDto.builder().appro_no(((Number) result[0]).longValue())
					.appro_title((String) result[1]).appro_time(approTime).appro_name((String) result[3])
					.appro_status((Integer) result[4]).build();
			approvalList.add(dto);
		}
		return approvalList;
	}

	// 요청 받은 결재 내역
	// native 를 사용했을 경우 Timestamp 를 변환하는 과정
	public List<ApproveDto> comeApproveRequestList(String username) {
		List<Object[]> results = approveRepository.findApprovalRequestsForUser(username);

		List<ApproveDto> approvalList = new ArrayList<>();
		for (Object[] result : results) {
			// Timestamp를 LocalDateTime으로 변환
			LocalDateTime approTime = null;
			if (result[2] != null) {
				approTime = ((Timestamp) result[2]).toLocalDateTime();
			}

			ApproveDto dto = ApproveDto.builder().appro_no(((Number) result[0]).longValue())
					.appro_title((String) result[1]).appro_time(approTime).appro_name((String) result[3])
					.appro_status((Integer) result[4]).vac_type(result[5] != null ? (Integer) result[5] : null).build();
			approvalList.add(dto);
		}

		return approvalList;
	}

	// 참조 지정 받은 결재 내역
	// native 를 사용하지 않으면 localDateTime 만 설정해주면 된다.
	public List<ApproveDto> comeRefList(String empId) {
		List<Object[]> results = approveRepository.refRequests(empId);

		List<ApproveDto> refList = new ArrayList<>();
		for (Object[] result : results) {
			// 결과 배열에서 데이터 추출
			LocalDateTime approTime = (LocalDateTime) result[2]; // LocalDateTime으로 캐스팅

			ApproveDto dto = ApproveDto.builder().appro_no(((Number) result[0]).longValue())
					.appro_title((String) result[1]).appro_time(approTime).appro_name((String) result[3])
					.appro_status((Integer) result[4]).vac_type(result[5] != null ? (Integer) result[5] : null).build();
			refList.add(dto);
		}

		return refList;
	}

	// 서명 리스트 출력
	public List<MySignDto> signList(String empId) {
		List<MySign> mySignList = mySignRepository.mySignfindAllByEmpid(empId);

		List<MySignDto> mDto = new ArrayList<>();
		for (MySign signDto : mySignList) {
			MySignDto mySignDto = new MySignDto().toDto(signDto);
			mDto.add(mySignDto);
		}
		return mDto;
	}

	// 보고서 상세
	public Map<String, Object> getDocuDetail(Long approNo) {
		Map<String, Object> detailMap = new HashMap<>();

		// Approve 객체 가져오기
		Approve approve = approveRepository.findByApproNo(approNo);

		// ApproveLine 가져오기
		List<ApproveLine> approLineList = approveLineRepository.findByApprove(approve);

		// 결재자와 협의자를 구분하여 리스트로 할당
		List<ApproveLineDto> approveLineList = approLineList.stream().filter(a -> "N".equals(a.getConsultYn())) // 결재자만
																												// 필터링
				.map(a -> ApproveLineDto.builder().emp_id(a.getEmployee().getEmpId())
						.appro_line_name(a.getApproLineName()).appro_line_order(a.getApproLineOrder())
						.appro_line_status(a.getApproLineStatus()).appro_permit_time(a.getApproPermitTime())
						.reason_back(a.getReasonBack()).consult_yn(a.getConsultYn())
						.appro_line_sign(a.getApproLineSign()).build())
				.collect(Collectors.toList());

		List<ApproveLineDto> consultLineList = approLineList.stream().filter(a -> "Y".equals(a.getConsultYn())) // 협의자만
																												// 필터링
				.map(a -> ApproveLineDto.builder().emp_id(a.getEmployee().getEmpId())
						.appro_line_name(a.getApproLineName()).appro_line_order(a.getApproLineOrder())
						.appro_line_status(a.getApproLineStatus()).appro_permit_time(a.getApproPermitTime())
						.reason_back(a.getReasonBack()).consult_yn(a.getConsultYn())
						.appro_line_sign(a.getApproLineSign()).build())
				.collect(Collectors.toList());

		detailMap.put("lDto", approveLineList); // 결재자 리스트
		detailMap.put("cDto", consultLineList); // 협의자 리스트

		// ApproFile 조회
		ApproFile aFile = approFileRepository.findByApprove(approve);
		ApproFileDto fileDto = null;
		if (aFile != null) {
			fileDto = new ApproFileDto().toDto(aFile);
		}
		detailMap.put("fDto", fileDto);

		// 6. Approve DTO 변환
		ApproveDto aDto = new ApproveDto().toDto(approve);
		// TempEdit이 null인지 확인하고, null이 아닌 경우에만 ApproveDto 변환을 시도
		if (approve.getTempEdit() != null) {
			aDto.setTemp_no(approve.getTempEdit().getTempNo());
		} else {
			aDto.setTemp_no(null); // TempEdit이 null일 경우
		}

		aDto.setAppro_no(approve.getApproNo());
		aDto.setEmp_id(approve.getEmployee().getEmpId());
		aDto.setDept_code(approve.getDepartment().getDeptCode());
		aDto.setJob_code(approve.getJob().getJobCode());
		aDto.setAppro_name(approve.getApproName());
		aDto.setAppro_time(approve.getApproTime());
		aDto.setAppro_type(approve.getApproType());
		aDto.setAppro_status(approve.getApproStatus());
		aDto.setAppro_title(approve.getApproTitle());
		aDto.setAppro_content(approve.getApproContent());

		detailMap.put("aDto", aDto);

		return detailMap;
	}

	// 결재 상세
	public Map<String, Object> getApproveDetail(Long approNo) {
		Map<String, Object> detailMap = new HashMap<>();

		// Approve 객체 가져오기
		Approve approve = approveRepository.findByApproNo(approNo);

		// ApproveLine 가져오기
		List<ApproveLine> approLineList = approveLineRepository.findByApprove(approve);

		// 결재자와 협의자를 구분하여 리스트로 할당
		List<ApproveLineDto> approveLineList = approLineList.stream().filter(a -> "N".equals(a.getConsultYn())) // 결재자만
																												// 필터링
				.map(a -> ApproveLineDto.builder().emp_id(a.getEmployee().getEmpId())
						.appro_line_name(a.getApproLineName()).appro_line_order(a.getApproLineOrder())
						.appro_line_status(a.getApproLineStatus()).appro_permit_time(a.getApproPermitTime())
						.reason_back(a.getReasonBack()).consult_yn(a.getConsultYn())
						.appro_line_sign(a.getApproLineSign()).build())
				.collect(Collectors.toList());

		List<ApproveLineDto> consultLineList = approLineList.stream().filter(a -> "Y".equals(a.getConsultYn())) // 협의자만
																												// 필터링
				.map(a -> ApproveLineDto.builder().emp_id(a.getEmployee().getEmpId())
						.appro_line_name(a.getApproLineName()).appro_line_order(a.getApproLineOrder())
						.appro_line_status(a.getApproLineStatus()).appro_permit_time(a.getApproPermitTime())
						.reason_back(a.getReasonBack()).consult_yn(a.getConsultYn())
						.appro_line_sign(a.getApproLineSign()).build())
				.collect(Collectors.toList());

		detailMap.put("lDto", approveLineList); // 결재자 리스트
		detailMap.put("cDto", consultLineList); // 협의자 리스트

		// 3. Reference
		List<Reference> reference = referenceRepository.findByApprove(approve);
		List<ReferenceDto> refList = reference.stream()
				.map(r -> ReferenceDto.builder().emp_id(r.getEmployee().getEmpId()).ref_name(r.getRefName()).build())
				.collect(Collectors.toList());
		detailMap.put("rDto", refList);

		// 4. VacationRequest
		VacationRequest vRequest = vacationRequestRepository.findByApprove(approve);
		VacationRequestDto vDto = null;
		if (vRequest != null) {
			vDto = new VacationRequestDto().toDto(vRequest);
		}
		detailMap.put("vDto", vDto);

		// ApproFile을 Approve 객체를 기준으로 조회
		ApproFile aFile = approFileRepository.findByApprove(approve);
		ApproFileDto fileDto = null;
		if (aFile != null) {
			fileDto = new ApproFileDto().toDto(aFile);
		}
		detailMap.put("fDto", fileDto);

		// 6. Approve DTO 변환
		ApproveDto aDto = new ApproveDto().toDto(approve);
		// TempEdit이 null인지 확인하고, null이 아닌 경우에만 ApproveDto 변환을 시도
		if (approve.getTempEdit() != null) {
			aDto.setTemp_no(approve.getTempEdit().getTempNo());
		} else {
			aDto.setTemp_no(null); // TempEdit이 null일 경우
		}

		aDto.setAppro_no(approve.getApproNo());
		aDto.setEmp_id(approve.getEmployee().getEmpId());
		aDto.setDept_code(approve.getDepartment().getDeptCode());
		aDto.setJob_code(approve.getJob().getJobCode());
		aDto.setAppro_name(approve.getApproName());
		aDto.setAppro_time(approve.getApproTime());
		aDto.setAppro_type(approve.getApproType());
		aDto.setAppro_status(approve.getApproStatus());
		aDto.setAppro_title(approve.getApproTitle());
		aDto.setAppro_content(approve.getApproContent());

		detailMap.put("aDto", aDto);

		return detailMap;
	}

	// 보고서 수정
	@Transactional
	public int docuUpdate(ApproveDto approveDto, List<ApproveLineDto> approveLineDtos, ApproFileDto approFileDto,
			MultipartFile file) {
		Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
		Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
		Job job = jobRepository.findByJobCode(approveDto.getJob_code());
		TempEdit tempEdit = tempEditRepository.findByTempNo(approveDto.getTemp_no());

		Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
		approve = approveRepository.save(approve);

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

		if (approFileDto != null && approFileDto.getOri_file() != null && !approFileDto.getOri_file().isEmpty()) {
			approFileDto.setAppro_no(approveDto.getAppro_no());
			ApproFile approFile = approFileDto.toEntity(approve);
			approFileRepository.save(approFile);
		}

		return 1;
	}



	// 보고서 결재 요청
	@Transactional
	public int docuApproUp(ApproveDto approveDto, List<ApproveLineDto> approveLineDtos, ApproFileDto approFileDto) {
		Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
		Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
		Job job = jobRepository.findByJobCode(approveDto.getJob_code());

		TempEdit tempEdit = null;
		if (approveDto.getTemp_no() != null) {
			tempEdit = tempEditRepository.findById(approveDto.getTemp_no()).orElse(null);
		}

		Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
		approve = approveRepository.save(approve);
		Long approNo = approve.getApproNo();

		// alert에 저장하기 위한 ApproveLine 선언
		ApproveLine alertApproLine = null;

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
				alertApproLine = a;
			}
		}

		// alertApproLine != null이면 alert에 저장
		// 첫 결재자에게 alert
		if (alertApproLine != null) {
			AlertDto alertDto = new AlertDto();
			alertDto.setReference_name("approve");
			alertDto.setReference_no(alertApproLine.getApprove().getApproNo());
			alertDto.setAlarm_title("보고서 결재 요청");
			alertDto.setAlarm_status("N");
			alertDto.setAlarm_content(alertApproLine.getApprove().getApproTitle());

			Alert alert = alertDto.toEntity(alertApproLine.getEmployee());
			
			try {
				webSocketHandler.sendAlert(alertRepository.save(alert));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (approFileDto != null) { // approFileDto가 null이 아닐 때만 저장
			approFileDto.setAppro_no(approNo); // Approve의 appro_no 설정
			ApproFile approFile = approFileDto.toEntity(approve);
			approFileRepository.save(approFile);
		}

		return 1;

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
