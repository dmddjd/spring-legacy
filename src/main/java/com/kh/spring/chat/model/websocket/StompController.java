package com.kh.spring.chat.model.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
//@RequiredArgsConstructor
public class StompController {
	
	/*
	 * SimpMessagingTemplate
	 *  - 서버에서 특정 클라이언트에게 메시지를 전송하기 위한 STOMP 템플릿
	 *  - STOMP 구독경로로 메시지를 전송할 수 있다.
	 *  convertAndSend : 전체사용자에게 메시지를 보낼때
	 *  convertAndSendToUser : 특정 사용자에게 메시지를 보낼때
	 *  */
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private ChatService service;
	
	/*
	 * @MessageMapping(destination경로)
	 *  - 클라이언트가 websocket을 통해 지정한 Destination경로를 매핑하는 속성
	 * 
	 * @Payload
	 *  - STOMP의 바디영역의 데이터를 VO클래스로 바인딩해주는 속성 
	 * 
	 * @SendTo
	 *  */
	@MessageMapping("/chat/enter/{roomNo}")
	@SendTo("/topic/room/{roomNo}") // 구독 url지정
	public ChatMessage handleEnter(
			@DestinationVariable int roomNo,
			@Payload ChatMessage message
			) {
		message.setType(ChatMessage.MessageType.ENTER);
		message.setMessage(message.getUserName()+"님이 입장하셨습니다.");
		// 필요하다면 서비스로직 호출하여 db에 내용 저장
		
		// 브로커에게 메시지 템플릿 전송
		//messagingTemplate.convertAndSend("/topic/room/"+roomNo,message);
		return message;
	}
	
	
	@MessageMapping("/chat/exit/{roomNo}")
	@SendTo("/topic/room/{roomNo}")
	public ChatMessage handleExit(
			@DestinationVariable int roomNo , 
			@Payload ChatMessage message
			) {
		// 1. 참여자정보 삭제.
		service.exitChatRoom(message);
		
		// 2. 채팅방 참여자수가 0명이라면 채팅방 삭제.
		
		// 3. 메시지 담은후 전송
		message.setType(ChatMessage.MessageType.EXIT);
		message.setMessage(message.getUserName()+"님이 퇴장하셨습니다.");
		
		return message;
	}
	
	// 관리자 공지 메시지용 매핑 url
	@MessageMapping("/notice/send")
	public void sendNotice(@Payload String notice) {
		// 공지내용을 DB에 저장
		// 기타 업무로직 생략
		messagingTemplate.convertAndSend("/topic/notice",notice);
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
