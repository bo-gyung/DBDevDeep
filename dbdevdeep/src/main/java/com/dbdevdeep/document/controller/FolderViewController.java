package com.dbdevdeep.document.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbdevdeep.document.domain.FolderDto;
import com.dbdevdeep.document.service.FolderService;

@RestController
public class FolderViewController {
	
	private final FolderService folderService;
	
	public FolderViewController(FolderService folderService) {
		this.folderService = folderService;
	}
	
	@GetMapping("/folder")
	public String selectFolderList(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		List<FolderDto> publicFolderList = folderService.selectPublicFolderList();
		List<FolderDto> privateFolderList = folderService.selectPrivateFolderList(empId);
		
		model.addAttribute("publicFolderList", publicFolderList);
		model.addAttribute("privateFolderList", privateFolderList);
		
		return "include/document_nav";
	}
}
