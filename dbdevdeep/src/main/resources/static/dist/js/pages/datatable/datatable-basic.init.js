
/*************************************************************************************/
// -->Template Name: Bootstrap Press Admin
// -->Author: Themedesigner
// -->Email: niravjoshi87@gmail.com
// -->File: datatable_basic_init
/****************************************
 *       1. Notice Table                   *
 ****************************************/
$('#notice_config').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	 "responsive": true,
	 // 컬럼 width 비율 조절
	 "columnDefs": [
        { "width": "0%", "targets": 0 },
        { "width": "5%", "targets": 1 },
        { "width": "5%", "targets": 2 },
        { "width": "10%", "targets": 3 },
        { "width": "60%", "targets": 4 },
        { "width": "10%", "targets": 5 },
        { "width": "10%", "targets": 6 }
    ],
	"order": [[2, "desc"]], 

	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
	// 페이지네이션 버튼을 전체 숫자와 함께 표시
	pagingType: 'full_numbers',
	// 페이지당 항목 수를 선택할 수 있는 옵션
	lengthChange: false,
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
	"initComplete": function() {
		var searchBoxContainer = $('<div class="custom-dataTables_filter" style="display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 30px;"></div>');
		var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="height: 46px; padding: 8px 12px; width: 300px; box-sizing: border-box;">');
		var searchButton = $('<button class="btn btn-primary ml-2" style="height:46px;">검색</button>');

			searchButton.on('click', function () {
				var searchTerm = searchInput.val();  // 검색어 가져오기
				$('#notice_config').DataTable().search(searchTerm).draw();  // 검색어로 필터링
			});

		searchBoxContainer.append(searchInput).append(searchButton);

		// 페이징 밑에 검색 박스 추가
		$('.dataTables_paginate').after(searchBoxContainer);

		// DataTables 기본 검색창 숨기기
		$('div.dataTables_filter').hide();
	}
});

/****************************************
 * 2. holiday_config : 각자의 테이블 옵션 설정하기 *
 ****************************************/
$('#holiday_config').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "5%", "targets": 0, "className": "text-center", "orderable": false },
		{ "width": "7%", "targets": 1 },
		{ "width": "53%", "targets": 2 },
		{ "width": "30%", "targets": 3 },
		{ "width": "10%", "targets": 4, "orderable": false }
	],
	"order": [[1, "desc"]], // 여기서 2는 'start_date'가 위치한 컬럼 인덱스
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
			var searchBoxContainer = $('<div class="custom-dataTables_filter" style="display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 30px;"></div>');
			var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="height: 46px; padding: 8px 12px; width: 300px; box-sizing: border-box;">');
			var searchButton = $('<button class="btn btn-primary ml-2" style="height:46px; width:72px;">검색</button>');

		searchButton.on('click', function() {
			var searchTerm = searchInput.val();  // 검색어 가져오기
			$('#holiday_config').DataTable().search(searchTerm).draw();  // 검색어로 필터링
		});

		searchBoxContainer.append(searchInput).append(searchButton);

		// 페이징 밑에 검색 박스 추가
		$('.dataTables_paginate').after(searchBoxContainer);

		// DataTables 기본 검색창 숨기기
		$('div.dataTables_filter').hide();
	}
});

/******************************************
 * 			3. address_book Table
 * ****************************************/
