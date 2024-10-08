package com.dbdevdeep.approve.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.dbdevdeep.approve.domain.ApproFileDto;
import com.dbdevdeep.approve.domain.ApproveDto;
import com.dbdevdeep.approve.domain.ApproveLineDto;
import com.dbdevdeep.approve.domain.TempEdit;
import com.dbdevdeep.approve.domain.TempEditDto;
import com.dbdevdeep.approve.service.ApproveLineService;
import com.dbdevdeep.approve.service.ApproveService;
import com.dbdevdeep.approve.service.TempEditService;
import com.dbdevdeep.employee.domain.MySignDto;

@Controller
public class ApproveViewController {

	private final ApproveService approveService;
	private final ApproveLineService approveLineService;
	private final TempEditService tempEditService;
	
	@Autowired
	public ApproveViewController(ApproveService approveService , ApproveLineService approveLineService,
			TempEditService tempEditService) {
		this.approveService = approveService;
		this.approveLineService = approveLineService;
		this.tempEditService = tempEditService;
	}
	
	//목록조회
	@GetMapping("/approve")
	public String selectApproveList(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
		
		List<ApproveDto> resultList = approveService.selectApproveList(username);
		model.addAttribute("resultList",resultList);
		return "approve/approList";
	}
	
	// 보고서 목록 조회
	@GetMapping("/approveDocu")
	public String selectApproveDocuList(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		List<ApproveDto> resultList = approveService.selectApproveDocuList(username);
		model.addAttribute("resultList",resultList);
		return "approve/approDocuList";
	}
	
	// 결재 받은 목록 조회
		@GetMapping("/comeApprove")
		public String selectComeApproveList(Model model) {
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String username = authentication.getName();
			
			List<ApproveDto> approvalList = approveService.comeApproveRequestList(username);
			model.addAttribute("approvalList",approvalList);
			return "approve/comeApprove";
		}
		
	// 참조 지정 받은 목록 조회
		@GetMapping("/refApprove")
		public String refApproveList(Model model) {
					
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
					
			List<ApproveDto> refList = approveService.comeRefList(username);
			model.addAttribute("refList",refList);
			return "approve/refApprove";
		}
		
	// 결재 작성
	@GetMapping("/approCreate")
    public String showApproCreatePage() {
        return "approve/approCreate";
	}
	
	// 보고서 작성
	@GetMapping("/docuCreate")
    public String showDocuCreatePage(Model model) {
		List<TempEditDto> tempList = tempEditService.callTemp();
		model.addAttribute("tempList",tempList);
        return "approve/docuCreate";
	}
	
	// 보고서 상세
	@GetMapping("/docuDetail/{appro_no}")
	public String docuDetailOne(Model model, @PathVariable("appro_no") Long approNo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		Map<String, Object> detailMap = approveService.getDocuDetail(approNo);
		
		try {
			ApproveLineDto backReason = approveLineService.approBackReason(approNo);
			detailMap.put("backReason",backReason);
		}catch (NoSuchElementException e) {
			detailMap.put("backReason", null);
		}
		
		List<MySignDto> signDto = approveService.signList(username);
		
		model.addAttribute("sDto", signDto);
		model.addAllAttributes(detailMap);
		return "approve/docuDetail";
	}
	
	// 결재 상세
	@GetMapping("/approDetail/{appro_no}")
	public String selectBoardOne(Model model, @PathVariable("appro_no") Long approNo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		// 결재 정보 상세
		Map<String, Object> detailMap = approveService.getApproveDetail(approNo);
		
		// 반려 사유 정보 가져오기
		try {
	        ApproveLineDto backReason = approveLineService.approBackReason(approNo);
	        detailMap.put("backReason", backReason); // 반려 사유를 Map에 추가
	    } catch (NoSuchElementException e) {
	        // 반려 정보가 없는 경우 null 추가
	        detailMap.put("backReason", null);
	    }
		
		// 사인정보 가져오기 
		List<MySignDto> signDto = approveService.signList(username);
		
		model.addAttribute("sDto", signDto);
		model.addAllAttributes(detailMap);
		return "approve/approDetail";
	}
	
	// 참조 상세
		@GetMapping("/refDetail/{appro_no}")
		public String refBoardOne(Model model, @PathVariable("appro_no") Long approNo) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			
			// 결재 정보 상세
			Map<String, Object> detailMap = approveService.getApproveDetail(approNo);
			
			// 반려 사유 정보 가져오기
			try {
		        ApproveLineDto backReason = approveLineService.approBackReason(approNo);
		        detailMap.put("backReason", backReason); // 반려 사유를 Map에 추가
		    } catch (NoSuchElementException e) {
		        // 반려 정보가 없는 경우 null 추가
		        detailMap.put("backReason", null);
		    }
			
			// 사인정보 가져오기 
			List<MySignDto> signDto = approveService.signList(username);
			
			model.addAttribute("sDto", signDto);
			model.addAllAttributes(detailMap);
			return "approve/refDetail";
		}

	// 결재 수정
	@GetMapping("/approUpdate/{appro_no}")
	public String updateApproOne(Model model, @PathVariable("appro_no") Long approNo) {
		Map<String, Object> detailMap = approveService.getApproveDetail(approNo);
		model.addAllAttributes(detailMap);
		return "approve/approUpdate";
	}
	
	// 보고서 수정
	@GetMapping("/docuUpdate/{appro_no}")
	public String updateDocuOne(Model model, @PathVariable("appro_no") Long approNo) {
		Map<String, Object> detailMap = approveService.getDocuDetail(approNo);
		model.addAllAttributes(detailMap);
		return "approve/docuUpdate";
	}
	
}