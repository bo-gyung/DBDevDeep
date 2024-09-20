package com.dbdevdeep.approve.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.domain.ApproveDto;
import com.dbdevdeep.approve.domain.ApproveLineDto;
import com.dbdevdeep.approve.domain.ReferenceDto;
import com.dbdevdeep.approve.domain.VacationRequestDto;
import com.dbdevdeep.approve.service.ApproveLineService;
import com.dbdevdeep.approve.service.ApproveService;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Controller
public class ApproveApiController {

	private final ApproveService approveService;
	private final FileService fileService;
	private final ApproveLineService approveLineService;
	private final EmployeeRepository employeeRepository;
	
	// 직원 아이디 추출
	private String pullId(String input) {
	    return input.substring(input.indexOf('(') + 1, input.indexOf(')'));
	}
	// 직원 직급과 이름 추출
	private String pullName(String input) {
	    return input.substring(0, input.indexOf('(')).trim();
	}
	
	// String 시간 변환 메서드
	private LocalDateTime changeTime(String dateStr) {
	    try {
	        // datetime-local 형식으로 파싱 시도
	        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	        return LocalDateTime.parse(dateStr, dateTimeFormatter);
	    } catch (DateTimeParseException e) {
	        // datetime-local 형식이 아니면 date 형식으로 파싱
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);
	        return localDate.atStartOfDay();
	    }
	}
	
	
	@Autowired
	public ApproveApiController(ApproveService approveService, FileService fileService, ApproveLineService approveLineService ,EmployeeRepository employeeRepository) {
		this.approveService = approveService;
		this.fileService = fileService;
		this.approveLineService = approveLineService;
		this.employeeRepository =employeeRepository;
	}
	
	// 보고서 삭제
	@ResponseBody
	@DeleteMapping("/docuAppro/{appro_no}")
	public Map<String, String> deleteDocuAppro(@PathVariable("appro_no") Long appro_no){
		Map<String, String> map = new HashMap<>();
		map.put("res_code", "404");
		map.put("res_msg", "삭제중 오류가 발생하였습니다.");
		
		int fileDeleteResult = fileService.approFileDelete(appro_no);
		
		if(fileDeleteResult >=0 ) {
			if(approveService.deleteDocuApprove(appro_no) > 0) {
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
	
	// 결재 삭제
	@ResponseBody
	@DeleteMapping("/appro/{appro_no}")
	public Map<String, String> deleteAppro(@PathVariable("appro_no") Long appro_no) {
	    Map<String, String> map = new HashMap<>();
	    map.put("res_code", "404");
	    map.put("res_msg", "삭제중 오류가 발생하였습니다.");

	    int fileDeleteResult = fileService.approFileDelete(appro_no);

	    if (fileDeleteResult >= 0) { 
	        if (approveService.deleteApprove(appro_no) > 0) {
	            map.put("res_code", "200");
	            map.put("res_msg", "정상적으로 삭제되었습니다.");
	        } else {
	            map.put("res_msg", "삭제중 오류 발생 하였습니다.");
	        }
	    } else {
	        map.put("res_msg", "파일 삭제 중 오류가 발생하였습니다.");
	    }
	    return map;
	}
	
	// 승인 처리
	@ResponseBody
	@PostMapping("/agreeApprove")
	public Map<String,String> agreeApprove(@RequestBody Map<String, Object> requestData){
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "승인 처리 중 오류가 발생했습니다.");
		
		try {
			Long approNo = Long.valueOf((String) requestData.get("approNo"));
	        String empId = (String) requestData.get("empId");
	        String principalId = (String) requestData.get("principalId");
	        String deptCode = (String) requestData.get("deptCode");
	        String jobCode = (String) requestData.get("jobCode");
			String signImage = (String) requestData.get("signImage");
			
			int result = approveService.agreeApproveLine(approNo, empId, principalId , deptCode, jobCode, signImage);

	        if (result > 0) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "승인 처리되었습니다.");
	        } else {
	            resultMap.put("res_msg", "승인 처리가 실패하였습니다.");
	        }
	        
		}catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "승인처리 중 오류가 발생했습니다.");
	    }

	    return resultMap;
	}
	
	// 반려 처리
	@ResponseBody
	@PostMapping("/backApprove")
	public Map<String, String> backApprove(@RequestBody Map<String, Object> requestData) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "반려 처리 중 오류가 발생했습니다.");

	    try {
	        // 요청 데이터 파싱
	        Long approNo = Long.valueOf((String) requestData.get("approNo"));
	        String empId = (String) requestData.get("empId");
	        String principalId = (String) requestData.get("principalId");
	        String deptCode = (String) requestData.get("deptCode");
	        String jobCode = (String) requestData.get("jobCode");
	        int vacType = Integer.parseInt((String) requestData.get("vacType"));
	        LocalDateTime startDate = changeTime((String) requestData.get("startDate"));
	        LocalDateTime endDate = changeTime((String) requestData.get("endDate"));
	        String reasonBack = (String) requestData.get("reasonBack");

	        // ApproveLineDto 생성
	        ApproveLineDto approveLineDto = new ApproveLineDto();
	        approveLineDto.setAppro_no(approNo);
	        approveLineDto.setEmp_id(principalId);
	        approveLineDto.setReason_back(reasonBack);

	        // 서비스 호출
	        int result = approveService.backApproveLine(approveLineDto, empId, vacType, startDate, endDate, deptCode, jobCode);

	        if (result > 0) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "반려 처리되었습니다.");
	        } else {
	            resultMap.put("res_msg", "반려 처리가 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
	    }

	    return resultMap;
	}
	
	// 보고서 수정
	@ResponseBody
	@PostMapping("/docuReUp/{appro_no}")
	public Map<String,String> updateDocuApprove(
			@RequestParam("appro_no") Long approNo,
			@RequestParam("appro_name") String approName,
			@RequestParam("emp_id") String empId,
			@RequestParam("dept_code") String deptCode,
			@RequestParam("job_code") String jobCode,
			@RequestParam("appro_title") String approTitle,
			@RequestParam("tempNo") String tempNo,
			@RequestParam("appro_content") String approContent,
			@RequestParam("consult") String consult,
			@RequestParam("approval") String approval,
			@RequestParam(name="file_name", required=false)MultipartFile file){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 수정중 오류가 발생했습니다.");
		Long tempNoN = (tempNo != null && !tempNo.isEmpty()) ? Long.parseLong(tempNo) : 0L ;
		
		ApproFileDto approFileDto = null;
		if (file != null && !file.isEmpty()) { 
		    approFileDto = new ApproFileDto();
		    String savedFileName = fileService.approveUpload(file);
		    if (savedFileName != null) {
		        approFileDto.setOri_file(file.getOriginalFilename());
		        approFileDto.setNew_file(savedFileName);
		        if (fileService.approFileDelete(approNo) > 0) {
		            resultMap.put("res_msg", "기존 파일이 정상적으로 삭제되었습니다.");
		        }
		    } else {
		        resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		    }
		}
		try {
			ApproveDto approveDto = new ApproveDto();
			approveDto.setAppro_no(approNo);
			approveDto.setEmp_id(empId);
			approveDto.setDept_code(deptCode);
			approveDto.setJob_code(jobCode);
			approveDto.setTemp_no(tempNoN);
			approveDto.setAppro_name(approName);
			approveDto.setAppro_type(1);
			approveDto.setAppro_title(approTitle);
			approveDto.setAppro_content(approContent);
			
			List<ApproveLineDto> approveLineDtos = new ArrayList<>();
			LocalDateTime currentTime = LocalDateTime.now();
			int order = 1;
			boolean firstSet = false;
			
			if(consult !=null && !consult.isEmpty()) {
				String[] consults = consult.split(">");
				for(String c : consults) {
					String consultId = pullId(c);
					String consultName = pullName(c);
					int status = firstSet ? 0 : 1;
					firstSet = true;
					approveLineDtos.add(new ApproveLineDto(null, null, consultId, consultName ,order++, status, currentTime ,null, "Y" , null));
				}
			}
			if(approval != null && !approval.isEmpty()) {
				String[] approvals = approval.split(">");
				for(String a : approvals) {
					String approvalId = pullId(a);
					String approvalName = pullName(a);
					int status = firstSet ? 0 : 1;
					firstSet = true;
					approveLineDtos.add(new ApproveLineDto(null, null, approvalId , approvalName ,order++ , status , currentTime ,null, "N" , null));
				}
			}
			
			int result = approveService.docuUpdate(approveDto, approveLineDtos, approFileDto, file);
			
			if(result > 0) {
				resultMap.put("res_code","200");
				resultMap.put("res_msg", "게시글이 성공적으로 수정되었습니다.");
			}
		} catch (Exception e) {
	        e.printStackTrace();  // 서버 로그에 오류 출력
	        resultMap.put("res_msg", "서버 내부 오류: " + e.getMessage());
	    }
		
		return resultMap;
	}
	
	// 결재 수정
	@ResponseBody
	@PostMapping("/approReUp/{appro_no}")
	public Map<String,String> updateApprove(
			@RequestParam("appro_no") Long approNo,
			@RequestParam("appro_name") String approName,
			@RequestParam("dept_code") String deptCode,
			@RequestParam("emp_id") String empId,
			@RequestParam("job_code") String jobCode,
			@RequestParam("appro_title") String approTitle,
			// 변경 전 휴가 종류
			@RequestParam("vac_type") int vacType,
			// 변경 후 휴가 종류
			@RequestParam("vac_type_display") int vacTypeDisplay ,
			// 수정된 일정
			@RequestParam("start_date") String startDate,
			@RequestParam("end_date") String endDate,
			// 수정전 일정
			@RequestParam("start_date_ori") String startDateOri,
			@RequestParam("end_date_ori") String endDateOri,
			@RequestParam("appro_content") String approContent,
			@RequestParam("consult") String consult,
			@RequestParam("approval") String approval,
			@RequestParam("reference") String reference,
			@RequestParam(name="file_name", required=false)MultipartFile file){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 수정중 오류가 발생했습니다.");
		
		ApproFileDto approFileDto = null;
		if (file != null && !file.isEmpty()) { 
		    approFileDto = new ApproFileDto();
		    String savedFileName = fileService.approveUpload(file);
		    if (savedFileName != null) {
		        approFileDto.setOri_file(file.getOriginalFilename());
		        approFileDto.setNew_file(savedFileName);
		        if (fileService.approFileDelete(approNo) > 0) {
		            resultMap.put("res_msg", "기존 파일이 정상적으로 삭제되었습니다.");
		        }
		    } else {
		        resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		    }
		}
		
		try {
			ApproveDto approveDto = new ApproveDto();
			approveDto.setAppro_no(approNo);
			approveDto.setEmp_id(empId);
			approveDto.setDept_code(deptCode);
			approveDto.setJob_code(jobCode);
			approveDto.setAppro_name(approName);
			approveDto.setAppro_title(approTitle);
			approveDto.setAppro_content(approContent);
			
			List<VacationRequestDto> vacationRequestDtos = new ArrayList<>();
			// 변경 전 휴가 타입과 시간
			VacationRequestDto prevVacationRequestDto = new VacationRequestDto();
		    prevVacationRequestDto.setVac_type(vacType);
		    prevVacationRequestDto.setStart_time(changeTime(startDateOri));
		    prevVacationRequestDto.setEnd_time(changeTime(endDateOri));
		    vacationRequestDtos.add(prevVacationRequestDto);

		    // 변경 후 휴가 타입과 시간
		    VacationRequestDto newVacationRequestDto = new VacationRequestDto();
		    newVacationRequestDto.setVac_type(vacTypeDisplay);
		    newVacationRequestDto.setStart_time(changeTime(startDate));
		    newVacationRequestDto.setEnd_time(changeTime(endDate));
		    vacationRequestDtos.add(newVacationRequestDto);

			List<ApproveLineDto> approveLineDtos = new ArrayList<>();
			LocalDateTime currentTime = LocalDateTime.now();
			int order = 1;
			boolean firstSet = false;
			
			if(consult !=null && !consult.isEmpty()) {
				String[] consults = consult.split(">");
				for(String c : consults) {
					String consultId = pullId(c);
					String consultName = pullName(c);
					int status = firstSet ? 0 : 1;
					firstSet = true;
					approveLineDtos.add(new ApproveLineDto(null, null, consultId, consultName ,order++, status, currentTime ,null, "Y" , null));
				}
			}
			if(approval != null && !approval.isEmpty()) {
				String[] approvals = approval.split(">");
				for(String a : approvals) {
					String approvalId = pullId(a);
					String approvalName = pullName(a);
					int status = firstSet ? 0 : 1;
					firstSet = true;
					approveLineDtos.add(new ApproveLineDto(null, null, approvalId , approvalName ,order++ , status , currentTime ,null, "N" , null));
				}
			}
			
			List<ReferenceDto> referenceDto = new ArrayList<>();
				if(reference != null && !reference.isEmpty()) {
					String[] references = reference.split(">");
					for(String r : references) {
						String refId = pullId(r);
						String refName = pullName(r);
						referenceDto.add(new ReferenceDto(null , null , refId , refName));
					}
				}
			
			int result = approveService.approUpdate(approveDto, vacationRequestDtos, approveLineDtos, referenceDto ,approFileDto, file);
			
			if(result > 0) {
				resultMap.put("res_code","200");
				resultMap.put("res_msg", "게시글이 성공적으로 수정되었습니다.");
			}
		} catch (Exception e) {
	        e.printStackTrace();  // 서버 로그에 오류 출력
	        resultMap.put("res_msg", "서버 내부 오류: " + e.getMessage());
	    }
		
		return resultMap;
	}
		
	
	// 보고서 결재 요청
	@ResponseBody
	@PostMapping("/docuApproUp")
	public Map<String,String> docuApproUp(
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
	     			
	     			int result = approveService.docuApproUp(approveDto, approveLineDtos, approFileDto);
	    			
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
		    
	// 결재 요청
	@ResponseBody
	@PostMapping("/approUp")
	public Map<String, String> approUp(
			@RequestParam("emp_name") String approName,
			@RequestParam("emp_id") String empId,
		    @RequestParam("dept_code") String deptCode,
		    @RequestParam("job_code") String jobCode,
		    @RequestParam("appro_title") String approTitle,
		    @RequestParam("appro_content") String approContent,
		    @RequestParam("vac_type") int vacType,
		    @RequestParam("start_date") String startDate,
		    @RequestParam("end_date") String endDate,
		    @RequestParam("consult") String consult,
		    @RequestParam("approval") String approval,
		    @RequestParam("reference") String reference,
		    @RequestParam("file_name") MultipartFile file) {
		
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "결재 요청 중 오류가 발생하였습니다.");
		
		try {
			
		ApproveDto approveDto = new ApproveDto();
			approveDto.setEmp_id(empId);
			approveDto.setDept_code(deptCode);
			approveDto.setJob_code(jobCode);
			approveDto.setAppro_name(approName);
			approveDto.setAppro_title(approTitle);
			approveDto.setAppro_content(approContent);
			
		VacationRequestDto vacationRequestDto = new VacationRequestDto();
			LocalDateTime startDateTime;
			LocalDateTime endDateTime;

			try {
				// datetime-local 형식으로 파싱 시도
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
				startDateTime = LocalDateTime.parse(startDate, dateTimeFormatter);
				endDateTime = LocalDateTime.parse(endDate, dateTimeFormatter);
			} catch (DateTimeParseException e) {
				// datetime-local 형식이 아니면 date 형식으로 파싱
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate startDateLocal = LocalDate.parse(startDate, dateFormatter);
				LocalDate endDateLocal = LocalDate.parse(endDate, dateFormatter);
				startDateTime = startDateLocal.atStartOfDay();
				endDateTime = endDateLocal.atStartOfDay();
			}

			vacationRequestDto.setVac_yn("Y");
			vacationRequestDto.setVac_type(vacType);
			vacationRequestDto.setStart_time(startDateTime);
			vacationRequestDto.setEnd_time(endDateTime);
			
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

	        // 참조자 처리
	        List<ReferenceDto> referenceDto = new ArrayList<>();

	        if (reference != null && !reference.isEmpty()) {
	            String[] references = reference.split(">");
	            for (String r : references) {
	                String refId = pullId(r); // 참조자 ID 추출
	                String refName = pullName(r); // 참조자 이름 추출

	                ReferenceDto refDto = new ReferenceDto();
	                refDto.setEmp_id(refId); // 참조자 ID 설정
	                refDto.setRef_name(refName); // 참조자 이름 설정

	                referenceDto.add(refDto);
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
		
		
			int result = approveService.approUp(approveDto, vacationRequestDto, approveLineDtos, referenceDto ,approFileDto, file);
			
			if(result > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "결재요청 하였습니다.");
			}
		} catch (Exception e) {
	        e.printStackTrace();  // 서버 로그에 오류 출력
	        resultMap.put("res_msg", "서버 내부 오류: " + e.getMessage());
	    }
		
		return resultMap;
	}
	
	
}