// 주소록 페이징
$('#address_book').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "10%", "targets": 0 },
		{ "width": "10%", "targets": 1 },
		{ "width": "10%", "targets": 2 },
		{ "width": "10%", "targets": 3 },
		{ "width": "10%", "targets": 4 },
		{ "width": "10%", "targets": 5 },
		{ "width": "10%", "targets": 6 },
		{ "width": "10%", "targets": 7 },
		{ "width": "20%", "targets": 8 }
	],
	"order": [[0, "asc"]],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
	// 페이지네이션 버튼을 전체 숫자와 함께 표시
	pagingType: 'full_numbers',
	// 페이지당 항목 수를 선택할 수 있는 옵션
	lengthChange: false,
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
	"initComplete": function() {
		var searchBoxWrapper = $('<div style="display: flex; justify-content: center; width: 100%;"></div>');
		var searchBoxContainer = $('<div class="custom-dataTables_filter" style="position: relative; display: flex; align-items: center; width: 100%; max-width: 500px; margin: 15px auto 0 auto;"></div>');
		var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="width: 100%; box-sizing: border-box; padding-right: 60px;">');
		var searchButton = $('<button class="btn btn-primary ml-2" style="position: absolute; right: 5px; top: 4px; height: 80%; border: none; border-radius: 2px; margin: 0; padding: 0 16px; display: flex; align-items: center;">검색</button>');

		searchButton.on('click', function() {
			var searchTerm = searchInput.val();  // 검색어 가져오기
			$('#address_book').DataTable().search(searchTerm).draw();  // 검색어로 필터링
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
});

// 직원 목록
$('#address_all_book').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "10%", "targets": 0, "className": "text-center" },
		{ "width": "10%", "targets": 1, "className": "text-center" },
		{ "width": "10%", "targets": 2, "className": "text-center" },
		{ "width": "10%", "targets": 3, "className": "text-center" },
		{ "width": "10%", "targets": 4, "className": "text-center" },
		{ "width": "10%", "targets": 5, "className": "text-center" },
		{ "width": "10%", "targets": 6, "className": "text-center" },
		{ "width": "20%", "targets": 7, "className": "text-center" }
	],
	"order": [[0, "asc"]],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
	// 페이지네이션 버튼을 전체 숫자와 함께 표시
	pagingType: 'full_numbers',
	// 페이지당 항목 수를 선택할 수 있는 옵션
	lengthChange: false,
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
	"initComplete": function() {
		var searchBoxWrapper = $('<div style="display: flex; justify-content: center; width: 100%;"></div>');
		var searchBoxContainer = $('<div class="custom-dataTables_filter" style="position: relative; display: flex; align-items: center; width: 100%; max-width: 500px; margin: 15px auto 0 auto;"></div>');
		var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="width: 100%; box-sizing: border-box; padding-right: 60px;">');
		var searchButton = $('<button class="btn btn-primary ml-2" style="position: absolute; right: 5px; top: 4px; height: 80%; border: none; border-radius: 2px; margin: 0; padding: 0 16px; display: flex; align-items: center;">검색</button>');

		searchButton.on('click', function() {
			var searchTerm = searchInput.val();  // 검색어 가져오기
			$('#address_all_book').DataTable().search(searchTerm).draw();  // 검색어로 필터링
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
});

// 직원 목록 이동 이벤트
$('#address_all_book').on('click', '.employee-detail', function() {
	var emp_id = $(this).data('emp');
	location.href = "/employee/detail/" + emp_id;
});

// log-employee
$('#log-employee').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "10%", "targets": 0, "className": "text-center" },
		{ "width": "10%", "targets": 1, "className": "text-center" },
		{ "width": "10%", "targets": 2, "className": "text-center" },
		{ "width": "10%", "targets": 3, "className": "text-center" },
		{ "width": "10%", "targets": 4, "className": "text-center" },
		{ "width": "10%", "targets": 5, "className": "text-center" },
		{ "width": "10%", "targets": 6, "className": "text-center" }
	],
	"order": [[0, "asc"]],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
	// 페이지네이션 버튼을 전체 숫자와 함께 표시
	pagingType: 'full_numbers',
	// 페이지당 항목 수를 선택할 수 있는 옵션
	lengthChange: false,
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
	"initComplete": function() {
		var searchBoxWrapper = $('<div style="display: flex; justify-content: center; width: 100%;"></div>');
		var searchBoxContainer = $('<div class="custom-dataTables_filter" style="position: relative; display: flex; align-items: center; width: 100%; max-width: 500px; margin: 15px auto 0 auto;"></div>');
		var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="width: 100%; box-sizing: border-box; padding-right: 60px;">');
		var searchButton = $('<button class="btn btn-primary ml-2" style="position: absolute; right: 5px; top: 4px; height: 80%; border: none; border-radius: 2px; margin: 0; padding: 0 16px; display: flex; align-items: center;">검색</button>');

		searchButton.on('click', function() {
			var searchTerm = searchInput.val();  // 검색어 가져오기
			$('#log-employee').DataTable().search(searchTerm).draw();  // 검색어로 필터링
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
});

// 직원 정보 변경 기록 이동 이벤트
$('#log-employee').on('click', '.log-employee', function() {
	var audit_no = $(this).data('audit_no');
	location.href = "/log/employee/" + audit_no;
});

$('#employee_teacher_history_list').DataTable({
	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "10%", "targets": 0, "className": "text-center" },
		{ "width": "10%", "targets": 1, "className": "text-center" },
		{ "width": "10%", "targets": 2, "className": "text-center" },
		{ "width": "10%", "targets": 3, "className": "text-center" }
	],
	"order": [[0, "asc"]],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
	// 페이지네이션 버튼을 전체 숫자와 함께 표시
	pagingType: 'full_numbers',
	// 페이지당 항목 수를 선택할 수 있는 옵션
	lengthChange: false,
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
	"initComplete": function() {
		var searchBoxContainer = $('<div class="custom-dataTables_filter" style="display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 30px;"></div>');
		var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="height: 46px; padding: 8px 12px; width: 300px; box-sizing: border-box;">');
		var searchButton = $('<button class="btn btn-primary ml-2" style="height:46px;">검색</button>');

		searchButton.on('click', function() {
			var searchTerm = searchInput.val();  // 검색어 가져오기
			$('#employee_teacher_history_list').DataTable().search(searchTerm).draw();  // 검색어로 필터링
		});

		searchBoxContainer.append(searchInput).append(searchButton);

		// 페이징 밑에 검색 박스 추가
		$('.dataTables_paginate').after(searchBoxContainer);

		// DataTables 기본 검색창 숨기기
		$('div.dataTables_filter').hide();
	}
});

