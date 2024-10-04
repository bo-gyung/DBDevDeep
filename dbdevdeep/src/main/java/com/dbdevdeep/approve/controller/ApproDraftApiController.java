package com.dbdevdeep.approve.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.domain.ApproveDto;
import com.dbdevdeep.approve.domain.ApproveLineDto;
import com.dbdevdeep.approve.service.ApproDraftService;
import com.dbdevdeep.approve.service.ApproveService;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Controller
public class ApproDraftApiController {

	private final ApproDraftService approDraftService;
	private final FileService fileService;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public ApproDraftApiController(ApproDraftService approDraftService, EmployeeRepository employeeRepository ,
			FileService fileService) {
		this.approDraftService = approDraftService;
		this.fileService = fileService;
		this.employeeRepository = employeeRepository;
	}
	
	// 직원 아이디 추출
		private String pullId(String input) {
		    return input.substring(input.indexOf('(') + 1, input.indexOf(')'));
		}
		// 직원 직급과 이름 추출
		private String pullName(String input) {
		    return input.substring(0, input.indexOf('(')).trim();
		}
	
	
	@ResponseBody
	@PostMapping("/draftApprove")
	public Map<String, String> draftApprove(@RequestParam(value= "draft_no", required = false) Long draftNo
			,@RequestParam("appro_content") String approContent, @RequestParam("appro_title") String approTitle, @RequestParam("emp_id") String empId, @RequestParam("consult") String consult,
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
	        
	        ApproDraft savedDraft = approDraftService.saveDraft(dto, draftNo);
	        
	        if (savedDraft != null) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "임시 보관함에 등록되었습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "서버 오류 발생: " + e.getMessage());
	    }

	    return resultMap;
	}
	
	// 임시 삭제
	@ResponseBody
	@DeleteMapping("/draftAppro/{draft_no}")
	public Map<String, String> deleteDocuAppro(@PathVariable("draft_no") Long draft_no){
		Map<String, String> map = new HashMap<>();
		map.put("res_code", "404");
		map.put("res_msg", "삭제중 오류가 발생하였습니다.");
		
		int fileDeleteResult = fileService.approDraftFileDelete(draft_no);
		
		if(fileDeleteResult >=0 ) {
			if(approDraftService.deleteDocuDraft(draft_no) > 0) {
				map.put("res_code", "200");
				map.put("res_msg", "삭제되었습니다.");
			}else {
				map.put("res_msg", "삭제중 오류 발생 하였습니다.");
			}
		} else {
			map.put("res_msg", "파일 삭제중 오류가 발생하였습니다.");
		}
		return map;
	}

	// 임시 결재 요청 후 삭제 
	@ResponseBody
	@PostMapping("/draftApproUp")
	public Map<String,String> docuApproUp(
			@RequestParam("draft_no") String draftNo,
			@RequestParam("emp_name") String approName,
			@RequestParam("emp_id") String empId,
		    @RequestParam("dept_code") String deptCode,
		    @RequestParam("job_code") String jobCode,
		    @RequestParam("appro_title") String approTitle,
		    @RequestParam("tempNo") String tempNo,
		    @RequestParam("appro_content") String approContent,
			@RequestParam("consult") String consult,
		    @RequestParam("approval") String approval,
		    @RequestParam("file_name") MultipartFile file){
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "결재 요청 중 오류가 발생하였습니다.");
		
		try {
	        Long templateNo = null;
	        if (tempNo != null && !tempNo.trim().isEmpty()) {
	            templateNo = Long.parseLong(tempNo);
	        }
			
			ApproveDto approveDto = new ApproveDto();
			approveDto.setEmp_id(empId);
			approveDto.setTemp_no(templateNo);
			approveDto.setDept_code(deptCode);
			approveDto.setJob_code(jobCode);
			approveDto.setAppro_name(approName);
			approveDto.setAppro_type(1);
			approveDto.setAppro_title(approTitle);
			approveDto.setAppro_content(approContent);
			
			
			// approve_line 설정 
	        List<ApproveLineDto> approveLineDtos = new ArrayList<>();
	        LocalDateTime currentTime = LocalDateTime.now();
	        int order = 1;
	        boolean firstSet = false;

	        // 협의자 처리
	        if (consult != null && !consult.isEmpty()) {
	            String[] consults = consult.split(">");
	            for (String c : consults) {
	                String consultId = pullId(c); // 협의자 ID 추출
	                String consultName = pullName(c); // 협의자 이름 추출
	                int status = firstSet ? 0 : 1;
	                firstSet = true;

	                ApproveLineDto approveLineDto = new ApproveLineDto();
	                approveLineDto.setEmp_id(consultId); // 올바르게 협의자 ID 설정
	                approveLineDto.setAppro_line_name(consultName); // 협의자 이름 설정
	                approveLineDto.setAppro_line_order(order++);
	                approveLineDto.setAppro_line_status(status);
	                approveLineDto.setAppro_permit_time(currentTime);
	                approveLineDto.setConsult_yn("Y"); // 협의 여부 설정

	                approveLineDtos.add(approveLineDto);
	            }
	        }

	        // 결재자 처리
	        if (approval != null && !approval.isEmpty()) {
	            String[] approvals = approval.split(">");
	            for (String a : approvals) {
	                String approvalId = pullId(a); // 결재자 ID 추출
	                String approvalName = pullName(a); // 결재자 이름 추출
	                int status = firstSet ? 0 : 1;
	                firstSet = true;

	                ApproveLineDto approveLineDto = new ApproveLineDto();
	                approveLineDto.setEmp_id(approvalId); // 올바르게 결재자 ID 설정
	                approveLineDto.setAppro_line_name(approvalName); // 결재자 이름 설정
	                approveLineDto.setAppro_line_order(order++);
	                approveLineDto.setAppro_line_status(status);
	                approveLineDto.setAppro_permit_time(currentTime);
	                approveLineDto.setConsult_yn("N"); // 결재 여부 설정

	                approveLineDtos.add(approveLineDto);
	            }
	        }
	        
	     // 파일 업로드 설정
	     			ApproFileDto approFileDto = null; // 파일이 없는 경우 null로 설정 
	     			if (file != null && !file.isEmpty()) {
	     			    // 파일 저장 및 정보 설정
	     			    String savedFileName = fileService.approveUpload(file);
	     			    if (savedFileName != null) {
	     			        approFileDto = new ApproFileDto(); // 파일이 있을 경우에만 객체 생성
	     			        approFileDto.setNew_file(savedFileName);
	     			        approFileDto.setOri_file(file.getOriginalFilename());
	     			    } else {
	     			        resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
	     			        return resultMap; 
	     			    }
	     			}
	     			
	     			int result = approDraftService.docuDraftUp(draftNo, approveDto, approveLineDtos, approFileDto);
	    			
	    			if(result > 0) {
	    				resultMap.put("res_code", "200");
	    				resultMap.put("res_msg", "결재요청 하였습니다.");
	    			}
			
			
		}catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "서버 내부 오류: " + e.getMessage());
	    }
		
		return resultMap;
	}
}
