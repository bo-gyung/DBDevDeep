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
        };
        
    	// 웹소켓 메시지를 수신했을 때 호출
		socket.onmessage = function(event) {
		    console.log("서버로부터 메시지 수신: " + event.data);
		    /*displayMessage(event.data);*/
		    
		    const message = JSON.parse(event.data);
		    
		    if (message.alert) {
		        const alert = message.alert;
		       
		        const alertHtml = `
		        	<a href="javascript:void(0)" class="message-item d-flex align-items-center border-bottom px-3 py-2"
		        		data-alert-no="${alert.id}"
		        		onclick="alertMoveFunc('${alert.reference_name}', ${alert.id});">
			        	<div class="btn btn-info rounded-circle btn-circle">
		              	<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-info text-white">
			        		<circle cx="12" cy="12" r="10"></circle>
			        		<line x1="12" y1="16" x2="12" y2="12"></line>
			        		<line x1="12" y1="8" x2="12" y2="8"></line>
			        	</svg>
		            </div>
		            <div class="w-75 d-inline-block v-middle pl-2">
		                <h6 class="message-title mb-0 mt-1">[${alert.title}]</h6>
		                <span class="font-12 text-nowrap d-block text-muted text-truncate">
		                    ${alert.content}
		                </span>
		                <span class="font-12 text-nowrap d-block text-muted">
		                    ${new Date(alert.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
		                </span>
		            </div>
		          </a>
		        `;
		
		        // 원하는 위치에 공지 추가 (예: #alert-container라는 ID의 요소)
		        document.getElementById('alertDiv').innerHTML += alertHtml;
		        
		        const alertDiv = document.getElementById('alertDiv');
       	
		   		// alertDiv 안의 a 태그를 모두 선택
		   		const anchorTags = alertDiv.getElementsByTagName('a');
		   	
		   		// a 태그의 개수 출력
		   		const count = anchorTags.length;
		   		
		   		document.getElementById("alertNum").innerText = count;
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

    // 연결 상태 업데이트 함수
    function updateStatus(message, color) {
        const statusElement = document.getElementById("status");
        if (statusElement) {
            statusElement.innerText = message;
            statusElement.style.color = color;
        }
    }

    // 페이지를 떠날 때 웹소켓 연결 종료
    window.onbeforeunload = function() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            // 웹소켓 연결 종료.
            socket.close();
        }
    };

    // 페이지 로드 시 웹소켓 연결 시도
    // window.onload를 사용하지 않고 DOMContentLoaded를 사용하여 모든 리소스가 로드된 후 실행
    document.addEventListener("DOMContentLoaded", function() {
        connectWebSocket();
    });
    
    
})(); 