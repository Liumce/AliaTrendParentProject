package cn.how2j.trend.pojo;

//指数中每一支股票的日期和点数，每一个code对应的一支股票
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IndexData {
    String date;
    float closePoint;
}
