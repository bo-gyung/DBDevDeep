package com.dbdevdeep.approve.domain;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OpenAPI_ServiceResponse")
public class HolidayResponseDto {
    private Header header;
    private Body body;

    @XmlElement(name = "header")
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    @XmlElement(name = "body")
    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public static class Header {
        private String resultCode;
        private String resultMsg;

        @XmlElement(name = "resultCode")
        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        @XmlElement(name = "resultMsg")
        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;

        @XmlElement(name = "items")
        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        @XmlElement(name = "numOfRows")
        public int getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(int numOfRows) {
            this.numOfRows = numOfRows;
        }

        @XmlElement(name = "pageNo")
        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        @XmlElement(name = "totalCount")
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    public static class Items {
        private List<HolidayDto> item;

        @XmlElement(name = "item")
        public List<HolidayDto> getItem() {
            return item;
        }

        public void setItem(List<HolidayDto> item) {
            this.item = item;
        }
    }
}
