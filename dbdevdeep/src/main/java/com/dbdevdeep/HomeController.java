package com.dbdevdeep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.service.AttendanceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {
	
	private final AttendanceService attendanceService;
	
	@GetMapping({"", "/"})
	public String home(Model model, HttpSession session) {
		
		AttendanceDto dto = attendanceService.findByTodayCheckTime();
		
		if(dto != null) {
			model.addAttribute("checktime", dto);			
		}
		
		String nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH"))+"00";
		
		if (session.getAttribute("weatherFetched") == null && !nowTime.equals(session.getAttribute("weatherFetchedTime"))) {
	        try {
	            Map<String, String> resultMap = todayWeather();
	            model.addAttribute("resultMap", resultMap);
	            session.setAttribute("weatherFetched", resultMap); // API 호출 완료 표시
	            session.setAttribute("weatherFetchedTime", nowTime);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	    	model.addAttribute("resultMap", session.getAttribute("weatherFetched"));
	    }
		
		return "home";
	}
	
	public Map<String, String> todayWeather() throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		
	    String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    baseDate = LocalTime.now().isBefore(LocalTime.of(3, 0)) ? LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) : baseDate;
	    String realDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    String baseTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH"))+"00";
	    
	    System.out.println(baseDate + ", " + realDate + ", " + baseTime);
	    
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=7pawedG81MlCdHRRrEnYTSiXAO5AlMzn2IzPrz5je848GT8xvkGfn0ulwyO7M6nWbOj9gsqZ6mDCas%2FxOE2eUQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("300", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*‘21년 6월 28일발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0200", "UTF-8")); /*05시 발표*/
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String responseBody = sb.toString();
        
		try {
			JSONObject jsonObject = new JSONObject(responseBody);
			JSONArray items = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
			
			for (int i = 0; i < items.length(); i++) {
			    JSONObject item = items.getJSONObject(i);
			    if ("TMN".equals(item.getString("category"))) { 
			        System.out.println("TMN: " + Math.round(Double.parseDouble(item.getString("fcstValue"))));
			        resultMap.put("TMN", Math.round(Double.parseDouble(item.getString("fcstValue"))) + "");
			    }
			    if ("TMX".equals(item.getString("category"))) {
			        System.out.println("TMX: " + Math.round(Double.parseDouble(item.getString("fcstValue"))));
			        resultMap.put("TMX", Math.round(Double.parseDouble(item.getString("fcstValue"))) + "");
			    }
			    if ("SKY".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	String sky = "";
			    	switch(item.getString("fcstValue")) {
			    	case "1": sky = "맑음1"; break;
			    	case "3": sky = "구름많음3"; break;
			    	case "4": sky = "흐림4"; break;
			    	}
			    	System.out.println("SKY: " + sky);
			    	resultMap.put("SKY", item.getString("fcstValue"));
			    }
			    if ("TMP".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	System.out.println("TMP: " + Math.round(Double.parseDouble(item.getString("fcstValue"))) + " " + item.getString("fcstDate") +" " + item.getString("fcstTime"));
			    	resultMap.put("TMP", Math.round(Double.parseDouble(item.getString("fcstValue"))) + "");
			    }
			    if ("PTY".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	String pty = "";
			    	switch(item.getString("fcstValue")) {
			    	case "0": pty = "맑음0"; break;
			    	case "1": pty = "비1"; break;
			    	case "2": pty = "비/눈2"; break;
			    	case "3": pty = "눈3"; break;
			    	case "4": pty = "소나기4"; break;
			    	}
			    	System.out.println("PTY: " + pty);
			    	resultMap.put("PTY", item.getString("fcstValue"));
			    }
			    if ("WSD".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	resultMap.put("WSDV", item.getString("fcstValue"));
			    	if(Double.parseDouble(item.getString("fcstValue")) < 4) {
			    		System.out.println("WSD: " + "약0");
			    		resultMap.put("WSD", "0");
			    	} else if (Double.parseDouble(item.getString("fcstValue")) < 9) {
			    		System.out.println("WSD: " + "약강1");
			    		resultMap.put("WSD", "1");
			    	} else if (Double.parseDouble(item.getString("fcstValue")) < 14) {
			    		System.out.println("WSD: " + "강2");
			    		resultMap.put("WSD", "2");
			    	} else {
			    		System.out.println("WSD: " + "강강3");
			    		resultMap.put("WSD", "3");
			    	}
			    }
			    if ("POP".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	System.out.println("POP: " + item.getString("fcstValue"));
			    	resultMap.put("POP", item.getString("fcstValue"));
			    }
			    if ("PCP".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	System.out.println("PCP: " + item.getString("fcstValue"));
			    	resultMap.put("PCP", item.getString("fcstValue"));
			    }
			    if ("REH".equals(item.getString("category")) && baseTime.equals(item.getString("fcstTime")) && realDate.equals(item.getString("fcstDate"))) {
			    	System.out.println("REH: " + item.getString("fcstValue"));
			    	resultMap.put("REH", item.getString("fcstValue"));
			    }
			    
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return resultMap;
    }
}
