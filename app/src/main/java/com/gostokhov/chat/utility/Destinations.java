package com.gostokhov.chat.utility;

public class Destinations {
	public static class ChatRoom {
		public static String publicMessages(String chatRoomId) {
			return "/topic/" + chatRoomId + ".public.messages";
		}
	}
}
