package com.dbdevdeep;

import java.util.Date;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ExceptionHandlingController implements ErrorController{
	
	@RequestMapping(value="/error")
	public String handlingError(HttpServletRequest request, Model model) {		
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
		
		if(status != null) {
			int statusCode = Integer.valueOf(status.toString());
			
			if(statusCode == HttpStatus.NOT_FOUND.value()) { // 404 error
				model.addAttribute("code", status.toString());
				model.addAttribute("msg", httpStatus.getReasonPhrase());
				model.addAttribute("timestamp", new Date());
				return "/error/404";
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) { // 500 error
				model.addAttribute("code", status.toString());
				// 500 error => server error이기에 사용자에게 정보 제공 X
				return "/error/500";
			}
		}
		// 404, 500 error 이외 모든 에러는 error/error 페이지로 이동
		return "/error/error";
	}
}
