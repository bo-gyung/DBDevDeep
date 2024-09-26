package com.dbdevdeep.approve.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.approve.domain.ApproDraftDto;
import com.dbdevdeep.approve.domain.TempEditDto;
import com.dbdevdeep.approve.service.ApproDraftService;
import com.dbdevdeep.approve.service.TempEditService;

@Controller
public class ApproDraftViewController {

	private final ApproDraftService approDraftService;
	private final TempEditService tempEditService;
	
	@Autowired
	public ApproDraftViewController(ApproDraftService approDraftService, TempEditService tempEditService) {
		this.approDraftService = approDraftService;
		this.tempEditService = tempEditService;
	}
	
	// 보고서 임시함
		@GetMapping("/approvedraft")
		public String userDraftList(Model model) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			
			List<ApproDraftDto> resultList = approDraftService.userDraftList(username);
			
			model.addAttribute("resultList", resultList);
			return "approve/docuDraftList";
		}
		
	// 보고서 임시 상세
		@GetMapping("/approvedraft/{draft_no}")
		public String selectDraftOne(Model model , @PathVariable("draft_no") Long draft_no) {
			ApproDraftDto dto = approDraftService.selectDraftOne(draft_no);
			List<TempEditDto> tempList = tempEditService.callTemp();
			model.addAttribute("tempList",tempList);
			model.addAttribute("dto",dto);
			return "approve/docuDraftDetail";
		}
}
