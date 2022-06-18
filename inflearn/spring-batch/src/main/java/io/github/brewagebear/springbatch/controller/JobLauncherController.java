package io.github.brewagebear.springbatch.controller;

import io.github.brewagebear.springbatch.domain.Item;
import io.github.brewagebear.springbatch.domain.Member;
import java.util.Date;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {

//    private final Job job;
//
//    private final JobLauncher jobLauncher;
//
//    private final BasicBatchConfigurer basicBatchConfigurer;
//
//    public JobLauncherController(Job job, JobLauncher jobLauncher,
//        BasicBatchConfigurer basicBatchConfigurer) {
//        this.job = job;
//        this.jobLauncher = jobLauncher;
//        this.basicBatchConfigurer = basicBatchConfigurer;
//    }
//
//    @PostMapping("/batch")
//    public String launch(@RequestBody Member member)
//        throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//
//        JobParameters jobParameters = new JobParametersBuilder()
//            .addString("id", member.getId())
//            .addDate("date", new Date())
//            .toJobParameters();
//
//        jobLauncher.run(job, jobParameters);
//
//        return "Batch Completed";
//    }
//
//    @PostMapping("/batch-async")
//    public String launch(@RequestBody Item item)
//        throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//
//        JobParameters jobParameters = new JobParametersBuilder()
//            .addString("id", item.getId())
//            .addDate("date", new Date())
//            .toJobParameters();
//
//        SimpleJobLauncher jobLauncher = (SimpleJobLauncher)basicBatchConfigurer.getJobLauncher();
//        // SimpleJobLauncher jobLauncher = (SimpleJobLauncher)simpleJobLauncher; -> Proxy로 세팅되어있기 때문에 직접접근 X
//        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        jobLauncher.run(job, jobParameters);
//
//        return "Batch Completed";
//    }
}
