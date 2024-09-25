package com.dbdevdeep.approve.domain;

import java.time.LocalDateTime;

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
public class ApproDraftDto {

	private Long draft_no;
	private String emp_id;
	private Long temp_no;
	private String draft_name;
	private LocalDateTime appro_time;
	private int appro_type;
	private String appro_title;
	private String appro_content;
	private String ori_file;
	private String new_file;
	private String file_root;
	private int vac_type;
	private LocalDateTime start_time;
	private LocalDateTime end_time;
	private String consult_draft_root;
	private String approval_draft_root;
	private String ref_draft_root;
	
	// DTO를 Entity로 변환하는 메소드
	public ApproDraft toEntity() {
		return ApproDraft.builder()
				.draftNo(draft_no)
				.draftName(draft_name)
				.approTime(appro_time)
				.approType(appro_type)
				.approTitle(appro_title)
				.approContent(appro_content)
				.oriFile(ori_file)
				.newFile(new_file)
				.fileRoot(file_root)
				.vacType(vac_type)
				.startTime(start_time)
				.endTime(end_time)
				.consultDraftRoot(consult_draft_root)
				.approvalDraftRoot(approval_draft_root)
				.refDraftRoot(ref_draft_root)
				.build();
	}
	
	 // Entity를 DTO로 변환하는 정적 메소드
	public ApproDraftDto toDto(ApproDraft approDraft) {
		return ApproDraftDto.builder()
				.draft_no(approDraft.getDraftNo())
				.emp_id(approDraft.getEmployee().getEmpId())
				.temp_no(approDraft.getTempEdit().getTempNo())
				.draft_name(approDraft.getDraftName())
				.appro_time(approDraft.getApproTime())
				.appro_type(approDraft.getApproType())
				.appro_title(approDraft.getApproTitle())
				.appro_content(approDraft.getApproContent())
				.ori_file(approDraft.getOriFile())
				.new_file(approDraft.getNewFile())
				.file_root(approDraft.getFileRoot())
				.vac_type(approDraft.getVacType())
				.start_time(approDraft.getStartTime())
				.end_time(approDraft.getEndTime())
				.consult_draft_root(approDraft.getConsultDraftRoot())
				.approval_draft_root(approDraft.getApprovalDraftRoot())
				.ref_draft_root(approDraft.getRefDraftRoot())
				.build();
	}
}
