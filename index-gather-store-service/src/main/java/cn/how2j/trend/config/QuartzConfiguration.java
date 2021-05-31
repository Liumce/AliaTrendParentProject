package cn.how2j.trend.config;

import cn.how2j.trend.job.IndexDataSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
    private static final int interval = 1;

    //使用JobBuilder创建/定义JobDetail
    @Bean
    public JobDetail weatherDataSyncJobDetail() {
        //根据JobDetail创建一个新任务，并设定任务的key，设置作业在没有触发器指向它时不在存储。
        return JobBuilder.newJob(IndexDataSyncJob.class).withIdentity("indexDataSyncJob")
                .storeDurably().build();
    }

    //使用TriggerBuilder实例化实际触发器
    //触发器是调度作业的“机制”。许多触发器可以指向同一作业，但单个触发器只能指向一个作业。
    @Bean
    public Trigger weatherDataSyncTrigger() {
        //SimpleScheduleBuilder为触发器定义了调度间隔。
        SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(interval).repeatForever();
        //从给定作业中提取JobKey，设置应由producedTrigger触发的作业的标识。
        return TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
                .withIdentity("indexDataSyncTrigger").withSchedule(schedBuilder).build();
    }
}
