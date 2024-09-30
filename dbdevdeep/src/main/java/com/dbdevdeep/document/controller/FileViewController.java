package com.dbdevdeep.document.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.document.domain.FileDto;
import com.dbdevdeep.document.domain.FolderDto;
import com.dbdevdeep.document.service.FolderService;

@Controller
public class FileViewController {
	
	private final FolderService folderService;
	
	public FileViewController(FolderService folderService) {
		this.folderService = folderService;
	}
	
	@GetMapping("/file")
	public String selectFileList(Model model) {
		return "document/file";
	}
	

    @GetMapping("/folderList")
    @ResponseBody
    public List<FolderDto> getFolderList(@RequestParam("folder_no") Long folderNo) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
    	
        return folderService.getChildFolders(folderNo, empId);
    }

    @GetMapping("/fileList")
    @ResponseBody
    public List<FileDto> getFileList(@RequestParam("folder_no") Long folderNo) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
    	
        return folderService.getFilesInFolder(folderNo, empId);
    }
	
}
