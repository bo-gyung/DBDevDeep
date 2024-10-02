package com.dbdevdeep.document.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.document.domain.FileEntity;
import com.dbdevdeep.document.domain.Folder;

public interface FileRepository extends JpaRepository<FileEntity, Long>{

	List<FileEntity> findByFolder(Folder folder);

	FileEntity findByFileNo(Long fileNo);

	List<FileEntity> findByFolderAndEmployee_EmpId(Folder folder, String empId);
	
	// 공용문서함 및 하위 폴더 파일 크기 합산 (네이티브 쿼리 사용)
	@Query(value = "WITH RECURSIVE folder_hierarchy AS (" +
	               "   SELECT folder_no FROM folder WHERE folder_no = :folderNo" +
	               "   UNION ALL" +
	               "   SELECT f.folder_no FROM folder f " +
	               "   INNER JOIN folder_hierarchy fh ON f.parent_folder_no = fh.folder_no" +
	               ")" +
	               "SELECT COALESCE(SUM(f.file_size), 0) " +
	               "FROM file f " +
	               "WHERE f.folder_no IN (SELECT folder_no FROM folder_hierarchy)", nativeQuery = true)
	Long getTotalFileSizeWithSubFolders(@Param("folderNo") Long folderNo);

	// 개인문서함 및 하위 폴더의 파일 크기 합산 (empId로 필터링)
	@Query(value = "WITH RECURSIVE folder_hierarchy AS (" +
	               "   SELECT folder_no FROM folder WHERE folder_no = :folderNo" +
	               "   UNION ALL" +
	               "   SELECT f.folder_no FROM folder f " +
	               "   INNER JOIN folder_hierarchy fh ON f.parent_folder_no = fh.folder_no" +
	               ")" +
	               "SELECT COALESCE(SUM(f.file_size), 0) " +
	               "FROM file f " +
	               "WHERE f.folder_no IN (SELECT folder_no FROM folder_hierarchy) AND f.emp_id = :empId", nativeQuery = true)
	Long getTotalFileSizeWithSubFoldersByEmpId(@Param("folderNo") Long folderNo, @Param("empId") String empId);

	List<FileEntity> findByFolder_FolderTypeOrderByRegTimeDesc(int folderType, Pageable pageable);

}
