package com.codebutler.data;

public class Keyword {

    // private variables
    private int _id;
    private String _keyword;
    private String _codeLanguage;
    private String _lessons;
    private String _relevantCode;

    // constructors
    public Keyword(int id, String keyword, String codeLanguage, String lessons, String relevantCode) {
        this._id = id;
        this._keyword = keyword;
        this._codeLanguage = codeLanguage;
        this._lessons = lessons;
        this._relevantCode = relevantCode;
    }
    public Keyword(String keyword, String codeLanguage, String lessons, String relevantCode) {
        this._keyword = keyword;
        this._codeLanguage = codeLanguage;
        this._lessons = lessons;
        this._relevantCode = relevantCode;
    }

    //Get methods
    public int getID() {
        return this._id;
    }
    public String getKeyword() {
        return this._keyword;
    }
    public String getCodeLanguage() {
        return this._codeLanguage;
    }
    public String getLessons() {
        return this._lessons;
    }
    public String getRelevantCode() {
        return this._relevantCode;
    }

    //Set methods
    public void setID(int id) {
        this._id = id;
    }
    public void setKeyword(String keyword) {
        this._keyword = keyword;
    }
    public void setCodeLanguage(String codeLanguage) {
        this._codeLanguage = codeLanguage;
    }
    public void setLessons(String lessons) {
        this._lessons = lessons;
    }
    public void setRelevantCode(String relevantCode) {
        this._relevantCode = relevantCode;
    }

    //Special methods
    public String toString() {
        return "" + this.getKeyword() + " in lessons: " + this.getLessons() + "\n";
    }
}
