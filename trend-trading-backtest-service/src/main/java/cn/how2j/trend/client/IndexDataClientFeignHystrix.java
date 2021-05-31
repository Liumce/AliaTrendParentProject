package cn.how2j.trend.client;

import cn.how2j.trend.pojo.IndexData;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Component;

import java.util.List;

//当被调用的微服务index-data-service不可用或者不可访问时，发生回退
@Component
public class IndexDataClientFeignHystrix implements IndexDataClient {
    @Override
    public List<IndexData> getIndexData(String code) {
        IndexData indexData = new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}
