package com.dbdevdeep.approve.domain;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
public class HolidayDto {
    private String dateKind;
    private String dateName;
    private String isHoliday;
    private String locdate;
    private int seq;

    @XmlElement(name = "dateKind")
    public String getDateKind() {
        return dateKind;
    }

    public void setDateKind(String dateKind) {
        this.dateKind = dateKind;
    }

    @XmlElement(name = "dateName")
    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    @XmlElement(name = "isHoliday")
    public String getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(String isHoliday) {
        this.isHoliday = isHoliday;
    }

    @XmlElement(name = "locdate")
    public String getLocdate() {
        return locdate;
    }

    public void setLocdate(String locdate) {
        this.locdate = locdate;
    }

    @XmlElement(name = "seq")
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
