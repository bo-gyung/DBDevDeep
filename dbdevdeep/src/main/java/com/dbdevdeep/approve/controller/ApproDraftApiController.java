package com.dbdevdeep.approve.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.service.ApproDraftService;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Controller
public class ApproDraftApiController {

	private final ApproDraftService approDraftService;
	private final FileService fileService;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public ApproDraftApiController(ApproDraftService approDraftService, EmployeeRepository employeeRepository , FileService fileService) {
		this.approDraftService = approDraftService;
		this.fileService = fileService;
		this.employeeRepository = employeeRepository;
	}
	
	
	@ResponseBody
	@PostMapping("/draftApprove")
	public Map<String, String> draftApprove(@RequestParam("appro_content") String approContent, @RequestParam("appro_title") String approTitle, @RequestParam("emp_id") String empId, @RequestParam("consult") String consult,
		    @RequestParam("approval") String approval, @RequestParam(value = "file_name") MultipartFile file) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "게시글 등록중 오류가 발생했습니다.");

	    Employee employee = employeeRepository.findByempId(empId);
	    ApproDraftDto dto = new ApproDraftDto();
	    
	    dto.setEmp_id(empId);
	    dto.setAppro_title(approTitle);
	    dto.setAppro_content(approContent);
	    dto.setConsult_draft_root(consult);
	    dto.setApproval_draft_root(approval);
	    
	    try {
	        // 파일이 있을 경우에만 업로드 처리
	        if (file != null && !file.isEmpty()) {
	            String savedFileName = fileService.approveUpload(file);
	            if (savedFileName != null) {
	                dto.setOri_file(file.getOriginalFilename());
	                dto.setNew_file(savedFileName);
	            } else {
	                resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
	                return resultMap;
	            }
	        }
	        
	        if (approDraftService.saveDraft(dto) != null) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "게시글이 성공적으로 등록되었습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "서버 오류 발생: " + e.getMessage());
	    }

	    return resultMap;
	}

}
