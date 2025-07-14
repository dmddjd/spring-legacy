<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />

	<div class="content">
		<div class="content-1">
			<h3>회원 정보 조회</h3>

			<p>아이디를 입력 받아 일치하는 회원 정보를 출력</p>

			아이디 : <input type="text" id="in1">
			<button id="select1">조회</button>
			<div id="result1" style="height: 150px"></div>
		</div>
		<!-- 
		stomp서비스 실습
		  관리자가 알람내용을 입력 후 "보내기" 버튼 클릭시 모든 사용자에게 알림 내용이 출력되도록 설정
		   - 클라이언트는 전달받은 메시지를 alertify라이브러리를 이용하여 출력
		   - 구독 url은 /topic/notice로 설정
		   - 사용자가 어떤페이지에 있던 알람을 받을 수 있도록 설정
		 -->
		<sec:authorize access="hasRole('ROLE_ADMIN')">
			<div class="content-2">
				<input type="text" id="notice" placeholder="공지내용을입력하세요.">
				<button id="send-notice-btn">공지보내기</button>
			</div>
		</sec:authorize>
	</div>
	
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	<script>
		$(function(){
			const webSocket = new SockJS("${contextPath}/stomp");
			const stompClient = Stomp.over(webSocket);
			
			stompClient.connect({} , function(){
				const sendBtn = document.querySelector("#send-notice-btn");
				sendBtn.onclick = function(){
					const content = document.querySelector("#notice").value;
					stompClient.send("/app/notice/send",{},content);
				}
			});
			
		});
	</script>
	</sec:authorize>	
	
	
	
	
	
	
	
	
	
	
	

	<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
</body>
</html>