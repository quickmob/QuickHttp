package com.lookballs.app.http.bean;

public class UploadBean {

    /**
     * status : 0
     * msg : Success
     * data : {"sgin":null,"url":"https://mo.baidu.com/boxandroid?from=1023544j&source=1023534z"}
     */

    private int status;
    private String msg;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sgin : null
         * url : https://mo.baidu.com/boxandroid?from=1023544j&source=1023534z
         */

        private Object sgin;
        private String url;

        public Object getSgin() {
            return sgin;
        }

        public void setSgin(Object sgin) {
            this.sgin = sgin;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
