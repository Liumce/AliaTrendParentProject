package cn.how2j.trend.pojo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Index implements Serializable {
    private static final long serialVersionUID = -5587270167443098440L;
    String code;
    String name;
}
