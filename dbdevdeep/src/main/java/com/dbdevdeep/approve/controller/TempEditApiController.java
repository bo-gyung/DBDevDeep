package com.dbdevdeep.approve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.approve.domain.TempEditDto;
import com.dbdevdeep.approve.service.TempEditService;

@Controller
public class TempEditApiController {

	private final TempEditService tempEditService;
	
	@Autowired
	public TempEditApiController(TempEditService tempEditService) {
		this.tempEditService = tempEditService;
	}
	
	@ResponseBody
	@GetMapping("/getTempContent/{tempNo}")
	public ResponseEntity<TempEditDto> getTempContent(@PathVariable("tempNo") Long tempNo) {
	    TempEditDto tDto = tempEditService.getTempContent(tempNo);
	    
	    if (tDto == null || tDto.getTemp_content() == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    return ResponseEntity.ok(tDto);
	}

}
