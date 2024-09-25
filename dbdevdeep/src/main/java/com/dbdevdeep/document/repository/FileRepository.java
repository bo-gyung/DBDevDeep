package com.dbdevdeep.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.document.domain.FileEntity;
import com.dbdevdeep.document.domain.Folder;

public interface FileRepository extends JpaRepository<FileEntity, Long>{

	List<FileEntity> findByFolder(Folder folder);
	
	// 특정 폴더의 파일 크기 합산 쿼리
	@Query("SELECT SUM(f.fileSize) FROM FileEntity f WHERE f.folder.folderNo = :folderNo")
	Long getTotalFileSizeByFolderNo(@Param("folderNo") Long folderNo);

	FileEntity findByFileNo(Long fileNo);

	List<FileEntity> findByFolderAndEmployee_EmpId(Folder folder, String empId);

	@Query("SELECT SUM(f.fileSize) FROM FileEntity f WHERE f.folder.folderNo = :folderNo AND f.employee.empId = :empId")
	Long getTotalFileSizeByFolderNoAndEmployee_EmpId(@Param("folderNo") Long folderNo, @Param("empId") String empId);

}
