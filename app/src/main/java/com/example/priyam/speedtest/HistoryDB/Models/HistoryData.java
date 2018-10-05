package com.example.priyam.speedtest.HistoryDB.Models;

public class HistoryData {
    int id;
    String download;
    String upload;
    String date;
    String ConType;

    public HistoryData(int id, String download, String upload, String date, String conType) {
        this.id = id;
        this.download = download;
        this.upload = upload;
        this.date = date;
        ConType = conType;
    }

    public HistoryData(String download, String upload, String date, String conType){
        this.download = download;
        this.upload = upload;
        this.date = date;
        ConType = conType;
    }

    public int getId() {
        return id;
    }

    public String getDownload() {
        return download;
    }

    public String getUpload() {
        return upload;
    }

    public String getDate() {
        return date;
    }

    public String getConType() {
        return ConType;
    }

    /*public HistoryData(int id, String score, String date) {
        this.id = id;
        this.score = score;
        this.date = date;

    }

    public HistoryData(String score, String date) {
        this.score = score;
        this.date = date;

    }*/

}
