package cn.how2j.trend.service;

import cn.how2j.trend.pojo.IndexData;
import cn.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/*服务类，直接从reids获取数据，
 * 相当于只读方式，不存在数据冲突
 *  如果没有数据，则会返回 “无效指数代码”。
 */

/*@Cacheable
 * **标注在方法上，表示该方法是支持缓存
 * ****Spring会在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时，
 * ****可以直接从缓存中获取结果，而不需要再次执行该方法
 *
 * **标注在类上，表示该类所有方法都支持缓存
 *
 * 标注的方法在执行前会检查Cache中是否存在相同key的缓存元素，
 * 如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回
 *
 */

/*@CacheEvict
 * 用来清除缓存
 * 在注解里指定的缓存删除掉
 */

/*@CachePut
 * 声明一个方法支持缓存功能
 * 使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，
 * 而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。
 */
@Service
@CacheConfig(cacheNames = "index_datas")
public class IndexDataService {
    @Cacheable(key = "'indexData-code-'+ #p0")
    public List<IndexData> get(String code) {
        return CollUtil.toList();
    }
}
