package com;

import jdk.nashorn.internal.scripts.JO;
import org.apache.log4j.Logger;
import org.quartz.*;


import java.util.Date;

/**
 * Created by liangwenchang on 2019/5/15.
 */
public class myJob implements Job {

    private static Logger _log = Logger.getLogger(myJob.class);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String jobSay = jobDataMap.getString("JobSay");

        _log.info(new Date() + " 调用Job处理:收到的数据>" + jobSay);
    }
}
