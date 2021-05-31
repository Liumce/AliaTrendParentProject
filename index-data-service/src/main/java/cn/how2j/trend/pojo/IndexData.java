package cn.how2j.trend.pojo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


//指数中每一支股票的日期和点数，每一个code对应的一支股票
@Data
@ToString
public class IndexData implements Serializable {
    private static final long serialVersionUID = -4727771895428921715L;
    String date;
    float closePoint;
}
