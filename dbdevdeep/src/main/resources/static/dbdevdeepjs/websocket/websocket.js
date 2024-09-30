// 전역 변수로 웹소켓 연결 상태 관리
let socket;

(function() {
    // 전역에서 접근할 수 있도록 window 객체에 할당
    window.connectWebSocket = function() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            // 연결된 웹소켓 존재
            return;
        }

        socket = new WebSocket("ws://localhost:8094/ws");

        // 웹소켓 연결이 열렸을 때 호출
        socket.onopen = function(event) {
            updateStatus("연결 상태: 연결됨", "green");
            // 현재 페이지 정보를 서버에 전송
        	sendPageInfo();
        };
        
    	// 웹소켓 메시지를 수신했을 때 호출
		socket.onmessage = function(event) {
		    
		    const message = JSON.parse(event.data);
		    
		    if (message.alert) {
		        const alert = message.alert;
		        
		        const alertRefName = alert.reference_name;
		        
		        const alertStatus = alert.alarm_status;
		        
		        if(alertStatus == 'N') {
	        	    let title
	        	    let content
	        	   
	        	    if(alertRefName == 'approve') {
	        	 	   content = '[' + alert.alarm_title + '] ' + '\'' + alert.alarm_content + '\' 결재' + alert.alarm_title.split(' ')[2] + '이 있습니다.';
	        	 	   title = '결재'
	        	    } else if(alertRefName == 'notice') {
						content = '[' + alert.alarm_title + '] ' + '\'' + alert.alarm_content + '\' 공지가 있습니다.';
	        	 	   title = '공지';
	        	    }
			       
			        const alertHtml = `
			        	<a href="javascript:void(0)" class="message-item d-flex align-items-center border-bottom px-3 py-2 " style="cursor: auto;" data-a-alert-no="${alert.alarm_no}">
				        	<div data-alert-no="${alert.alarm_no}"
				        		onclick="alertMoveFunc('${alert.reference_name}', '${alert.alarm_no}', '${alert.reference_no}');"
				        		onmouseover="this.style.cursor='pointer'"
				        		onmouseout="this.style.curosr='auto'"
			        			class="w-75 message-item d-flex align-items-center">
					        	<div class="btn btn-info rounded-circle btn-circle">
						        	<i cloass="far fa-file-alt"></i>
						        	<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-info text-white">
						        		<circle cx="12" cy="12" r="10"></circle>
						        		<line x1="12" y1="16" x2="12" y2="12"></line>
						        		<line x1="12" y1="8" x2="12" y2="8"></line>
						        	</svg>
					            </div>
					            <div class="w-100 d-inline-block v-middle pl-2">
					                <h6 class="message-title mb-0 mt-1">[${title}]</h6>
					                <span class="font-12 text-nowrap d-block text-muted text-truncate">
					                    ${content}
					                </span>
					                <span class="font-12 text-nowrap d-block text-muted">
					                    ${new Date(alert.alarm_time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
					                </span>
					            </div>
				        	</div>
				            <div class="d-inline-block v-middle text-right" style="color: lightgray; width: 25% !important;"
				            	data-alert-no="${alert.alarm_no}"
				        		onclick="alertDeleteFunc('${alert.reference_name}', ${alert.alarm_no});"
			        			onmouseover="this.style.color='#0031AE'; this.style.cursor='pointer'"
				        		onmouseout="this.style.color='lightgray'">
				            	<i class="fas fa-times" style="margin-top: 6px; font-size: 20px;"></i>
				            </div>
			          </a>
			        `;
			        
			
			        // 원하는 위치에 공지 추가 (예: #alert-container라는 ID의 요소)
			        document.getElementById('alertDiv').innerHTML += alertHtml;
			        
			        const alertDiv = document.getElementById('alertDiv');
				} else if(alertStatus == 'X') {
					const alertLink = document.querySelector(`a[data-a-alert-no="${alert.alarm_no}"]`);
                    if (alertLink) {
                        alertLink.remove();
                    }
				}
				
		   		// alertDiv 안의 a 태그를 모두 선택
		   		const anchorTags = alertDiv.getElementsByTagName('a');
		   	
		   		// a 태그의 개수 출력
		   		const count = anchorTags.length;
		   		
		   		document.getElementById("alertNum").innerText = count == 0? '' : count;
		   		
		    } else if(message.res_code =='200' && message.res_type =='chat'){
				// 웹소켓 채팅 관련 이벤트
				
				if(message.now_page =='chat'){
					// 채팅 페이지에 접속한 사용자 : 채팅방 목록 리로드
					updateChatRoomList();
					
					if(message.room_in =='Y'){
						// 현재 채팅방에 접속해있는 사용자 : 채팅 메세지목록 리로드
						loadChatroom(message.room_no);
						
					} else if(message.room_in =='N'){
						// 현재 채팅방에 접속하고 있지 않은 사용자 : 채팅방 목록에 배지알림
						
					}
				} else if(message.now_page =='no_chat'){
					// 채팅 페이지에 접속하고 있지 않은 사용자 : 헤더에 배지알림
					
				}
			}
		};


        // 웹소켓 연결이 닫혔을 때 호출
        socket.onclose = function(event) {
            updateStatus("연결 상태: 끊어짐", "red");
        };

        // 웹소켓 에러가 발생했을 때 호출
        socket.onerror = function(error) {
            updateStatus("연결 상태: 에러 발생", "orange");
        };
    }

    // 헤더의 연결 상태 업데이트 함수
    function updateStatus(message, color) {
        const statusElement = document.getElementById("status");
        if (statusElement) {
            statusElement.innerText = message;
            statusElement.style.color = color;
        }
    }
    
    // 현재 페이지 정보를 WebSocket을 통해 서버에 전송하는 함수
	function sendPageInfo() {
	    const pageUrl = window.location.pathname; // 현재 페이지 경로 가져오기
	
	    // WebSocket 연결이 열려 있는 경우에만 정보 전송
	    if (socket && socket.readyState === WebSocket.OPEN) {
	        socket.send(JSON.stringify({
	            type: "PAGE_INFO", // 메시지 타입
	            pageUrl: pageUrl   // 현재 페이지 경로 정보
	        }));
	    }
	}

    // 페이지를 떠날 때 웹소켓 연결 종료
    window.onbeforeunload = function() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            // 웹소켓 연결 종료.
		    // WebSocket 연결이 열려 있는 경우에만 정보 전송
		    if (socket && socket.readyState === WebSocket.OPEN) {
		        socket.send(JSON.stringify({
		            type: "ROOM_NO", // 메시지 타입
		            roomNo: 0   // 현재 페이지 경로 정보
		        }));
		    }
            socket.close();
        }
    };

    // 페이지 로드 시 웹소켓 연결 시도
    // window.onload를 사용하지 않고 DOMContentLoaded를 사용하여 모든 리소스가 로드된 후 실행
    document.addEventListener("DOMContentLoaded", function() {
        connectWebSocket();
    });
    
    
})(); 