package com.example.secureguard;

public class Bulletin {
    private String data;
    private String time;
    private String item;

    public Bulletin(String data, String time, String item) {
        this.data = data;
        this.time = time;
        this.item = item;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Bulletin{" +
                "data='" + data + '\'' +
                ", time='" + time + '\'' +
                ", item='" + item + '\'' +
                '}';
    }
}
