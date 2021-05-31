package cn.how2j.trend.client;

import cn.how2j.trend.pojo.IndexData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**Feign以接口形式工作
 * 当Feign需要整合Hystrix时，配置为feign开启Hystrix支持：
 * feign.hystrix.enable=true
 *
 * 并给FeignClient指定回退类
 *
 */
@FeignClient(value = "INDEX-DATA-SERVICE", fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    @GetMapping("/data/{code}")
    public List<IndexData> getIndexData(@PathVariable("code") String code);
}
