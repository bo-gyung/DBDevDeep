package com.dbdevdeep.approve.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.alert.config.AlertMessageHandler;
import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.alert.domain.AlertDto;
import com.dbdevdeep.alert.repository.AlertRepository;
import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.domain.ApproFile;
import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.domain.ApproveDto;
import com.dbdevdeep.approve.domain.ApproveLine;
import com.dbdevdeep.approve.domain.ApproveLineDto;
import com.dbdevdeep.approve.domain.TempEdit;
import com.dbdevdeep.approve.repository.ApproDraftRepository;
import com.dbdevdeep.approve.repository.ApproFileRepository;
import com.dbdevdeep.approve.repository.ApproveLineRepository;
import com.dbdevdeep.approve.repository.ApproveRepository;
import com.dbdevdeep.approve.repository.TempEditRepository;
import com.dbdevdeep.employee.domain.Department;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.Job;
import com.dbdevdeep.employee.repository.DepartmentRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.JobRepository;
import com.dbdevdeep.websocket.config.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;


@Service
public class ApproDraftService {

	private final ApproDraftRepository approDraftRepository;
	private final EmployeeRepository employeeRepository;
	private final TempEditRepository tempEditRepository;
	private final DepartmentRepository departmentRepository;
	private final JobRepository jobRepository;
	private final ApproveRepository approveRepository;
	private final ApproveLineRepository approveLineRepository;
	private final ApproFileRepository approFileRepository;
	private final AlertRepository alertRepository;
	private final ObjectMapper objectMapper;
	private final WebSocketHandler webSocketHandler;
	private final AlertMessageHandler alertMessageHandler;
	
	@Autowired
	public ApproDraftService(ApproDraftRepository approDraftRepository,
			EmployeeRepository employeeRepository, TempEditRepository tempEditRepository,
			DepartmentRepository departmentRepository, JobRepository jobRepository,
			ApproveRepository approveRepository, ApproveLineRepository approveLineRepository,
			ApproFileRepository approFileRepository, AlertRepository alertRepository,
			ObjectMapper objectMapper, WebSocketHandler webSocketHandler,
			AlertMessageHandler alertMessageHandler) {
		this.approDraftRepository = approDraftRepository;
		this.employeeRepository = employeeRepository;
		this.tempEditRepository = tempEditRepository;
		this.departmentRepository = departmentRepository;
		this.jobRepository = jobRepository;
		this.approveRepository = approveRepository;
		this.approveLineRepository = approveLineRepository;
		this.approFileRepository = approFileRepository;
		this.alertRepository = alertRepository;
		this.objectMapper = objectMapper;
		this.webSocketHandler = webSocketHandler;
		this.alertMessageHandler = alertMessageHandler;
	}

	// 보고서 임시 저장
	public ApproDraft saveDraft(ApproDraftDto dto, Long draftNo) {
	    Employee employee = employeeRepository.findByempId(dto.getEmp_id());
	    ApproDraft aDraft = null;
	    
	    if(draftNo != null) {
	    	aDraft = ApproDraft.builder()
	    			.employee(employee)
	    			.draftNo(draftNo)
	    			.approTime(null)
	    			.approTitle(dto.getAppro_title())
	    			.approContent(dto.getAppro_content())
	    			.oriFile(dto.getOri_file())
	    			.newFile(dto.getNew_file())
	    			.consultDraftRoot(dto.getConsult_draft_root())
	    			.approvalDraftRoot(dto.getApproval_draft_root())
	    			.build();
	    }else {
	    	aDraft = ApproDraft.builder()
	    			.employee(employee)
	    			.approTime(null)
	    			.approTitle(dto.getAppro_title())
	    			.approContent(dto.getAppro_content())
	    			.oriFile(dto.getOri_file())
	    			.newFile(dto.getNew_file())
	    			.consultDraftRoot(dto.getConsult_draft_root())
	    			.approvalDraftRoot(dto.getApproval_draft_root())
	    			.build();
	    }

	    return approDraftRepository.save(aDraft);
	}

	// 보고서 임시 리스트 출력
	public  List<ApproDraftDto> userDraftList(String empId){
		List<ApproDraft> approDraftList = null;
		Employee employee = employeeRepository.findByempId(empId);
		approDraftList = approDraftRepository.findByDraftList(employee);
		
		List<ApproDraftDto> approDraftDtoList = new ArrayList<ApproDraftDto>();
		for(ApproDraft a : approDraftList) {
			ApproDraftDto dto = new ApproDraftDto().toDto(a);
			approDraftDtoList.add(dto);
		}
		return approDraftDtoList;
	}
	
	// 보고서 임시 상세
	public ApproDraftDto selectDraftOne(Long draftNo) {
		ApproDraft ad = approDraftRepository.findByDraftNo(draftNo);
		ApproDraftDto aDto = ApproDraftDto.builder()
				.draft_no(ad.getDraftNo())
				.appro_time(ad.getApproTime())
				.appro_title(ad.getApproTitle())
				.appro_content(ad.getApproContent())
				.ori_file(ad.getOriFile())
				.new_file(ad.getNewFile())
				.consult_draft_root(ad.getConsultDraftRoot())
				.approval_draft_root(ad.getApprovalDraftRoot())
				.build();
		return aDto;
	}

	// 임시 보고서 삭제
	public int deleteDocuDraft(Long draft_no) {
		int result = 0;
		try {
			approDraftRepository.deleteById(draft_no);
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 임시 보고서 결재 요청 + 임시 삭제 docuDraftUp
	@Transactional
	public int docuDraftUp(String draftNo, ApproveDto approveDto, List<ApproveLineDto> approveLineDtos, ApproFileDto approFileDto) {
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
		
		Long dNo = Long.parseLong(draftNo);
		approDraftRepository.deleteById(dNo);

		return 1;

	}
}
