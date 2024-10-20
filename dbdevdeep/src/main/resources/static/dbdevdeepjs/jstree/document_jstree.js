$(document).ready(function() {
    let folderData = {
        publicFolderList: [],
        privateFolderList: []
    };
	
	// 공용문서함과 개인문서함의 폴더 번호를 미리 정의
	const publicFolderNo = 1;  // 예: 공용업무폴더의 번호
	const privateFolderNo = 2;  // 예: 개인업무폴더의 번호
	
	// 페이지가 로드되었을 때 기본적으로 공용문서함 용량을 보여줌
	getFolderTotalSize(publicFolderNo);  // 공용문서함의 용량 계산
	
	if ($('#copyButton').length === 0) {
	    $('<button/>', {
	        id: 'copyButton',
	        text: '개인문서함으로 복사',
	        class: 'btn waves-effect waves-light btn-outline-secondary m-1',
	        style: 'width: 170px; height: 45px;',
	        click: function() {
				// 복사 모달창 열기 및 개인문서함 로드
				openCopyModal();
	        }
	    }).insertBefore('#uploadButton'); // '업로드' 버튼 앞에 추가
	}

    fetch('/api/folder-tree', {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        folderData.publicFolderList = addIconToItemsRecursively(data.publicFolderList);
        folderData.privateFolderList = addIconToItemsRecursively(data.privateFolderList);
        
        displayFolders(folderData.publicFolderList, 1);  // 기본적으로 공용문서함 표시
		selectDefaultFolder();  // 기본 폴더 선택
    })
    .catch(error => {
        console.error('Error loading folder tree:', error);
    });

    $('#public-tab').on('click', function() {
        displayFolders(folderData.publicFolderList, 1);
        $(this).addClass('active');
        $('#private-tab').removeClass('active');
		$('#storage_name').text('공용문서함');
		// 공용문서함 폴더 번호를 사용해 폴더 용량을 계산
		getFolderTotalSize(publicFolderNo);
		
		// 전체 및 개별 체크박스 해제
		$('#selectAllCheckbox').prop('checked', false);  // 전체 선택 체크박스 해제
		$('.folderAndFileCheckbox').prop('checked', false);  // 모든 개별 체크박스 해제
		
		if ($('#copyButton').length === 0) {
		    $('<button/>', {
		        id: 'copyButton',
		        text: '개인문서함으로 복사',
		        class: 'btn waves-effect waves-light btn-outline-secondary m-1',
		        style: 'width: 170px; height: 45px;',
		        click: function() {
					// 복사 모달창 열기 및 개인문서함 로드
					openCopyModal();
		        }
		    }).insertBefore('#uploadButton'); // '업로드' 버튼 앞에 추가
		}
    });

    $('#private-tab').on('click', function() {
        displayFolders(folderData.privateFolderList, 1);
        $(this).addClass('active');
        $('#public-tab').removeClass('active');
		$('#storage_name').text('개인문서함');
		// 개인문서함 폴더 번호를 사용해 폴더 용량을 계산
		getFolderTotalSize(privateFolderNo);
		
		// 전체 및 개별 체크박스 해제
		$('#selectAllCheckbox').prop('checked', false);  // 전체 선택 체크박스 해제
		$('.folderAndFileCheckbox').prop('checked', false);  // 모든 개별 체크박스 해제
		
		// '개인문서함으로 복사' 버튼이 존재하면 제거
		$('#copyButton').remove();
    });

    function displayFolders(folders, maxDepth) {
        $('#folder_list').jstree('destroy').empty();

        $('#folder_list').jstree({
            'core': {
                'data': folders,
                'themes': {
                    'icons': true
                }
            },
			'state': { 'opened': true }, // 트리의 모든 폴더가 열린 상태로 표시
            'types': {
                'default': {
                    'icon': '/assets/images/yellow_folder.png'
                },
                'file': {
                    'icon': '/assets/images/yellow_folder.png'
                }
            }
        }).on('ready.jstree', function() {
            let tree = $('#folder_list').jstree(true);
            tree.open_node(tree.get_node('#').children, function() {
                tree.get_node('#').children.forEach(function(node) {
                    openNodeToDepth(tree, node, maxDepth);
                });
            });
			
			// 폴더 트리가 준비된 후에 기본 폴더를 선택하도록 설정
			selectDefaultFolder();
			
        }).on('select_node.jstree', function(e, data) {
            const selectedFolderName = data.node.text; // 선택된 폴더 이름
			const folderNo = data.node.id; // 선택된 폴더 번호 (id)
            updateFolderName(selectedFolderName); // 폴더 이름 업데이트
            updateFolderPath(data.node); // 폴더 경로 업데이트
			
			// 테이블 초기화 후 새로운 데이터 로드
			clearTableAndLoadData(folderNo);
			
			// 폴더 번호도 숨겨진 필드에 저장
			$('#folder_no').val(folderNo); // 폴더 번호를 숨겨진 input에 저장
			$('#currentfolder_no').val(folderNo);
        });
    }
	
	// 복사 모달창 열기 및 개인문서함 폴더 목록 표시
	function openCopyModal() {
	    // 개인문서함 폴더 목록을 로드하여 jstree에 표시
	    $('#copy_folder_list').jstree('destroy').empty();
	    $('#copy_folder_list').jstree({
	        'core': {
	            'data': folderData.privateFolderList,
	            'themes': {
	                'icons': true
	            }
	        },
	        'types': {
	            'default': {
	                'icon': '/assets/images/yellow_folder.png'
	            },
	            'file': {
	                'icon': '/assets/images/yellow_folder.png'
	            }
	        }
	    }).on('ready.jstree', function() {
	        let tree = $('#copy_folder_list').jstree(true);
			
			// 모든 폴더를 펼침
			tree.open_all();
		}).on('select_node.jstree', function(e, data) {
		    const folderNo = data.node.id;

		    // 서버에 선택된 폴더 번호를 전송할 준비
		    $('#selectedCopyFolderNo').val(folderNo); // 폴더 번호를 숨겨진 input에 저장
		});
		
		const selectedFileNos = [];  // 파일 번호를 저장할 배열
        const selectedFolderNos = [];  // 폴더 번호를 저장할 배열
        
        // 체크된 폴더 및 파일 항목의 value 값 가져오기
        $('.folderAndFileCheckbox:checked').each(function() {
            const value = $(this).val();

            // 'file_'로 시작하는 경우 파일, 'folder_'로 시작하는 경우 폴더로 구분
            if (value.startsWith('file_')) {
                selectedFileNos.push(value.replace('file_', ''));  // 'file_' 제거 후 번호 저장
            } else if (value.startsWith('folder_')) {
				selectedFolderNos.push(value.replace('folder_', ''));  // 'folder_' 제거 후 번호 저장
			}
        });
		
		// 체크박스가 선택되지 않았을 때 경고 메시지 표시
		if (selectedFileNos.length === 0 && selectedFolderNos.length === 0) {
		    showAlert('warning', '경고', '복사할 폴더나 파일을 선택해주세요!');
		    return;
		}
		
		// 개인문서함의 현재 용량과 총 용량을 서버에서 가져와 비교
		fetch(`/api/privateTotalSize`)
		    .then(response => response.json())
		    .then(folderData => {
		        const currentFolderSize = folderData.usedSize; 
		        const totalFolderCapacity = folderData.totalCapacity;
				
		        // 복사할 파일들의 용량 계산
		        return fetch('/file/size', {
		            method: 'POST',
		            headers: {
		                'Content-Type': 'application/json',
		                'X-CSRF-TOKEN': $('#csrf_token').val()
		            },
					body: JSON.stringify(selectedFileNos)  // 파일 번호 배열을 JSON으로 전송
		        })
		        .then(response => response.json())
		        .then(fileSizes => {
					let totalCopyFileSize = fileSizes.reduce((acc, fileSize) => acc + fileSize, 0);  // 선택한 파일들의 총 용량

			            // 선택한 폴더 내 파일들의 용량도 서버에서 가져와 계산
			            if (selectedFolderNos.length > 0) {
			                return fetch('/folder/size', {
			                    method: 'POST',
			                    headers: {
			                        'Content-Type': 'application/json',
			                        'X-CSRF-TOKEN': $('#csrf_token').val()
			                    },
			                    body: JSON.stringify(selectedFolderNos)  // 폴더 번호 배열을 JSON으로 전송
			                })
			                .then(response => response.json())
			                .then(folderTotalSize => {
			                    totalCopyFileSize += folderTotalSize;  // 폴더 내 파일들의 크기를 총 용량에 더함

			                    // 개인문서함의 총 용량과 비교
			                    if (currentFolderSize + totalCopyFileSize > totalFolderCapacity) {
			                        showAlert('warning', '경고', '개인문서함의 용량을 초과하여 파일을 복사할 수 없습니다.');
			                        return;
			                    }

			                    // 복사 모달창을 열기
			                    $('#copyModal').modal('show');
			                });
			            } else {
			                // 폴더가 선택되지 않은 경우 바로 용량 비교
			                if (currentFolderSize + totalCopyFileSize > totalFolderCapacity) {
			                    showAlert('warning', '경고', '개인문서함의 용량을 초과하여 파일을 복사할 수 없습니다.');
			                    return;
			                }

			                // 복사 모달창을 열기
			                $('#copyModal').modal('show');
			            }
		        });
		    })
		    .catch(error => {
		        console.error('Error fetching folder size or file sizes:', error);
		        showAlert('error', '오류', '용량 정보를 불러오는 중 오류가 발생했습니다.');
		    });

	    // 복사 폴더 선택 및 서버 전송 로직 추가
	    $('#copyFrm').on('submit', function(e) {
	        e.preventDefault();
			
			// 폴더 선택이 안 된 경우 경고 메시지 표시
			const targetFolderNo = $('#selectedCopyFolderNo').val(); // 이동될 대상 폴더 번호
			if (!targetFolderNo) {
			    showAlert('warning', '경고', '복사할 대상 폴더를 선택해주세요!');
			    return;
			}

			// 이동 확인 메시지
            Swal.fire({
                title: '복사하시겠습니까?',
                text: '선택된 파일이 복사됩니다.',
                icon: 'warning',
                showCancelButton: true,
				showCancelButton: true,
				confirmButtonText: '확인',
				cancelButtonText: '취소',
				customClass: {
				    confirmButton: 'swal-custom-button'
				}
	        }).then((result) => {
	            if (result.isConfirmed) {
	                fetch('/file/copy', {
	                    method: 'POST',
	                    headers: {
	                        'Content-Type': 'application/json',
	                        'X-CSRF-TOKEN': $('#csrf_token').val()
	                    },
	                    body: JSON.stringify({
	                        targetFolderNo: targetFolderNo,
	                        fileNos: selectedFileNos,
							folderNos: selectedFolderNos
	                    })
	                })
	                .then(response => response.json())
	                .then(data => {
	                    if (data.file_res_code === '200' || data.folder_res_code === '200') {
	                        showAlert('success', '성공', '선택한 폴더나 파일이 성공적으로 복사되었습니다.', () => location.reload());
	                    } else {
	                        showAlert('error', '실패', '복사 중 오류가 발생했습니다.');
	                    }
	                })
	                .catch(error => {
	                    console.error('Error during copy:', error);
	                    showAlert('error', '실패', '복사 중 오류가 발생했습니다.');
	                });
	            }
	        });
	    });
	}
	
	// showAlert 함수 정의
	function showAlert(icon, title, text, callback = null) {
	    Swal.fire({
	        icon: icon,
	        title: title,
	        text: text,
	        confirmButtonText: '확인',
	        customClass: {
	            confirmButton: 'swal-custom-button'
	        }
	    }).then(() => {
	        if (callback) callback();
	    });
	}

    function openNodeToDepth(tree, node, maxDepth, currentDepth = 1) {
        if (currentDepth < maxDepth) {
            tree.open_node(node, function() {
                if (tree.get_node(node).children.length > 0) {
                    tree.get_node(node).children.forEach(function(childNode) {
                        openNodeToDepth(tree, childNode, maxDepth, currentDepth + 1);
                    });
                }
            });
        }
    }

    const addIconToItemsRecursively = (items) => {
        return items.map(item => {
            const updatedItem = {
                ...item,
                icon: '/assets/images/yellow_folder.png',
				// 여기서 item의 children이 올바르게 연결되어 있는지 확인
				children: item.children ? addIconToItemsRecursively(item.children) : []
            };
            if (item.children && item.children.length > 0) {
                updatedItem.children = addIconToItemsRecursively(item.children);
            }
            return updatedItem;
        });
    };

    // 폴더 이름을 업데이트하는 함수
    function updateFolderName(folderName) {
        $('div[style="font-size:30px;"]').text(folderName);
    }

    // 선택한 폴더의 경로를 업데이트하는 함수
    function updateFolderPath(node) {
        let tree = $('#folder_list').jstree(true);
        let path = tree.get_path(node, ' > ');  // 최상위부터 현재 폴더까지 경로
		
		// 경로가 비어 있지 않도록 최상위 폴더에 대한 경로 처리
		if (path === '') {
		    path = node.text;  // 최상위 폴더일 경우 폴더 이름을 직접 설정
		}
		
        $('#folderPath').text(path); // 폴더 위치 td에 경로 표시
		$('#parentFolderPath').text(path);
    }
	
	// 페이지 로드 시 기본적으로 '공용업무폴더'를 선택하도록 설정
	function selectDefaultFolder() {
	    let tree = $('#folder_list').jstree(true);
	    const firstNode = tree.get_node(tree.get_node('#').children[0]);  // 첫 번째 폴더 노드
	    if (firstNode) {
	        tree.select_node(firstNode);  // 첫 번째 폴더 자동 선택
	        updateFolderPath(firstNode);  // 폴더 경로 업데이트
	        $('#folder_no').val(firstNode.id);  // 폴더 번호 설정
	    }
	}
	
	function clearTableAndLoadData(folderId) {
	    if ($.fn.DataTable.isDataTable('#file_config')) {
	        $('#file_config').DataTable().clear().destroy();  // 테이블의 기존 데이터를 모두 지운 후 새 데이터를 로드
	    }

	    loadFoldersAndFiles(folderId).then(() => {
	        reloadDataTable();  // 테이블 다시 그리기
	    });
	}
	
	// 폴더 번호를 서버에 보내고 용량을 받아오는 함수
	function getFolderTotalSize(folderNo) {
		$('#spinner').show();
		
	    fetch(`/api/totalSize?folder_no=${folderNo}`)
	        .then(response => response.json())
	        .then(data => {
	            updateStorageInfo(data.usedSize);  // 받은 데이터를 기반으로 용량 정보 업데이트
	        })
	        .catch(error => {
	            console.error('Error fetching folder size:', error);
	        });
	}

	function loadFoldersAndFiles(folderId) {
	    return loadFolders(folderId)
	        .then(() => loadFiles(folderId));
	}

	function loadFolders(folderNo) {
	    return fetch(`/folderList?folder_no=${folderNo}`, {
	        method: 'GET'
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Network response was not ok');
	        }
	        return response.json();
	    })
	    .then(folders => {
	        populateFolderTable(folders);
	    })
	    .catch(error => {
	        console.error('Error fetching folders:', error);
	    });
	}

	function loadFiles(folderNo) {
	    return fetch(`/fileList?folder_no=${folderNo}`, {
	        method: 'GET'
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Network response was not ok');
	        }
	        return response.json();
	    })
	    .then(files => {
	        populateFileTable(files);

	    })
	    .catch(error => {
	        console.error('Error fetching files:', error);
	    });
	}
	
	function updateStorageInfo(totalSizeInBytes) {
		if (!isNaN(totalSizeInBytes)) {  // totalSizeInBytes가 NaN이 아니면 업데이트
		    const totalGB = 10;  // 총 10GB 용량
		    const usedGB = totalSizeInBytes / (1024 * 1024 * 1024);  // 바이트를 GB로 변환
		    const usedMB = totalSizeInBytes / (1024 * 1024);  // 바이트를 MB로 변환
		    const percentage = (usedGB / totalGB) * 100;  // 퍼센티지 계산

		    if (usedGB >= 1) {
		        $('#storage-info .used-space').text(usedGB.toFixed(2) + 'GB');  // GB로 표시
		    } else {
		        $('#storage-info .used-space').text(usedMB.toFixed(2) + 'MB');  // MB로 표시
		    }
		    $('#storage-info .progress-bar').css('width', percentage + '%');  // 프로그레스바 업데이트
		}
	}
	
	function populateFolderTable(folders) {
		    const tbody = $('#file_config tbody');
		    tbody.empty(); // 기존 내용 제거

		    // 폴더 추가
		    folders.forEach(folder => {
		        tbody.append(`
		            <tr>
		                <td><input type="checkbox" class="folderAndFileCheckbox" style="padding-top:5px;" value="folder_${folder.folder_no}"></td>
		                <td><img src="/assets/images/yellow_folder_icon.png" alt="FOLDER" width="20"></td>
		                <td>${folder.folder_name}</td>
		                <td></td> <!-- 크기 없음 -->
						<td>${folder.emp_name}</td>
		                <td>${formatDate(folder.reg_time)}</td>
		            </tr>
		        `);
		    });
		}

	function populateFileTable(files) {
	    const tbody = $('#file_config tbody');

		files.forEach(file => {
		    let fileIcon = ''; // 기본값 설정

		    // 파일 확장자에 따른 아이콘 결정
		    switch (file.file_extension.toLowerCase()) {
		        case '.pdf':
		            fileIcon = '<img src="/assets/images/pdf.png" alt="PDF" width="24">';
		            break;
		        case '.ppt':
		        case '.pptx':
		            fileIcon = '<img src="/assets/images/ppt.png" alt="PPT" width="24">';
		            break;
		        case '.xls':
					fileIcon = '<img src="/assets/images/xls.png" alt="XLS" width="24">';
					break;
		        case '.xlsx':
		            fileIcon = '<img src="/assets/images/xlsx.png" alt="XLSX" width="24">';
		            break;
				case '.hwp':
					fileIcon = '<img src="/assets/images/hwp.png" alt="HWP" width="24">';
					break;					
		        case '.doc':
					fileIcon = '<img src="/assets/images/doc.png" alt="Word" width="24">';
					break;
		        case '.docx':
		            fileIcon = '<img src="/assets/images/docx.png" alt="Word" width="24">';
		            break;
		        case '.txt':
		            fileIcon = '<img src="/assets/images/txt.png" alt="Text" width="24">';
		            break;
		        case '.jpg':
					fileIcon = '<img src="/assets/images/jpg.png" alt="JPG" width="24">';
					break;
		        case '.jpeg':
		            fileIcon = '<img src="/assets/images/jpeg.png" alt="JPEG" width="24">';
		            break;
		        case '.gif':
		            fileIcon = '<img src="/assets/images/gif.png" alt="GIF" width="24">';
		            break;
		        case '.png':
		            fileIcon = '<img src="/assets/images/png.png" alt="PNG" width="24">';
		            break;
		        case '.mp4':
		            fileIcon = '<img src="/assets/images/mp4.png" alt="MP4" width="24">';
		            break;
		        case '.mp3':
		            fileIcon = '<img src="/assets/images/mp3.png" alt="MP3" width="24">';
		            break;
		        case '.avi':
		            fileIcon = '<img src="/assets/images/avi.png" alt="AVI" width="24">';
		            break;
		        case '.zip':
		            fileIcon = '<img src="/assets/images/zip.png" alt="ZIP" width="24">';
		            break;
		        default:
		            fileIcon = '<img src="/path/to/icons/file.png" alt="File" width="24">'; // 기본 아이콘
		            break;
		    }

		    // 테이블 행 추가
		    tbody.append(`
		        <tr>
		            <td><input type="checkbox" class="folderAndFileCheckbox" style="padding-top:5px;" value="file_${file.file_no}"></td>
		            <td>${fileIcon}</td>
					<td>
					    <a href="/download/${file.file_no}" style="text-decoration:none; color: inherit;">
					        <span>${file.ori_file_name}</span>
					    </a>
					</td>
		            <td>${formatFileSize(file.file_size)}</td>
		            <td>${file.emp_name}</td>
		            <td>${formatDate(file.reg_time)}</td>
		        </tr>
		    `);
		});

	}
	
	function reloadDataTable() {
		if (!$.fn.DataTable.isDataTable('#file_config')) {
			$('#file_config').DataTable({
				// 화면 크기에 따라 컬럼 width 자동 조절
				"responsive": true,
				"autoWidth": false,  // 자동 너비 계산을 비활성화
				// 컬럼 width 비율 조절
				"columnDefs": [
					{ "width": "5%", "targets": 0, "className": "text-center", "orderable": false },
					{ "width": "7%", "targets": 1, "orderable": false },
					{ "width": "53%", "targets": 2 },
					{ "width": "10%", "targets": 3, "orderable": false },
					{ "width": "10%", "targets": 4 },		
					{ "width": "15%", "targets": 5 }
				],
				language: {
				    emptyTable: "데이터가 없습니다." // 빈 테이블일 때 메시지
				},
				// 정보 표시 해제
				info: false,
				searching: true,
				// DataTables의 DOM 구조를 재정의
				// 표시건수, 검색, 테이블, 페이징의 위치 재설정
				// (정보 표시 부분을 제외)
				"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
				// 페이지네이션 버튼을 전체 숫자와 함께 표시
				pagingType: 'full_numbers',
				// 페이지당 항목 수를 선택할 수 있는 옵션
				lengthMenu: [10, 25, 50, 100],
				// 기본 페이지당 항목 수
				pageLength: 10,
		
				// 페이징 관련 설정
				drawCallback: function(settings) {
					var api = this.api();  // DataTables API 객체
					var info = api.page.info();  // 현재 페이지 정보
					// 전체 페이지 수
					var totalPages = info.pages;
					// 현재 페이지 번호
					var currentPage = info.page;
					// 표시할 페이지 버튼 수
					var numButtons = 5;
					// 시작 페이지와 종료 페이지 번호 계산
					var startPage = Math.max(currentPage - Math.floor(numButtons / 2), 0);
					var endPage = Math.min(startPage + numButtons - 1, totalPages - 1);
					// endPage가 최대값에 도달한 경우 startPage 조정
					if (endPage - startPage < numButtons - 1) {
						startPage = Math.max(endPage - numButtons + 1, 0);
					}
					// 사용자 정의 페이지네이션 HTML 생성
					var paginationHtml = '<ul class="pagination">';
					paginationHtml += '<li class="paginate_button page-item ' + (currentPage === 0 ? 'disabled' : '') + '"><a class="page-link" href="#" tabindex="0"><<</a></li>';
					paginationHtml += '<li class="paginate_button page-item ' + (currentPage === 0 ? 'disabled' : '') + '"><a class="page-link" href="#" tabindex="0"><</a></li>';
					for (var i = startPage; i <= endPage; i++) {
						var isActive = i === currentPage ? 'active' : '';  // 현재 페이지에 'active' 클래스 적용
						paginationHtml += '<li class="paginate_button page-item ' + isActive + '"><a class="page-link" href="#" tabindex="0">' + (i + 1) + '</a></li>';
					}
					paginationHtml += '<li class="paginate_button page-item ' + (currentPage === totalPages - 1 ? 'disabled' : '') + '"><a class="page-link" href="#" tabindex="0">></a></li>';
					paginationHtml += '<li class="paginate_button page-item ' + (currentPage === totalPages - 1 ? 'disabled' : '') + '"><a class="page-link" href="#" tabindex="0">>></a></li>';
					paginationHtml += '</ul>';
					// 페이지네이션 컨테이너 업데이트
					$(api.table().container()).find('.dataTables_paginate').html(paginationHtml);
					// 클릭 이벤트 핸들러 추가
					$(api.table().container()).find('.paginate_button').on('click', function(e) {
						e.preventDefault();  // 기본 링크 동작 방지
						if ($(this).hasClass('disabled')) return;  // 비활성화된 버튼 클릭 방지
						var idx = $(this).find('a').text();  // 클릭된 버튼의 텍스트 가져오기
						if (idx === '<<') {
							api.page('first').draw('page');  // 첫 페이지로 이동
						} else if (idx === '<') {
							api.page('previous').draw('page');  // 이전 페이지로 이동
						} else if (idx === '>') {
							api.page('next').draw('page');  // 다음 페이지로 이동
						} else if (idx === '>>') {
							api.page('last').draw('page');  // 마지막 페이지로 이동
						} else {
							api.page(parseInt(idx) - 1).draw('page');  // 선택된 페이지로 이동
						}
					});
				},
				"initComplete": function () {
					var searchBoxWrapper = $('<div style="display: flex; justify-content: center; width: 100%;"></div>');
					var searchBoxContainer = $('<div class="custom-dataTables_filter" style="position: relative; display: flex; align-items: center; width: 100%; max-width: 500px; margin: 30px auto 0 auto;"></div>');
					var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="width: 100%; box-sizing: border-box; padding-right: 60px;">');
					var searchButton = $('<i class="fas fa-search" style="cursor: pointer; color: #0031AE; position: absolute; right: 0px; top: 4px; height: 80%; border: none; border-radius: 2px; margin: 0; padding: 0 16px; display: flex; align-items: center;"></i>');
	
					searchButton.on('click', function() {
						var searchTerm = searchInput.val();  // 검색어 가져오기
						$('#file_config').DataTable().search(searchTerm).draw();  // 검색어로 필터링
					});
	
					// Enter 키로 검색하기
					searchInput.on('keypress', function(e) {
						if (e.which === 13) {  // Enter 키 코드
							e.preventDefault();  // 기본 Enter 동작 방지
							searchButton.click();  // 검색 버튼 클릭 이벤트 호출
						}
					});
	
					searchBoxContainer.append(searchInput).append(searchButton);
					searchBoxWrapper.append(searchBoxContainer);
	
					// 페이징 밑에 검색 박스 추가
					$('.dataTables_paginate').after(searchBoxContainer);
	
					// DataTables 기본 검색창 숨기기
					$('div.dataTables_filter').hide();
				}
			})
		}
	}
	
	// 날짜 형식 변환 함수
	function formatDate(reg_time) {
	    const date = new Date(reg_time);
	    const year = date.getFullYear();
	    const month = String(date.getMonth() + 1).padStart(2, '0'); // 월을 두 자리로 맞춤
	    const day = String(date.getDate()).padStart(2, '0'); // 일을 두 자리로 맞춤
	    return `${year}.${month}.${day}`;
	}

	// 파일 크기 변환 함수 (바이트를 MB로 변환)
	function formatFileSize(file_size) {
	    const sizeInMB = file_size / (1024 * 1024); // 바이트를 MB로 변환
	    return `${sizeInMB.toFixed(2)} MB`; // 소수점 둘째 자리까지 표시
	}

});


