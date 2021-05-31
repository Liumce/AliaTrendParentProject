package cn.how2j.trend.service;

import cn.how2j.trend.pojo.Index;
import cn.how2j.trend.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//从指数类获取指数信息,并用list存储
@Service
@CacheConfig(cacheNames = "indexes")   //缓存的名称是 indexes.共享公共缓存相关设置
public class IndexService {
    private List<Index> indexes;
    @Autowired
    RestTemplate restTemplate;


    //这种方式属于硬编码，稍后可修改
    //当该方法回退时
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        indexes = fetch_indexes_from_third_part();
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }

    @CacheEvict(allEntries = true)
    public void remove() {

    }

    @Cacheable(key = "'all_codes'") // 表示保存到 redis 用的 key是all_codes
    public List<Index> store() {
        System.out.println(this);
        return indexes;
    }

    @Cacheable(key="'all_codes'",unless = "#result.isEmpty()")
    public List<Index> get() {
        return CollUtil.toList();
    }

    public List<Index> fetch_indexes_from_third_part() {
        List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);
        return map2Index(temp);
    }

    private List<Index> map2Index(List<Map> temp) {
        List<Index> indexes = new ArrayList<>();
        for (Map map : temp) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index = new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }

        return indexes;
    }

    //HystrixCommand的fallback方法
    public List<Index> third_part_not_connected() {
        System.out.println("third_part_not_connected()");
        Index index = new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }
}
