package com.dbdevdeep.approve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dbdevdeep.approve.service.ApproDraftService;

@Controller
public class ApproDraftViewController {

	private final ApproDraftService approDraftService;
	
	@Autowired
	public ApproDraftViewController(ApproDraftService approDraftService) {
		this.approDraftService = approDraftService;
	}
}
