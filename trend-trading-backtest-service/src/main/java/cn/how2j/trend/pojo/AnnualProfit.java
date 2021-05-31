package cn.how2j.trend.pojo;


//每年收益实体类，字段为：年份，指数收益，趋势收益
public class AnnualProfit {

    private int year;
    private float indexIncome;
    private float trendIncome;
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public float getIndexIncome() {
        return indexIncome;
    }
    public void setIndexIncome(float indexIncome) {
        this.indexIncome = indexIncome;
    }
    public float getTrendIncome() {
        return trendIncome;
    }
    public void setTrendIncome(float trendIncome) {
        this.trendIncome = trendIncome;
    }
    @Override
    public String toString() {
        return "AnnualProfit [year=" + year + ", indexIncome=" + indexIncome + ", trendIncome=" + trendIncome + "]";
    }

}