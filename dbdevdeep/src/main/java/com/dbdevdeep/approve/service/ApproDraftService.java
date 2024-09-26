package com.dbdevdeep.approve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.alert.domain.AlertDto;
import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.domain.TempEdit;
import com.dbdevdeep.approve.repository.ApproDraftRepository;
import com.dbdevdeep.approve.repository.TempEditRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;


@Service
public class ApproDraftService {

	private final ApproDraftRepository approDraftRepository;
	private final EmployeeRepository employeeRepository;
	private final TempEditRepository tempEditRepository;
	
	@Autowired
	public ApproDraftService(ApproDraftRepository approDraftRepository,
			EmployeeRepository employeeRepository, TempEditRepository tempEditRepository) {
		this.approDraftRepository = approDraftRepository;
		this.employeeRepository = employeeRepository;
		this.tempEditRepository = tempEditRepository;
	}

	// 보고서 임시 저장
	public ApproDraft saveDraft(ApproDraftDto dto) {
	    Employee employee = employeeRepository.findByempId(dto.getEmp_id());

	    ApproDraft aDraft = ApproDraft.builder()
	            .employee(employee)
	            .approTime(null)
	            .approTitle(dto.getAppro_title())
	            .approContent(dto.getAppro_content())
	            .oriFile(dto.getOri_file())
	            .newFile(dto.getNew_file())
	            .consultDraftRoot(dto.getConsult_draft_root())
	            .approvalDraftRoot(dto.getApproval_draft_root())
	            .build();

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

	// 보고서 삭제
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
}