/******************************************
 * 			4. Approve Table
 * ****************************************/

$('#approve_config').DataTable({

	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "60%", "targets": 0 },
		{ "width": "20%", "targets": 1 },
		{ "width": "10%", "targets": 2 },
		{ "width": "10%", "targets": 3 }
	],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"pull-right"f><"clearfix">>>t<"row view-pager"<"col-sm-12"<ip>>>',
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
	}
});
// 보고서 테이블 
$('#approveDocu_config').DataTable({

	// 화면 크기에 따라 컬럼 width 자동 조절
	"responsive": true,
	// 컬럼 width 비율 조절
	"columnDefs": [
		{ "width": "60%", "targets": 0 },
		{ "width": "20%", "targets": 1 },
		{ "width": "20%", "targets": 2 }
	],
	// 정보 표시 해제
	info: false,
	// DataTables의 DOM 구조를 재정의
	// 표시건수, 검색, 테이블, 페이징의 위치 재설정
	// (정보 표시 부분을 제외)
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"pull-right"f><"clearfix">>>t<"row view-pager"<"col-sm-12"<ip>>>',
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
	}
});

/******************************************
 * 			5. class_by_year
 * ****************************************/
$('#class_by_year').DataTable({
	info: false,  // 테이블의 정보 표시를 비활성화합니다.
	"sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"pull-right"f><"clearfix">>>t<"row view-pager"<"col-sm-12"<ip>>>',
	pagingType: 'full_numbers',  // 페이지네이션 버튼을 전체 숫자와 함께 표시합니다.
	lengthMenu: [10, 25, 50, 100],  // 페이지당 항목 수를 선택할 수 있는 옵션
	pageLength: 10,  // 기본 페이지당 항목 수
	drawCallback: function(settings) {
		var api = this.api();  // DataTables API 객체를 가져옵니다.
		var info = api.page.info();  // 현재 페이지 정보 가져오기

		// 전체 페이지 수를 가져옵니다.
		var totalPages = info.pages;

		// 현재 페이지 번호를 가져옵니다.
		var currentPage = info.page;

		// 표시할 페이지 버튼 수
		var numButtons = 5;

		// 시작 페이지와 종료 페이지 번호를 계산합니다.
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

		// 페이지네이션 컨테이너를 업데이트합니다.
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
	}
});
/******************************************
 * 			place Table
 * ****************************************/
