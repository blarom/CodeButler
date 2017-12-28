package com.codebutler.data;

public class Lesson {

    // private variables
    private int _id;
    private String _lessonNumber;
    private String _lessonTitle;
    private String _link;

    // constructors
    public Lesson(int id, String lessonNumber, String lessonTitle, String link) {
        this._id = id;
        this._lessonNumber = lessonNumber;
        this._lessonTitle = lessonTitle;
        this._link = link;
    }
    public Lesson(String lessonNumber, String lessonTitle) {
        this._lessonNumber = lessonNumber;
        this._lessonTitle = lessonTitle;
    }

    //Get methods
    public int getID() {
        return this._id;
    }
    public String getLessonNumber() {
        return this._lessonNumber;
    }
    public String getLessonTitle() {
        return this._lessonTitle;
    }
    public String getLink() {
        return this._link;
    }

    //Set methods
    public void setID(int id) {
        this._id = id;
    }
    public void setLessonNumber(String lessonNumber) {
        this._lessonNumber = lessonNumber;
    }
    public void setLessonTitle(String lessonTitle) {
        this._lessonTitle = lessonTitle;
    }
    public void setLink(String link) {
        this._link = link;
    }

    //Special methods
    public String toString() {
        return "" + this.getLessonNumber() + " " + this.getLessonTitle() + "\n";
    }
}
