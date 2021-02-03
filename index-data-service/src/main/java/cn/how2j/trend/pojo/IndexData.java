package cn.how2j.trend.pojo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class IndexData implements Serializable {
    private static final long serialVersionUID = -4727771895428921715L;
    String date;
    float closePoint;
}
