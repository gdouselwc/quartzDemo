package com;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.List;


import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by liangwenchang on 2019/5/15.
 */
public class quartzDemo {

    private static Logger _log = Logger.getLogger(myJob.class);

    public void run() throws Exception {

        _log.info("Initializing....");

        //job
        JobDetail jobDetail = newJob(myJob.class)
                .withIdentity("myjob", "jobGroup")
                .usingJobData("JobSay","你好吗")
                .build();

        //trigger
        Date dt = evenMinuteDate(new Date());
        Trigger simpleTrigger = newTrigger()
                .withIdentity("trigger","triggerGroup")
                .startAt(dt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(10) //10秒调用一次job
                .withRepeatCount(9) //总共调用10次
                .withMisfireHandlingInstructionFireNow()
                )
                .build();

        //Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.scheduleJob(jobDetail, simpleTrigger);

        _log.info(new Date() + " 调度开始...");
        scheduler.start();

        //scheduler.shutdown();


    }

    //从数据库读取job运行
    public void ResumeJob() throws Exception{
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        // ①获取调度器中所有的触发器组
        List<String> triggerGroups = scheduler.getTriggerGroupNames();
        // ②重新恢复在tgroup1组中，名为trigger1触发器的运行
        for (int i = 0; i < triggerGroups.size(); i++) {//这里使用了两次遍历，针对每一组触发器里的每一个触发器名，和每一个触发组名进行逐次匹配
            List<String> triggers = scheduler.getTriggerGroupNames();
            for (int j = 0; j < triggers.size(); j++) {
                Trigger tg = scheduler.getTrigger(new TriggerKey(triggers
                        .get(j), triggerGroups.get(i)));
                // ②-1:根据名称判断
                if (tg instanceof SimpleTrigger
                        && tg.getDescription().equals("jgroup1.DEFAULT")) {//由于我们之前测试没有设置触发器所在组，所以默认为DEFAULT
                    // ②-1:恢复运行
                    scheduler.resumeJob(new JobKey(triggers.get(j),
                            triggerGroups.get(i)));
                }
            }
        }
        scheduler.start();
    }


    public static void main(String[] args) {

        try{

            quartzDemo quartzDemo = new quartzDemo();
            //quartzDemo.run();
            quartzDemo.ResumeJob();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
