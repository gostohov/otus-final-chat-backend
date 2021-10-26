package com.gostokhov.chat.enumiration;

public enum ChatRoomType {
    PRIVATE("PRIVATE"),
    GROUP("GROUP");

    private String type;

    ChatRoomType(String type) {
        this.type = type;
    }

    public String getType() { return this.type; }
}
