package com.example.boolan.beans;

public class Chat {
    private String content;
    private String type;

    public Chat() {

    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chat(String content, String type){
        this.content = content;
        this.type = type;
    }

    public String getContent(){
        return content;
    }
    public String getType(){
        return type;
    }
}
