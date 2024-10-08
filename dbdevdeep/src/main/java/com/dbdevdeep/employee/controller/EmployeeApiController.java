package com.dbdevdeep.employee.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.employee.domain.AuditLogDto;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.EmployeeStatus;
import com.dbdevdeep.employee.domain.EmployeeStatusDto;
import com.dbdevdeep.employee.domain.MySignDto;
import com.dbdevdeep.employee.domain.Transfer;
import com.dbdevdeep.employee.domain.TransferDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.security.service.SecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class EmployeeApiController {

	private final EmployeeService employeeService;
	private final FileService fileService;
	private final SecurityService securityService;

	@ResponseBody
	@PostMapping("/govid")
	public Map<String, String> govIdCheck(@RequestBody String govId) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "중복확인 중 오류가 발생하였습니다.");

		if (employeeService.govIdCheck(govId) == 1) {
			resultMap.put("res_code", "409");
			resultMap.put("res_msg", "중복되는 값이 존재합니다.");
		} else {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "중복되는 값이 없습니다.");
		}

		return resultMap;
	}

	@ResponseBody
	@PostMapping("/employee/add")
	public Map<String, String> signup(EmployeeDto dto, @RequestParam("file") MultipartFile file,
			@RequestParam(name = "trans_school_id", required = false) String trans_school_id) {

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "계정 등록 중 오류가 발생하였습니다.");

		String savedFileName = fileService.employeePicUpload(file);

		if (savedFileName != null) {
			dto.setOri_pic_name(file.getOriginalFilename());
			dto.setNew_pic_name(savedFileName);

			Employee employee = employeeService.addEmployee(dto);

			if (employee != null) {

				EmployeeDto employeeDto = new EmployeeDto().toDto(employee);

				TransferDto transferDto = new TransferDto();
				transferDto.setEmp_id(employeeDto.getEmp_id());
				transferDto.setTrans_date(employeeDto.getHire_date());
				transferDto.setTrans_school_id(trans_school_id);
				transferDto.setTrans_type("F");

				Transfer transferResult = employeeService.employeeTransfer(transferDto);

				if (transferResult != null) {
					resultMap.put("res_code", "200");
					resultMap.put("res_msg", "계정 등록에 성공하였습니다.");
				} else {
					resultMap.put("res_msg", "전근 기록에 실패하였습니다.");
				}
			}
		} else {
			resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		}
		return resultMap;
	}

	@ResponseBody
	@PostMapping("/addsign")
	public Map<String, String> addSign(MySignDto dto, @RequestParam("file") MultipartFile file) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "서명 등록 중 오류가 발생하였습니다.");

		String savedFileName = fileService.employeeSignPicUpload(file);

		if (savedFileName != null) {
			dto.setOri_pic_name(file.getOriginalFilename());
			dto.setNew_pic_name(savedFileName);

			if (employeeService.employeeSignAdd(dto) > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "서명 등록에 성공하였습니다.");
			}
		} else {
			resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		}
		return resultMap;
	}

	@ResponseBody
	@PostMapping("/editsign")
	public Map<String, String> editSign(MySignDto dto, @RequestParam("file") MultipartFile file) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "서명 등록 중 오류가 발생하였습니다.");

		String savedFileName = fileService.employeeSignPicUpload(file);

		if (savedFileName != null) {
			dto.setOri_pic_name(file.getOriginalFilename());
			dto.setNew_pic_name(savedFileName);

			if (employeeService.employeeSignAdd(dto) > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "서명 수정에 성공하였습니다.");
			}
		} else {
			resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		}
		return resultMap;
	}

	@ResponseBody
	@DeleteMapping("/mypage/sign/{sign_no}")
	public Map<String, String> deleteBoard(@PathVariable("sign_no") Long sign_no) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 삭제 중 오류가 발생했습니다.");

		if (employeeService.deleteSign(sign_no) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글이 성공적으로 삭제되었습니다.");
		}

		return resultMap;
	}

	// 내 정보 수정
	@ResponseBody
	@PostMapping("/mypage/edit")
	public Map<String, String> editMyInfo(EmployeeDto dto,
			@RequestParam(name = "file", required = false) MultipartFile file, HttpServletRequest request) {
		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "정보 수정 중 오류가 발생하였습니다.");

		if (file != null && "".equals(file.getOriginalFilename()) == false) {
			String savedFileName = fileService.employeePicUpload(file);

			if (savedFileName != null) {
				dto.setOri_pic_name(file.getOriginalFilename());
				dto.setNew_pic_name(savedFileName);

				if (fileService.employeePicDelete(dto.getEmp_id()) > 0) {
					resultMap.put("res_msg", "기존 파일이 정상적으로 삭제되었습니다.");
				}
			} else {
				resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
			}
		}

		Employee e = employeeService.editMyInfo(dto);

		if (e != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "내 정보가 성공적으로 수정되었습니다.");

			Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
			if (currentAuth != null && currentAuth.getPrincipal() instanceof UserDetails) {

				// 현재 세션 무효화
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				// 새로운 세션 생성
				HttpSession newSession = request.getSession(true);

				// 기존 인증 정보로 다시 로그인 처리
				UserDetails newUserDetails = securityService.loadUserByUsername(dto.getEmp_id());
				Authentication newAuth = new UsernamePasswordAuthenticationToken(newUserDetails, dto.getEmp_pw(),
						newUserDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuth);
				// 새로 생성된 세션에 인증 정보 설정
				newSession.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			}

		}

		return resultMap;
	}

	// 내 비밀번호 수정
	@ResponseBody
	@PostMapping("/edit-mypw")
	public Map<String, String> editMyPw(@RequestParam("new_emp_pw") String newEmpPw,
			@RequestParam("ori_emp_pw") String oriEmpPW, HttpServletRequest request) {
		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "정보 수정 중 오류가 발생하였습니다.");

		int result = employeeService.checkPw(oriEmpPW);

		if (result > 0) {
			Employee e = employeeService.editMyPw(newEmpPw);

			EmployeeDto dto = new EmployeeDto().toDto(e);

			if (e != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "내 정보가 성공적으로 수정되었습니다.");

				Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
				if (currentAuth != null && currentAuth.getPrincipal() instanceof UserDetails) {

					// 현재 세션 무효화
					HttpSession session = request.getSession(false);
					if (session != null) {
						session.invalidate();
					}
					// 새로운 세션 생성
					HttpSession newSession = request.getSession(true);

					// 기존 인증 정보로 다시 로그인 처리
					UserDetails newUserDetails = securityService.loadUserByUsername(dto.getEmp_id());
					Authentication newAuth = new UsernamePasswordAuthenticationToken(newUserDetails, dto.getEmp_pw(),
							newUserDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(newAuth);
					// 새로 생성된 세션에 인증 정보 설정
					newSession.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
				}
			}
		} else {
			resultMap.put("res_msg", "비밀번호가 일치하지 않습니다.");
		}

		return resultMap;
	}

	// 상태 메시지 수정
	@ResponseBody
	@PostMapping("/status/{empId}")
	public Map<String, String> updateStatus(@RequestBody EmployeeDto dto) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "404");
		map.put("res_msg", "게시글 삭제 중 오류가 발생했습니다.");

		int result = employeeService.editChatStatus(dto.getEmp_id(), dto.getChat_status_msg());

		if (result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "상태메세지가 수정되었습니다.");
		}

		return map;
	}

	// 채팅 상대 프로필 조회
	@ResponseBody
	@GetMapping("/profile/{empId}")
	public EmployeeDto selectProfile(Model model, @PathVariable("empId") String emp_id) {
		EmployeeDto dto = employeeService.selectEmployeeOne(emp_id);

		return dto;
	}

	// 비밀번호 초기화
	@ResponseBody
	@PutMapping("/reset-pw")
	public Map<String, String> editMyPw(@RequestParam("emp_id") String emp_id, @RequestParam("emp_pw") String emp_pw) {
		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "비밀번호 초기화 중 오류가 발생했습니다.");

		Employee e = employeeService.resetPw(emp_id, emp_pw);

		if (e != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "비밀번호가 성공적으로 초기화되었습니다.");

		}

		return resultMap;
	}

	@ResponseBody
	@PostMapping("/employee/edit")
	public Map<String, String> employeeInfoEdit(EmployeeDto dto,
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam(name = "admin_id") String admin_id) {
		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "정보 수정 중 오류가 발생하였습니다.");
		
		EmployeeDto oriData = employeeService.selectEmployeeOne(dto.getEmp_id());
		
		String oriDataJson = employeeService.convertDtoToJson(oriData);
		String newDataJson = employeeService.convertDtoToJson(dto);
				
		AuditLogDto alDto = new AuditLogDto();
		alDto.setAdmin_id(admin_id);
		alDto.setEmp_id(dto.getEmp_id());
		alDto.setNew_data(newDataJson);
		alDto.setOri_data(oriDataJson);
		alDto.setAudit_type("U");
		alDto.setChanged_item("emp_info");
		
		if (file != null && "".equals(file.getOriginalFilename()) == false) {
			String savedFileName = fileService.employeePicUpload(file);

			if (savedFileName != null) {
				dto.setOri_pic_name(file.getOriginalFilename());
				dto.setNew_pic_name(savedFileName);

				if (fileService.employeePicDelete(dto.getEmp_id()) > 0) {
					resultMap.put("res_msg", "기존 파일이 정상적으로 삭제되었습니다.");
				}
			} else {
				resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
			}
		}

		Employee e = employeeService.employeeInfoEdit(dto, alDto);

		if (e != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", dto.getEmp_name() + "님의 정보가 성공적으로 수정되었습니다.");
		}

		return resultMap;
	}

	// 전근 등록 및 직원 status 변경
	@ResponseBody
	@PostMapping("/employee/transfer")
	public Map<String, String> employeeTransfer(TransferDto dto) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "전근 등록 중 오류가 발생하였습니다.");

		int result = employeeService.findBySameTransfer(dto);

		if (result == 0) {
			Transfer transfer = employeeService.employeeTransfer(dto);

			if (transfer != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "전근 등록에 성공하였습니다.");
			} else {
				resultMap.put("res_msg", "전근은 재직 상태인 직원만 가능합니다.");
			}
		} else {
			resultMap.put("res_msg", "중복된 전근 기록이 존재합니다.");
		}

		return resultMap;
	}

	// 휴직 등록 및 직원 status 변경
	@ResponseBody
	@PostMapping("/employee/rest")
	public Map<String, String> employeeRest(EmployeeStatusDto dto) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "휴직 등록 중 오류가 발생하였습니다.");

		// 중복 확인
		int result = employeeService.findBySameRest(dto);

		if (result == 0) {
			EmployeeStatus employeeStatus = employeeService.employeeRest(dto);

			if (employeeStatus != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "휴직 등록에 성공하였습니다.");
			} else {
				resultMap.put("res_msg", "휴직은 재직 상태인 직원만 가능합니다.");
			}
		} else {
			resultMap.put("res_msg", "중복된 휴직 기록이 존재합니다.");
		}

		return resultMap;
	}

	// 퇴직 등록 및 직원 status 변경
	@ResponseBody
	@PostMapping("/employee/leave")
	public Map<String, String> employeeLeave(EmployeeStatusDto dto) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "퇴직 등록 중 오류가 발생하였습니다.");

		// 중복 확인
		int result = employeeService.findBySameLeave(dto);

		if (result == 0) {
			EmployeeStatus employeeStatus = employeeService.employeeLeave(dto);

			if (employeeStatus != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "퇴직 등록에 성공하였습니다.");
			} else {
				resultMap.put("res_msg", "퇴직은 재직 상태인 직원만 가능합니다.");
			}
		} else {
			resultMap.put("res_msg", "중복된 퇴직 기록이 존재합니다.");
		}

		return resultMap;
	}

	// 복직 등록 및 직원 status 변경
	@ResponseBody
	@PostMapping("/employee/return")
	public Map<String, String> employeeReturn(EmployeeStatusDto dto) {

		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "복직 등록 중 오류가 발생하였습니다.");

		EmployeeStatus employeeStatus = employeeService.employeeReturn(dto);

		if (employeeStatus != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "복직 등록에 성공하였습니다.");
		} else {
			resultMap.put("res_msg", "복직은 휴직 상태인 직원만 가능합니다.");
		}

		return resultMap;
	}
	
	
}
