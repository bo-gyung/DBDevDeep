package com.dbdevdeep.document.domain;

import java.util.List;

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
public class CopyRequestDto {
	private List<Long> fileNos;     // 파일 번호 리스트
	private List<Long> folderNos;
	private Long targetFolderNo;    // 이동될 폴더 번호
}
