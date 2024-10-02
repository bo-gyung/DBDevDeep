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
	private LocalDateTime appro_time;
	private String appro_title;
	private String appro_content;
	private String ori_file;
	private String new_file;
	private String consult_draft_root;
	private String approval_draft_root;
	
	// DTO를 Entity로 변환하는 메소드
	public ApproDraft toEntity() {
		return ApproDraft.builder()
				.draftNo(draft_no)
				.approTime(appro_time)
				.approTitle(appro_title)
				.approContent(appro_content)
				.oriFile(ori_file)
				.newFile(new_file)
				.consultDraftRoot(consult_draft_root)
				.approvalDraftRoot(approval_draft_root)
				.build();
	}
	
	 // Entity를 DTO로 변환하는 정적 메소드
	public ApproDraftDto toDto(ApproDraft approDraft) {
		return ApproDraftDto.builder()
				.draft_no(approDraft.getDraftNo())
				.emp_id(approDraft.getEmployee().getEmpId())
				.appro_time(approDraft.getApproTime())
				.appro_title(approDraft.getApproTitle())
				.appro_content(approDraft.getApproContent())
				.ori_file(approDraft.getOriFile())
				.new_file(approDraft.getNewFile())
				.consult_draft_root(approDraft.getConsultDraftRoot())
				.approval_draft_root(approDraft.getApprovalDraftRoot())
				.build();
	}
}
