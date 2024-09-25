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
		    console.log("서버로부터 메시지 수신: " + event.data);
		    /*displayMessage(event.data);*/
		    
		    const message = JSON.parse(event.data);
		    
		    if (message.alert) {
		        const alert = message.alert;
		       
		        const alertHtml = `
		        	<a href="javascript:void(0)" class="message-item d-flex align-items-center border-bottom px-3 py-2">
			        	<div class="btn btn-danger rounded-circle btn-circle">
		              <i data-feather="info" class="text-white"></i>
		            </div>
		            <div class="w-75 d-inline-block v-middle pl-2">
		                <h6 class="message-title mb-0 mt-1">[${alert.title}]</h6>
		                <span class="font-12 text-nowrap d-block text-muted text-truncate">
		                    '${alert.content}'
		                </span>
		                <span class="font-12 text-nowrap d-block text-muted">
		                    ${new Date(alert.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
		                </span>
		            </div>
		          </a>
		        `;
		
		        // 원하는 위치에 공지 추가 (예: #alert-container라는 ID의 요소)
		        document.getElementById('alertDiv').innerHTML += alertHtml;
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