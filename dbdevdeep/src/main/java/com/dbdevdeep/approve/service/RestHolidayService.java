package com.dbdevdeep.approve.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dbdevdeep.approve.domain.HolidayDto;
import com.dbdevdeep.approve.domain.HolidayResponseDto;

@Service
public class RestHolidayService {
    
    private static final String HOLIDAY_API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private static final String SERVICE_KEY = "fENa8pkJTLDR8iPDrgEfbXLap5gwBPmbKcWDJ1ciJcPiMiiEMHidN1MulQ5+ltbh7MzfsOScjrEVlNcyDlxU4Q==";

    public List<LocalDate> getHolidays(int year, int month) {
        RestTemplate restTemplate = new RestTemplate();

        // 메시지 변환기 추가
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new Jaxb2RootElementHttpMessageConverter()); // XML 변환기
        restTemplate.setMessageConverters(messageConverters);

        String url = String.format("%s?solYear=%d&solMonth=%02d&ServiceKey=%s",
                                   HOLIDAY_API_URL,
                                   year,
                                   month,
                                   SERVICE_KEY);

        // 응답을 DTO로 매핑
        ResponseEntity<HolidayResponseDto> response = restTemplate.getForEntity(url, HolidayResponseDto.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<LocalDate> holidays = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null && response.getBody().getBody() != null) {
                List<HolidayDto> holidayItems = response.getBody().getBody().getItems().getItem();
                System.out.println("Received holidays: " + holidayItems);
                for (HolidayDto holiday : holidayItems) {
                    holidays.add(LocalDate.parse(holiday.getLocdate(), formatter));
                }
            } else {
            }
        } else {
            System.out.println("API call failed with status code: " + response.getStatusCode());
        }

        return holidays;
    }
    

}