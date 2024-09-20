package com.dbdevdeep.approve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.repository.ApproDraftRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;


@Service
public class ApproDraftService {

	private final ApproDraftRepository approDraftRepository;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public ApproDraftService(ApproDraftRepository approDraftRepository,
			EmployeeRepository employeeRepository) {
		this.approDraftRepository = approDraftRepository;
		this.employeeRepository = employeeRepository;
	}

	public ApproDraft saveDraft(ApproDraftDto dto) {
		
		Employee emp = employeeRepository.findByempId(dto.getEmp_id());
				
		LocalDateTime startDateTime = parseToLocalDateTime(dto.getStart_time().toString());
        LocalDateTime endDateTime = parseToLocalDateTime(dto.getEnd_time().toString());
		
		ApproDraft aDraft = ApproDraft.builder()
				.employee(emp)
				.tempEdit(null)
				.approType(0)
				.approTitle(dto.getAppro_title())
				.approContent(dto.getAppro_content())
				.oriFile(dto.getOri_file())
				.newFile(dto.getNew_file())
				.fileRoot(null)
				.vacType(dto.getVac_type())
				.startTime(startDateTime)
				.endTime(endDateTime)
				.consultDraftRoot(dto.getConsult_draft_root())
				.approvalDraftRoot(dto.getApproval_draft_root())
				.refDraftRoot(dto.getRef_draft_root())
				.build();
		
		return approDraftRepository.save(aDraft);
	}
	
	private LocalDateTime parseToLocalDateTime(String dateString) {
        try {
            // "datetime-local" 형식으로 파싱 시도
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            return LocalDateTime.parse(dateString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateString, dateFormatter);
                return date.atStartOfDay(); // 날짜를 하루의 시작 시간으로 변환
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("유효하지 않은 날짜 형식입니다: " + dateString);
            }
        }
    }
}
