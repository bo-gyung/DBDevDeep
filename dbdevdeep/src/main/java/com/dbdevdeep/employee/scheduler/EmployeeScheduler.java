package com.dbdevdeep.employee.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dbdevdeep.employee.domain.EmployeeStatusDto;
import com.dbdevdeep.employee.domain.TransferDto;
import com.dbdevdeep.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EmployeeScheduler {

	private final EmployeeService employeeService;
	
	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void updateEmployeeStatuses() {
        LocalDate now = LocalDate.now();

        // 전근 처리
        List<TransferDto> transfersToProcess = employeeService.findTransfersToProcess(now);
        for (TransferDto dto : transfersToProcess) {
            employeeService.processTransfer(dto);
        }

        // 휴직 처리
        List<EmployeeStatusDto> restsToProcess = employeeService.findRestsToProcess(now);
        for (EmployeeStatusDto dto : restsToProcess) {
            employeeService.processRest(dto);
        }

        // 퇴직 처리
        List<EmployeeStatusDto> leavesToProcess = employeeService.findLeavesToProcess(now);
        for (EmployeeStatusDto dto : leavesToProcess) {
            employeeService.processLeave(dto);
        }
        
        // 복직 처리
        List<EmployeeStatusDto> returnsToProcess = employeeService.findReturnsToProcess(now);
        for (EmployeeStatusDto dto : returnsToProcess) {
            employeeService.processReturn(dto);
        }
    }
}
