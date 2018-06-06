package org.ucr.cs172.jerryzhu.lucenesearcher;

public class Tweet {

    public int id;
    public String user;
    public String text;
    public String hashtag;

    public Tweet(){}

    public Tweet(int id, String user, String text, String hashtag) {
        this.id = id;
        this.user = user;
        this.text = text;
        this.hashtag = hashtag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public String toString() {
        return String.format("Tweet[id=%d, user=%s, text=%s, hashtag=%s]", id, user, text, hashtag);
    }
}
