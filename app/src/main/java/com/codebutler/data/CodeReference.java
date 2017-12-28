package com.codebutler.data;

public class CodeReference {

    // private variables
    private int _id;
    private String _codeReference;
    private String _lessonTitle;
    private String _link;

    // constructors
    public CodeReference(int id, String codeReference, String link) {
        this._id = id;
        this._codeReference = codeReference;
        this._link = link;
    }

    //Get methods
    public int getID() {
        return this._id;
    }
    public String getCodeReference() {
        return this._codeReference;
    }
    public String getLink() {
        return this._link;
    }

    //Set methods
    public void setID(int id) {
        this._id = id;
    }
    public void setCodeReference(String lessonNumber) {
        this._codeReference = lessonNumber;
    }
    public void setLink(String link) {
        this._link = link;
    }

    //Special methods
    public String toString() {
        return "" + this.getCodeReference() + ": " + this.getLink() + "\n";
    }
}
