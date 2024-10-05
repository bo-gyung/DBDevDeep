package com.dbdevdeep.place.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.domain.PlaceDto;
import com.dbdevdeep.place.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PlaceService {
	
	private final PlaceRepository placeRepository;
	private final EmployeeRepository employeeRepository;
	private final FileService fileService;
	
	@Autowired
	public PlaceService(PlaceRepository placeRepository, EmployeeRepository employeeRepository,
			FileService fileService) {
		this.placeRepository = placeRepository;
		this.employeeRepository = employeeRepository;
		this.fileService = fileService;
	}
	
	// place_status가 'N'일때 조회가안되는 메소드
	public List<Place> getAvaliablePlaces(){
		// place_status가 'Y'인것만 조회
		return placeRepository.findByPlaceStatus("Y");
	}
	
	
	
	// 모든 Place 엔티티를 조회하는 기본 메서드,(select용)
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
	
	
	// 삭제하기
	public int deletePlace(Long place_no) {
		int result = -1;
		try {
		  Place place = placeRepository.findByplaceNo(place_no);
            if (place.getNewPicName() != null) {
                fileService.placeDelete(place_no);  // 파일 삭제
            }
            placeRepository.deleteById(place_no);  // 장소 삭제
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;	
        
	}
	
	// 수정하기
	public int updatePlace(PlaceDto dto, MultipartFile file) {
		int result = -1;
		
		// 필수 값 검증
        if (dto.getPlace_name() == null || dto.getPlace_name().isEmpty()) {
            throw new IllegalArgumentException("장소명은 필수 입력 항목입니다.");
        }
        if (dto.getPlace_status() == null || dto.getPlace_status().isEmpty()) {
            throw new IllegalArgumentException("장소 상태는 필수 입력 항목입니다.");
        }
		
		try {

			  Employee e = employeeRepository.findByempId(dto.getEmp_id());
	            Place existingPlace = placeRepository.findByplaceNo(dto.getPlace_no());

	            // 파일이 있을 경우 처리
	            if (file != null && !file.isEmpty()) {
	                String originalFilename = file.getOriginalFilename();
	                if (originalFilename != null && !originalFilename.isEmpty()) {
	                    // 새로운 파일 업로드
	                    String savedFileName = fileService.placeUpload(file);
	                    if (savedFileName != null) {
	                        // 기존 파일이 있으면 삭제
	                        if (existingPlace.getNewPicName() != null && !existingPlace.getNewPicName().isEmpty()) {
	                            fileService.placeDelete(existingPlace.getPlaceNo());
	                        }
	                        // 새로운 파일 정보 업데이트
	                        dto.setOri_pic_name(originalFilename);
	                        dto.setNew_pic_name(savedFileName);
	                    } else {
	                        throw new RuntimeException("파일 업로드 중 오류 발생");
	                    }
	                }
	            } else {
	                // 파일이 없으면 기존 파일 정보 유지
	                dto.setOri_pic_name(existingPlace.getOriPicName());
	                dto.setNew_pic_name(existingPlace.getNewPicName());
	            }

	            // 상태가 "사용 가능"이면 사용 불가 관련 필드 초기화
	            if ("Y".equals(dto.getPlace_status())) {
	                dto.setUnuseable_start_date(null);
	                dto.setUnuseable_end_date(null);
	                dto.setUnuseable_reason(null);
	            }

	            // Place 객체를 빌드하여 수정
	            Place p = Place.builder()
	                    .placeNo(dto.getPlace_no())
	                    .employee(e)
	                    .placeName(dto.getPlace_name())
	                    .placeLocation(dto.getPlace_location())
	                    .placeContent(dto.getPlace_content())
	                    .placeStatus(dto.getPlace_status())
	                    .placeStarttime(dto.getPlace_start_time())
	                    .placeEndtime(dto.getPlace_end_time())
	                    .unuseableReason(dto.getUnuseable_reason())
	                    .unuseableStartDate(dto.getUnuseable_start_date())
	                    .unuseableEndDate(dto.getUnuseable_end_date())
	                    .oriPicName(dto.getOri_pic_name())
	                    .newPicName(dto.getNew_pic_name())
	                    .regDate(dto.getReg_date())
	                    .modDate(LocalDateTime.now())
	                    .build();

	            placeRepository.save(p);
	            result = 1;

	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("게시글 수정 중 오류 발생", e);
	        }

	        return result;
	    }
	
	
	
	
	// 게시글 상세조회
	public PlaceDto selectPlaceOne(Long place_no) {

	    Place p = placeRepository.findByplaceNo(place_no);

	    if (p == null) {
	        throw new EntityNotFoundException("Place not found for place_no: " + place_no);
	    }

	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	    PlaceDto dto = PlaceDto.builder()
	            .place_name(p.getPlaceName())
	            .place_no(p.getPlaceNo())
	            .place_status(p.getPlaceStatus())
	            .place_start_time(p.getPlaceStarttime())
	            .place_end_time(p.getPlaceEndtime())
	            .unuseable_reason(p.getUnuseableReason())
	            // 날짜 포맷 적용: null이 아닌 경우에만 포맷을 적용
	            .unuseable_start_date(p.getUnuseableStartDate() != null && !p.getUnuseableStartDate().trim().isEmpty() ? 
	                                  LocalDate.parse(p.getUnuseableStartDate()).format(outputFormatter) : null)
	            .unuseable_end_date(p.getUnuseableEndDate() != null && !p.getUnuseableEndDate().trim().isEmpty() ? 
	                                LocalDate.parse(p.getUnuseableEndDate()).format(outputFormatter) : null)
	            .ori_pic_name(p.getOriPicName() != null ? p.getOriPicName() : "Default oriPicname")
	            .new_pic_name(p.getNewPicName() != null ? p.getNewPicName() : "Default newPicname")
	            .place_content(p.getPlaceContent())
	            .place_location(p.getPlaceLocation())
	            .build();

	    return dto;
	}
	
	
	
	// 로그인한 사용자 정보 가져오기
    public String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return user.getUsername(); // 로그인한 사용자의 emp_id를 반환
        }
        return null; // 로그인이 안 되어 있으면 null 반환
    }

 // 장소 등록
    public int createPlace(PlaceDto dto, MultipartFile file) {
    	int result = -1;
    	
        // 필수 값 검증
        if (dto.getPlace_name() == null || dto.getPlace_name().isEmpty()) {
            throw new IllegalArgumentException("장소명은 필수 입력 항목입니다.");
        }
        if (dto.getPlace_status() == null || dto.getPlace_status().isEmpty()) {
            throw new IllegalArgumentException("장소 상태는 필수 입력 항목입니다.");
        }
    	try {
    		  // 가장 큰 placeNo 값을 찾아서, 없으면 0을 할당하고, 있으면 +1
            Long maxPlaceNo = placeRepository.findMaxPlaceNo();  
            Long nextPlaceNo = (maxPlaceNo == null) ? 0L : maxPlaceNo + 1;
            Employee e = employeeRepository.findByempId(dto.getEmp_id());
            
            // 파일 업로드 처리
            String newPicName = null;
            String oriPicName = null;
            
            if (file != null && !file.isEmpty()) {
                // 파일이 존재하는 경우
                oriPicName = file.getOriginalFilename();  // 원본 파일 이름
                newPicName = fileService.placeUpload(file);  // 파일 업로드 후 새로운 파일 이름 반환
            } else {
                // 파일이 없는 경우 기본값 설정
            	 oriPicName = "noimage.png"; // 기본 이미지 파일 이름
            	 newPicName = "/assets/images/noimage.png"; // 파일이 저장된 경로
            }
            
            // Place 엔티티 빌드
            Place p = Place.builder()
                    .placeNo(nextPlaceNo)
                    .employee(e)
                    .placeName(dto.getPlace_name())
                    .placeLocation(dto.getPlace_location())
                    .placeContent(dto.getPlace_content())
                    .placeStatus(dto.getPlace_status())
                    .placeStarttime(dto.getPlace_start_time())
                    .placeEndtime(dto.getPlace_end_time())
                    .unuseableReason(dto.getUnuseable_reason())
                    .unuseableStartDate(dto.getUnuseable_start_date())
                    .unuseableEndDate(dto.getUnuseable_end_date())
                    .oriPicName(oriPicName)  // 파일 업로드가 성공한 경우 파일 이름 저장
                    .newPicName(newPicName)  // 파일이 없을 경우 기본값 사용
                    .regDate(dto.getReg_date())
                    .modDate(dto.getMod_date())
                    .build();

            // 데이터베이스 저장
            placeRepository.save(p);
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
	
	
	// 시간을 포맷팅하는 곳
	public String formatTime(String time) {
		if(time != null && !time.isEmpty()) {
			return time;
		}
		
		return "00:00"; // 기본값
	}
	
	// 시간 범위를 포맷팅해서 변환하는 곳
	public String getFormattedTimeRange(Place place) {
		String startTime = formatTime(place.getPlaceStarttime());
		String endTime = formatTime(place.getPlaceEndtime());
		
		
		return startTime + " ~ " + endTime;
	}
	
	
	
	// 사용 불가 시작일 가공 데이터
	public List<String> getFormattedUnuseableStartDate() {
		List<String> dates = placeRepository.findFormattedUnuseableStartDate();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		
		List<String> formattedDates = new ArrayList<>();
		for(String date : dates) {
			LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
			formattedDates.add(parsedDate.format(outputFormatter));
		}
		return formattedDates;
        
    }
	
	// 사용 불가 종료일 가공 데이터 가져오기
    public List<String> getFormattedUnuseableEndDate() {
       List<String> dates = placeRepository.findFormattedUnuseableEndDate();
       DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
       DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		
		List<String> formattedDates = new ArrayList<>();
		for(String date : dates) {
			LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
			formattedDates.add(parsedDate.format(outputFormatter));
		}
		return formattedDates;
    }
	
	//장소 목록
	public List<PlaceDto> selectPlaceList(PlaceDto placeDto){
		List<Place> placeList = placeRepository.findAll();
		List<PlaceDto> placeDtoList = new ArrayList<PlaceDto>();
		
		// 날짜 형태 폼 변환
		  DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		   DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		    
		// Place 엔티티를 PlaceDto로 변환
		for(Place p : placeList) {
			PlaceDto dto = new PlaceDto().toDto(p);
			
			// 시간 데이터 포맷 시간범위
			String formattedTimeRange = getFormattedTimeRange(p);
			dto.setFormattedTimeRange(formattedTimeRange);
			
			// 날짜 데이터를 포맷해서 DTO에 추가
			if(p.getUnuseableStartDate() != null && !p.getUnuseableStartDate().trim().isEmpty()) {
				LocalDate startDate = LocalDate.parse(p.getUnuseableStartDate(), inputFormatter);
				dto.setUnuseable_start_date(startDate.format(outputFormatter));
				
			}
			
			if(p.getUnuseableEndDate() != null && !p.getUnuseableEndDate().trim().isEmpty()) {
				LocalDate endDate = LocalDate.parse(p.getUnuseableEndDate(), inputFormatter);
				dto.setUnuseable_end_date(endDate.format(outputFormatter));
			}
			
			placeDtoList.add(dto);
			
		}
		return placeDtoList;
	}

}
