package cn.how2j.trend.job;

import cn.how2j.trend.pojo.Index;
import cn.how2j.trend.service.IndexDataService;
import cn.how2j.trend.service.IndexService;
import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;


/*开启定时任务--定时刷新
 * 第三方的数据一般说来每天都会更新。
 * 按照前面的做法，需要访问 fresh 地址才能实现刷新的效果
 *
 * 在配置类中进行定时任务的定时配置
 */

//任务类，同时刷新指数代码和指数数据
public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    private IndexService indexService;

    @Autowired
    private IndexDataService indexDataService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时启动：" + DateUtil.now());
        List<Index> indexes = indexService.fresh();
        for (Index index : indexes) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束：" + DateUtil.now());
    }
}