$('#place_list').DataTable({ 
	
	// 화면 크기에 따라 컬럼 width 자동 조절
	 "responsive": true,	 // 컬럼 width 비율 조절
	 "columnDefs": [
        { "width": "5%",  "targets": 0 },
        { "width": "20%", "targets": 1 },
        { "width": "15%", "targets": 2 },
        { "width": "25%", "targets": 3 },
        { "width": "10%", "targets": 4 },
        { "width": "25%", "targets": 5 }
    ],
    "order": [[0, "desc"]], 
	
  info: false,  // 테이블의 정보 표시를 비활성화
  "sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
  pagingType: 'full_numbers',  // 페이지네이션 버튼을 전체 숫자와 함께 표시
  lengthChange: false,  // 페이지당 항목 수를 선택할 수 있는 옵션
  pageLength: 10,  // 기본 페이지당 항목 수 // test
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
var searchBoxContainer = $('<div class="custom-dataTables_filter" style="display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 30px;"></div>');
var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="height: 46px; padding: 8px 12px; width: 300px; box-sizing: border-box;">');
var searchButton = $('<button class="btn btn-primary ml-2" style="height:46px;">검색</button>');

searchButton.on('click', function () {
var searchTerm = searchInput.val();  // 검색어 가져오기
$('#place_list').DataTable().search(searchTerm).draw();  // 검색어로 필터링
});

searchBoxContainer.append(searchInput).append(searchButton);

			// 페이징 밑에 검색 박스 추가
$('.dataTables_paginate').after(searchBoxContainer);
			// DataTables 기본 검색창 숨기기
$('div.dataTables_filter').hide();
}
});
/******************************************
 * 			item Table
 * ****************************************/
$('#item_list').DataTable({ 
	
	// 화면 크기에 따라 컬럼 width 자동 조절
	 "responsive": true,	 // 컬럼 width 비율 조절
	 "columnDefs": [
        { "width": "15%", "targets": 0 },
        { "width": "15%", "targets": 1 },
        { "width": "15%", "targets": 2 },
        { "width": "15%", "targets": 3 },
        { "width": "10%", "targets": 4 },
        { "width": "15%", "targets": 5 },
        { "width": "15%", "targets": 6 }
    ],
     
	
  info: false,  // 테이블의 정보 표시를 비활성화
  "sDom": '<"row view-filter"<"col-sm-12"<"pull-left"l><"clearfix">>>t<"row view-pager"<"col-sm-12"<ipf>>>',
  pagingType: 'full_numbers',  // 페이지네이션 버튼을 전체 숫자와 함께 표시
  lengthChange: false,  // 페이지당 항목 수를 선택할 수 있는 옵션
  pageLength: 10,  // 기본 페이지당 항목 수 // test
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
var searchBoxContainer = $('<div class="custom-dataTables_filter" style="display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 30px;"></div>');
var searchInput = $('<input type="text" class="form-control" placeholder="검색어를 입력해주세요" style="height: 46px; padding: 8px 12px; width: 300px; box-sizing: border-box;">');
var searchButton = $('<button class="btn btn-primary ml-2" style="height:46px;">검색</button>');

searchButton.on('click', function () {
var searchTerm = searchInput.val();  // 검색어 가져오기
$('#item_list').DataTable().search(searchTerm).draw();  // 검색어로 필터링
});

searchBoxContainer.append(searchInput).append(searchButton);

			// 페이징 밑에 검색 박스 추가
$('.dataTables_paginate').after(searchBoxContainer);
			// DataTables 기본 검색창 숨기기
$('div.dataTables_filter').hide();
}
});
/******************************************
 * 			Custom Table
 * ****************************************/

$('#custom_config').DataTable({
	// 필요 옵션 입력
	// 없으면 기본 설정된 값으로 생성됩니다.
});
