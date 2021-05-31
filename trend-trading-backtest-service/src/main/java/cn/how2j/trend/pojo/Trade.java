package cn.how2j.trend.pojo;


//交易类：用于记录交易的购买日期，出售日期，购买盘点，出售盘点，收益
public class Trade {

    private String buyDate; //购买日期
    private String sellDate;//出售日期
    private float buyClosePoint;//购买盘点
    private float sellClosePoint;//出售盘点
    private float rate;//收益

    public String getBuyDate() {
        return buyDate;
    }
    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }
    public String getSellDate() {
        return sellDate;
    }
    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }
    public float getBuyClosePoint() {
        return buyClosePoint;
    }
    public void setBuyClosePoint(float buyClosePoint) {
        this.buyClosePoint = buyClosePoint;
    }
    public float getSellClosePoint() {
        return sellClosePoint;
    }
    public void setSellClosePoint(float sellClosePoint) {
        this.sellClosePoint = sellClosePoint;
    }
    public float getRate() {
        return rate;
    }
    public void setRate(float rate) {
        this.rate = rate;
    }

}