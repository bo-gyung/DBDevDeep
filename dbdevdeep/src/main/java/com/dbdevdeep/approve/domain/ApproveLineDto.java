package com.dbdevdeep.approve.domain;

import java.time.LocalDateTime;

import com.dbdevdeep.employee.domain.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ApproveLineDto {

    private Long appro_line_no;
    private Long appro_no;
    private String emp_id;
    private String appro_line_name;
    private int appro_line_order;
    private int appro_line_status;
    private LocalDateTime appro_permit_time;
    private String reason_back;
    private String consult_yn;
    private String appro_line_sign;

    public ApproveLine toEntity(Approve approve, Employee employee) {
        if (employee == null) {
            return null; 
        }

        return ApproveLine.builder()
                .approLineNo(appro_line_no)
                .approve(approve)
                .employee(employee)
                .approLineName(appro_line_name)
                .approLineOrder(appro_line_order)
                .approLineStatus(appro_line_status)
                .approPermitTime(appro_permit_time)
                .reasonBack(reason_back)
                .consultYn(consult_yn)
                .approLineSign(appro_line_sign)
                .build();
    }

    public ApproveLineDto toDto(ApproveLine approveLine) {
        return ApproveLineDto.builder()
                .appro_line_no(approveLine.getApproLineNo())
                .appro_no(approveLine.getApprove().getApproNo())
                .emp_id(approveLine.getEmployee().getEmpId())
                .appro_line_name(approveLine.getApproLineName())
                .appro_line_order(approveLine.getApproLineOrder())
                .appro_line_status(approveLine.getApproLineStatus())
                .appro_permit_time(approveLine.getApproPermitTime())
                .reason_back(approveLine.getReasonBack())
                .consult_yn(approveLine.getConsultYn())
                .appro_line_sign(approveLine.getApproLineSign())
                .build();
    }
}
