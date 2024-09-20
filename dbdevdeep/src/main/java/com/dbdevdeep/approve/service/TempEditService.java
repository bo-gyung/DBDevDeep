package com.dbdevdeep.approve.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.approve.domain.TempEdit;
import com.dbdevdeep.approve.domain.TempEditDto;
import com.dbdevdeep.approve.repository.TempEditRepository;

@Service
public class TempEditService {

	private final TempEditRepository tempEditRepository;
	
	@Autowired
	public TempEditService(TempEditRepository tempEditRepository) {
		this.tempEditRepository = tempEditRepository;
	}
	
	public List<TempEditDto> callTemp(){
		List<Object[]> tEdit = tempEditRepository.findTempNoAndTempName();
		List<TempEditDto> tEditDto = new ArrayList<TempEditDto>();
		
		for(Object[] t : tEdit) {
			Long tempNo = (Long) t[0];
			String tempName = (String) t[1];
			
			TempEditDto dto = new TempEditDto(tempNo, tempName , null);
			tEditDto.add(dto);
		}
		return tEditDto;
	}
	
	public TempEditDto getTempContent(Long tempNo) {
	    try {
	        TempEdit template = tempEditRepository.findByTempNo(tempNo);
	        if (template == null) {
	            throw new IllegalArgumentException("템플릿을 찾을 수 없습니다.");
	        }
	        return new TempEditDto(template.getTempNo(), template.getTempName(), template.getTempContent());
	    } catch (Exception e) {
	        // 예외 로그 출력
	        e.printStackTrace();
	        return null;
	    }
	}


}
