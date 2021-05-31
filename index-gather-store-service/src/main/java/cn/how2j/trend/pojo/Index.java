package cn.how2j.trend.pojo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


//指数中所有股票的代号和名称，全都记录在codes.json
//指数类，用于指数里的名称和代码
@Data
@ToString
public class Index implements Serializable {
    private static final long serialVersionUID = -5587270167443098440L;
    String code;
    String name;
}
