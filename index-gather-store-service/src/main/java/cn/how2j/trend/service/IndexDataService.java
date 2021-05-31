package cn.how2j.trend.service;

import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.util.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*这里存在一个问题：当删除所有数据后，用户再访问redis就是【】，
 * 对用户来说体验感不佳，可以采用redis集群方式，一个主redis负责刷新，刷新好再把数据同步到其他redis，实现高可用
 *
 */
//用于获取一支股票的信息，与IndexService对应，指数数据存放的key是 indexData-code-000300 这种
@Service
@CacheConfig(cacheNames="index_datas")
public class IndexDataService {
    private Map<String, List<IndexData>> indexDatas=new HashMap<>();
    @Autowired
    RestTemplate restTemplate;


    /*fetchIndexData()存在问题
     * 	如果第三方能用，就把数据放在 redis里了。
		如果第三方不能用，就把断路数据放在 redis 里了。
		无论第三方是否有变化， 因为 redis里已有数据，以后所有访问都会从 redis 里获取数据
		数据无法进行刷新
		进行如下操作来解决数据无法刷新问题
     */
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<IndexData> fresh(String code) {
        List<IndexData> indexeDatas =fetch_indexes_from_third_part(code);

        indexDatas.put(code, indexeDatas);

        System.out.println("code:"+code);
        System.out.println("indexeDatas:"+indexDatas.get(code).size());
        //从容器中获取IndexService类的Bean
        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }

    //清空Redis数据
    //指示一个方法(或类上的所有方法)触发缓存退出操作。
    @CacheEvict(key="'indexData-code-'+ #p0")
    public void remove(String code){

    }

    //往 redis 里保存数据
    @CachePut(key="'indexData-code-'+ #p0")
    public List<IndexData> store(String code){
        return indexDatas.get(code);
    }

    //从 redis 中获取数据
    //指示调用一个方法(或类中的所有方法)的结果可以被缓存
    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }

    public List<IndexData> fetch_indexes_from_third_part(String code){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/"+code+".json",List.class);
        return map2IndexData(temp);
    }

    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();

            indexData.setDate(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }

        return indexDatas;
    }

    public List<IndexData> third_part_not_connected(String code){
        System.out.println("third_part_not_connected()");
        IndexData index= new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }
}
