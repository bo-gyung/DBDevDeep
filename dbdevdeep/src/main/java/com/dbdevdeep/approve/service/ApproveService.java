package com.dbdevdeep.approve.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Timestamp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
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
import com.dbdevdeep.employee.domain.MySign;
import com.dbdevdeep.employee.domain.MySignDto;
import com.dbdevdeep.employee.repository.DepartmentRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.JobRepository;
import com.dbdevdeep.employee.repository.MySignRepository;

import jakarta.persistence.EntityNotFoundException;
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
	
	@Autowired
	public ApproveService(ApproveRepository approveRepository, 
            VacationRequestRepository vacationRequestRepository,
            ApproveLineRepository approveLineRepository, 
            ApproFileRepository approFileRepository,
            ReferenceRepository referenceRepository,
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            JobRepository jobRepository,
            TempEditRepository tempEditRepository,
            FileService fileService,
            MySignRepository mySignRepository) {
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

	        // 관련된 휴가 요청 가져오기
	        VacationRequest vacationRequest = vacationRequestRepository.findByApprove(approve);
	        if (vacationRequest != null) {
	            // 휴가 유형 확인 및 복구할 시간 계산
	            int vacType = vacationRequest.getVacType();
	            if (vacType == 0 || vacType == 3 || vacType == 6 || vacType == 7 ) {
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
	                employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); // 0 미만으로 가지 않도록 설정

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

	        result = 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	// 승인 처리
	@Transactional
	public int agreeApproveLine(Long approNo, String empId, String principalId , String deptCode, String jobCode, String signImage) {
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
	    
	    if(nowEmpNo < maxEmpNo) {
	    	int nextEmpNo = nowEmpNo + 1;
	    	ApproveLine nextApproveLine = approveLineRepository.findByApproNoAndOrder(approNo,nextEmpNo);
	    	if(nextApproveLine != null) {
	    		ApproveLineDto nextDto = new ApproveLineDto().toDto(nextApproveLine);
	    		nextDto.setAppro_line_no(nextApproveLine.getApproLineNo());
	    		nextDto.setAppro_line_status(1);
	    		Employee nextEmployee = employeeRepository.findByempId(nextApproveLine.getEmployee().getEmpId());
	    		ApproveLine nextEmpDto = nextDto.toEntity(approve, nextEmployee);
	    		approveLineRepository.save(nextEmpDto);
	    	}
	    }else {
	    	if(approve != null) {
	    		ApproveDto aDto = new ApproveDto().toDto(approve);
	    		aDto.setAppro_no(approNo);
	    		aDto.setAppro_status(1);
	    		Approve finalApprove = aDto.toEntity(employee, department, job, null);
	    		approveRepository.save(finalApprove);
	    	}
	    }
	    return 1;
	}
	
	// 반려 처리
	@Transactional
	public int backApproveLine(ApproveLineDto approveLineDto, String empId, int vacType, LocalDateTime startDate, LocalDateTime endDate, String deptCode, String jobCode) {
	    // 1. 필수 엔티티 조회
	    Approve approve = approveRepository.findByApproNo(approveLineDto.getAppro_no());
	    Employee employee = employeeRepository.findByempId(empId); // 문서 직원
	    Employee principalEmployee = employeeRepository.findByempId(approveLineDto.getEmp_id()); // 결재를 하는 직원
	    Department department = departmentRepository.findByDeptCode(deptCode);
	    Job job = jobRepository.findByJobCode(jobCode);
	    ApproveLine approveLine = approveLineRepository.findByApproveIdAndEmpId(approveLineDto.getAppro_no(), approveLineDto.getEmp_id());

	    // 2. Approve 엔티티 업데이트
	    ApproveDto approveDto = new ApproveDto().toDto(approve); // 기존 Approve를 DTO로 변환
	    approveDto.setAppro_status(2); // 반려 상태로 설정
	    Approve updatedApprove = approveDto.toEntity(employee, department, job, null); // DTO를 다시 엔티티로 변환
	    approveRepository.save(updatedApprove); // 업데이트된 엔티티 저장

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

	 // 6. 휴가 시간 차감 로직 추가
	    if (vacType == 0 || vacType == 3 || vacType == 6 || vacType == 7 ) {
	        // 사용자가 선택한 휴가 유형에 따른 차감 시간 계산
	        int plusHour = plusVac(vacType ,startDate , endDate);

	        // 기존 vacation_hour에서 차감
	        EmployeeDto employeeDto = new EmployeeDto().toDto(employee); // Employee 객체를 DTO로 변환
	        int updatedVacationHour = employeeDto.getVacation_hour() + plusHour;
	        employeeDto.setVacation_hour(Math.max(updatedVacationHour, 0)); // 0 미만으로 가지 않도록 설정

	        employeeDto.setDepartment(department); // Department 엔티티 설정
	        employeeDto.setJob(job); // Job 엔티티 설정

	        // 업데이트된 DTO를 엔티티로 변환하여 저장
	        employeeRepository.save(employeeDto.toEntity());
	    }
	    
	    return 1; 
	}


	// 내가 결재 요청한 보고서 목록 조회
	public List<ApproveDto> selectApproveDocuList(String empId){
		List<Approve> approDocuList = approveRepository.findByAnotherTypeAndEmpId(empId);
		List<ApproveDto> approDocuListDto = new ArrayList<ApproveDto>();
		for(Approve a : approDocuList) {
			ApproveDto dto = new ApproveDto().toDto(a);
			approDocuListDto.add(dto);
		}
		return approDocuListDto;
	}
	
	// 내가 결재 요청한 목록 조회
	public List<ApproveDto> selectApproveList(String empId){
		List<Approve> approveList = approveRepository.findByTypeAndEmpId(empId);
		List<ApproveDto> approveDtoList = new ArrayList<ApproveDto>();
		
		for(Approve a : approveList) {
		
			ApproveDto dto = ApproveDto.builder()
					.appro_no(a.getApproNo())
					.emp_id(a.getEmployee().getEmpId())
					.dept_code(a.getDepartment().getDeptCode())
					.job_code(a.getJob().getJobCode())
					.appro_time(a.getApproTime())
					.appro_type(a.getApproType())
					.appro_status(a.getApproStatus())
					.appro_title(a.getApproTitle())
					.appro_content(a.getApproContent())
					.build();
			
			
			approveDtoList.add(dto);
		}
		
		return approveDtoList;
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

	        ApproveDto dto = ApproveDto.builder()
	                .appro_no(((Number) result[0]).longValue())  
	                .appro_title((String) result[1])             
	                .appro_time(approTime)                      
	                .appro_name((String) result[3])             
	                .appro_status((Integer) result[4])          
	                .vac_type(result[5] != null ? (Integer) result[5] : null)
	                .build();
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

	        ApproveDto dto = ApproveDto.builder()
	                .appro_no(((Number) result[0]).longValue())  
	                .appro_title((String) result[1])             
	                .appro_time(approTime)                      
	                .appro_name((String) result[3])             
	                .appro_status((Integer) result[4])          
	                .vac_type(result[5] != null ? (Integer) result[5] : null)  
	                .build();
	        refList.add(dto);
	    }
	    
	    return refList;
	}
	
	// 서명 리스트 출력
	public List<MySignDto> signList(String empId){
		List<MySign> mySignList = mySignRepository.mySignfindAllByEmpid(empId);
		
		List<MySignDto> mDto = new ArrayList<>();
		for (MySign signDto : mySignList) {
			MySignDto mySignDto = new MySignDto().toDto(signDto);
			mDto.add(mySignDto);
		}
		return mDto;
	}
	
	// 보고서 상세
	public Map<String , Object> getDocuDetail(Long approNo){
		Map<String, Object> detailMap = new HashMap<>();

	    // Approve 객체 가져오기
	    Approve approve = approveRepository.findByApproNo(approNo);

	    // ApproveLine 가져오기
	    List<ApproveLine> approLineList = approveLineRepository.findByApprove(approve);

	    // 결재자와 협의자를 구분하여 리스트로 할당
	    List<ApproveLineDto> approveLineList = approLineList.stream()
	        .filter(a -> "N".equals(a.getConsultYn())) // 결재자만 필터링
	        .map(a -> ApproveLineDto.builder()
	            .emp_id(a.getEmployee().getEmpId())
	            .appro_line_name(a.getApproLineName())
	            .appro_line_order(a.getApproLineOrder())
	            .appro_line_status(a.getApproLineStatus())
	            .appro_permit_time(a.getApproPermitTime())
	            .reason_back(a.getReasonBack())
	            .consult_yn(a.getConsultYn()) 
	            .appro_line_sign(a.getApproLineSign())
	            .build())
	        .collect(Collectors.toList());

	    List<ApproveLineDto> consultLineList = approLineList.stream()
	        .filter(a -> "Y".equals(a.getConsultYn())) // 협의자만 필터링
	        .map(a -> ApproveLineDto.builder()
	            .emp_id(a.getEmployee().getEmpId())
	            .appro_line_name(a.getApproLineName())
	            .appro_line_order(a.getApproLineOrder())
	            .appro_line_status(a.getApproLineStatus())
	            .appro_permit_time(a.getApproPermitTime())
	            .reason_back(a.getReasonBack())
	            .consult_yn(a.getConsultYn())
	            .appro_line_sign(a.getApproLineSign())
	            .build())
	        .collect(Collectors.toList());

	    detailMap.put("lDto", approveLineList); // 결재자 리스트
	    detailMap.put("cDto", consultLineList); // 협의자 리스트
	    
	    // ApproFile 조회
	    ApproFile aFile = approFileRepository.findByApprove(approve);
	    ApproFileDto fileDto = null;
	    if(aFile != null) {
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
	
	public Map<String, Object> getApproveDetail(Long approNo) {
	    Map<String, Object> detailMap = new HashMap<>();

	    // Approve 객체 가져오기
	    Approve approve = approveRepository.findByApproNo(approNo);

	    // ApproveLine 가져오기
	    List<ApproveLine> approLineList = approveLineRepository.findByApprove(approve);

	    // 결재자와 협의자를 구분하여 리스트로 할당
	    List<ApproveLineDto> approveLineList = approLineList.stream()
	        .filter(a -> "N".equals(a.getConsultYn())) // 결재자만 필터링
	        .map(a -> ApproveLineDto.builder()
	            .emp_id(a.getEmployee().getEmpId())
	            .appro_line_name(a.getApproLineName())
	            .appro_line_order(a.getApproLineOrder())
	            .appro_line_status(a.getApproLineStatus())
	            .appro_permit_time(a.getApproPermitTime())
	            .reason_back(a.getReasonBack())
	            .consult_yn(a.getConsultYn()) 
	            .appro_line_sign(a.getApproLineSign())
	            .build())
	        .collect(Collectors.toList());

	    List<ApproveLineDto> consultLineList = approLineList.stream()
	        .filter(a -> "Y".equals(a.getConsultYn())) // 협의자만 필터링
	        .map(a -> ApproveLineDto.builder()
	            .emp_id(a.getEmployee().getEmpId())
	            .appro_line_name(a.getApproLineName())
	            .appro_line_order(a.getApproLineOrder())
	            .appro_line_status(a.getApproLineStatus())
	            .appro_permit_time(a.getApproPermitTime())
	            .reason_back(a.getReasonBack())
	            .consult_yn(a.getConsultYn())
	            .appro_line_sign(a.getApproLineSign())
	            .build())
	        .collect(Collectors.toList());

	    detailMap.put("lDto", approveLineList); // 결재자 리스트
	    detailMap.put("cDto", consultLineList); // 협의자 리스트
	    
	    // 3. Reference
	    List<Reference> reference = referenceRepository.findByApprove(approve);
	    List<ReferenceDto> refList = reference.stream()
	        .map(r -> ReferenceDto.builder()
	            .emp_id(r.getEmployee().getEmpId())
	            .ref_name(r.getRefName())
	            .build())
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
	    if(aFile != null) {
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
	public int docuUpdate(ApproveDto approveDto, List<ApproveLineDto> approveLineDtos, ApproFileDto approFileDto, MultipartFile file) {
		Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
	    Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
	    Job job = jobRepository.findByJobCode(approveDto.getJob_code());
		TempEdit tempEdit = tempEditRepository.findByTempNo(approveDto.getTemp_no());
	    
		    Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
		    approve = approveRepository.save(approve);
		    
		    approveLineRepository.deleteByApprove(approve);
            for (ApproveLineDto lineDto : approveLineDtos) {
                lineDto.setAppro_no(approve.getApproNo());

                // 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
                Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
                if (lineEmployee == null) {
                    continue; // 또는 오류를 처리합니다.
                }

                ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
                approveLineRepository.save(approveLine);
            }
            
            if (approFileDto != null && approFileDto.getOri_file() != null && !approFileDto.getOri_file().isEmpty()) { 
                approFileDto.setAppro_no(approveDto.getAppro_no());
                ApproFile approFile = approFileDto.toEntity(approve);
                approFileRepository.save(approFile);
            }
            
            return 1;
	}
	
	// 결재 수정
	@Transactional
	public int approUpdate(ApproveDto approveDto, List<VacationRequestDto> vacationRequestDtos, List<ApproveLineDto> approveLineDtos,
			List<ReferenceDto> referenceDto , ApproFileDto approFileDto, MultipartFile file) {
		
		Employee employee = employeeRepository.findByempId(approveDto.getEmp_id());
	    Department department = departmentRepository.findByDeptCode(approveDto.getDept_code());
	    Job job = jobRepository.findByJobCode(approveDto.getJob_code());
		
	    TempEdit tempEdit = null;
	    if (approveDto.getTemp_no() != null) {  // temp_no가 null이 아닐 때만 findById를 호출합니다.
	        tempEdit = tempEditRepository.findById(approveDto.getTemp_no()).orElse(null);
	    }
	    
			// 1. approve 테이블에 저장
		    Approve approve = approveDto.toEntity(employee, department, job, tempEdit);
		    approve = approveRepository.save(approve);

		    // 2. vacation_request 테이블에 저장
		    VacationRequest oriVacRequest = vacationRequestRepository.findByApprove(approve);
		    if(oriVacRequest != null) {
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
            for (ApproveLineDto lineDto : approveLineDtos) {
                lineDto.setAppro_no(approve.getApproNo());

                // 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
                Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
                if (lineEmployee == null) {
                    continue; // 또는 오류를 처리합니다.
                }

                ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
                approveLineRepository.save(approveLine);
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
            if ((oriVacDto.getVac_type() == 0 || oriVacDto.getVac_type() == 3 || oriVacDto.getVac_type() == 6 ||
                 oriVacDto.getVac_type() == 7 ) &&
                (oriVacDto.getVac_type() != newVacDto.getVac_type() ||
                 !oriVacDto.getStart_time().equals(newVacDto.getStart_time()) ||
                 !oriVacDto.getEnd_time().equals(newVacDto.getEnd_time()))) {

                // 변경 전 시간 복구
                vacHours += minusVac(oriVacDto);
            }

            // 새로운 휴가 유형에 대해 차감 로직을 수행
            if (newVacDto.getVac_type() == 0 || newVacDto.getVac_type() == 3 || newVacDto.getVac_type() == 6 ||
                newVacDto.getVac_type() == 7 ) {

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
	    
	    for (ApproveLineDto lineDto : approveLineDtos) {
	        lineDto.setAppro_no(approNo); // Approve의 appro_no 설정
	        
	        // 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
	        Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
	        if (lineEmployee == null) {
	            continue; // 또는 오류를 처리합니다.
	        }

	        ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
	        approveLineRepository.save(approveLine);
	    }
	    
	    if (approFileDto != null) { // approFileDto가 null이 아닐 때만 저장
	        approFileDto.setAppro_no(approNo); // Approve의 appro_no 설정
	        ApproFile approFile = approFileDto.toEntity(approve);
	        approFileRepository.save(approFile);
	    }
	    
	    return 1;
		
	}
	
	// 결재 요청 
	@Transactional
	public int approUp(ApproveDto approveDto, VacationRequestDto vacationRequestDto, List<ApproveLineDto> approveLineDtos,
	                   List<ReferenceDto> referenceDto, ApproFileDto approFileDto, MultipartFile file) {

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
	    
	    
	 // 3. approve_Line 테이블에 저장
	    for (ApproveLineDto lineDto : approveLineDtos) {
	        lineDto.setAppro_no(approNo); // Approve의 appro_no 설정

	        // 각 ApproveLine에 맞는 Employee 객체를 가져옵니다.
	        Employee lineEmployee = employeeRepository.findByempId(lineDto.getEmp_id());
	        if (lineEmployee == null) {
	            continue; // 또는 오류를 처리합니다.
	        }

	        ApproveLine approveLine = lineDto.toEntity(approve, lineEmployee);
	        approveLineRepository.save(approveLine);
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
	        referenceRepository.save(reference);
	    }

	    // 5. appro_file 테이블에 저장
	    if (approFileDto != null) { // approFileDto가 null이 아닐 때만 저장
	        approFileDto.setAppro_no(approNo); // Approve의 appro_no 설정
	        ApproFile approFile = approFileDto.toEntity(approve);
	        approFileRepository.save(approFile);
	    }

	    // 6. 휴가 시간 차감 로직 추가
	    int vacationType = vacationRequestDto.getVac_type();
	    if (vacationType == 0 || vacationType == 3 || vacationType == 6 || vacationType == 7 ) {
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

	
	// 휴가 시간계산 메서드
	private int minusVac(VacationRequestDto vacationRequestDto) {
	    LocalDateTime startDate = vacationRequestDto.getStart_time();
	    LocalDateTime endDate = vacationRequestDto.getEnd_time();
	    int vacType = vacationRequestDto.getVac_type(); 
	    int hoursToDeduct = 0; 

	    // 두 날짜의 연도, 월, 일이 동일한 경우
	    if (startDate.toLocalDate().equals(endDate.toLocalDate())) {
	        // 시작과 끝의 시간이 동일하면 하루(8시간)
	        if (startDate.toLocalTime().equals(endDate.toLocalTime())) {
	            hoursToDeduct = 8;
	        } else {
	            // 시간이 다르면, 시간 차이만큼 차감
	            hoursToDeduct = endDate.getHour() - startDate.getHour();
	        }
	    } else {
	        // 날짜가 다를 경우 전체 일수 * 8 시간 계산
	        long daysBetween = java.time.Duration.between(startDate.toLocalDate().atStartOfDay(), endDate.toLocalDate().atStartOfDay()).toDays() + 1; 
	        hoursToDeduct = (int) (daysBetween * 8);
	    }

	    // 반차의 경우 차감 시간을 4로 설정
	    if (vacType == 6 || vacType == 7) {
	        hoursToDeduct = 4;
	    }

	    return hoursToDeduct;
	}

	public List<VacationRequestDto> selectApprovedVacationRequest(String empId) {
		List<VacationRequest> vacationRequestList = vacationRequestRepository.findByApprove_ApproStatusAndApprove_Employee_EmpId(1,empId);
		
		List<VacationRequestDto> vacationRequestDtoList = new ArrayList<VacationRequestDto>();
		for(VacationRequest vr : vacationRequestList) {
			VacationRequestDto dto = new VacationRequestDto().toDto(vr);
			vacationRequestDtoList.add(dto);
		}
		
		return vacationRequestDtoList;
	}

	public List<VacationRequestDto> selectAllApprovedVacationRequest() {
		List<VacationRequest> vacationRequestList = vacationRequestRepository.findByApprove_ApproStatus(1);
		
		List<VacationRequestDto> vacationRequestDtoList = new ArrayList<VacationRequestDto>();
		for(VacationRequest vr : vacationRequestList) {
			VacationRequestDto dto = new VacationRequestDto().toDto(vr);
			vacationRequestDtoList.add(dto);
		}
		
		return vacationRequestDtoList;
	}
	
	// 반려시 추가시간 메서드
	private int plusVac(int vacType, LocalDateTime startTime, LocalDateTime endTime) {
	    int hoursToAdd = 0; 

	    // 두 날짜의 연도, 월, 일이 동일한 경우
	    if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
	        // 시작과 끝의 시간이 동일하면 하루(8시간)
	        if (startTime.toLocalTime().equals(endTime.toLocalTime())) {
	            hoursToAdd = 8;
	        } else {
	            // 시간이 다르면, 시간 차이만큼 추가
	            hoursToAdd = endTime.getHour() - startTime.getHour();
	        }
	    } else {
	        // 날짜가 다를 경우 전체 일수 * 8 시간 계산
	        long daysBetween = java.time.Duration.between(startTime.toLocalDate().atStartOfDay(), endTime.toLocalDate().atStartOfDay()).toDays() + 1; 
	        hoursToAdd = (int) (daysBetween * 8);
	    }

	    // vacType이 6 또는 7일 경우 추가 시간을 4로 설정
	    if (vacType == 6 || vacType == 7) {
	        hoursToAdd = 4;
	    }

	    return hoursToAdd;
	}
}
