package com.dbdevdeep.approve.controller;

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

@Controller
public class ApproDraftApiController {

	private final ApproDraftService approDraftService;
	private final FileService fileService;
	
	@Autowired
	public ApproDraftApiController(ApproDraftService approDraftService, FileService fileService) {
		this.approDraftService = approDraftService;
		this.fileService = fileService;
	}
	
	@ResponseBody
	@PostMapping("draftApprove")
	public Map<String,String> draftApprove(ApproDraftDto dto, @RequestParam("file") MultipartFile file){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 등록중 오류가 발생했습니다.");
		
		String savedFileName = fileService.approveUpload(file);
		if(savedFileName != null) {
			dto.setOri_file(file.getOriginalFilename());
			dto.setNew_file(savedFileName);
			if(approDraftService.saveDraft(dto) != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "게시글이 성공적으로 등록되었습니다.");
			}
		}else {
			resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
		}
		
		return resultMap;
	}
	
}
