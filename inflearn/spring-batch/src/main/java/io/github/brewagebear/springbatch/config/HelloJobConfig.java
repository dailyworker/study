package io.github.brewagebear.springbatch.config;

import io.github.brewagebear.springbatch.context.ExecutionContextTasklet1;
import io.github.brewagebear.springbatch.context.ExecutionContextTasklet2;
import io.github.brewagebear.springbatch.context.ExecutionContextTasklet3;
import io.github.brewagebear.springbatch.context.ExecutionContextTasklet4;
import io.github.brewagebear.springbatch.listener.JobRepositoryListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ExecutionContextTasklet1 executionContextTasklet1;

    private final ExecutionContextTasklet2 executionContextTasklet2;

    private final ExecutionContextTasklet3 executionContextTasklet3;

    private final ExecutionContextTasklet4 executionContextTasklet4;

    private final JobRepositoryListener listener;

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
            .start(helloStep1())
            .next(helloStep2())
            .next(helloStep3())
            .next(helloStep4())
            .listener(listener)
            .build();
    }

    @Bean
    public Job helloFlow() {
        return jobBuilderFactory.get("helloFlow")
            .start(flow())
            .next(helloStep5())
            .end()
            .build();
    }

    @Bean
    public Step helloStep1() {
        return stepBuilderFactory.get("helloStep1")
            .tasklet(executionContextTasklet1)
            .build();
    }

    @Bean
    public Step helloStep2() {
        return stepBuilderFactory.get("helloStep2")
            .tasklet(executionContextTasklet2)
            .build();
    }

    @Bean
    public Step helloStep3() {
        return stepBuilderFactory.get("helloStep3")
            .tasklet(executionContextTasklet3)
            .build();
    }

    @Bean
    public Step helloStep4() {
        return stepBuilderFactory.get("helloStep4")
            .tasklet(executionContextTasklet4)
            .build();
    }

    @Bean
    public Step helloStep5() {
        return stepBuilderFactory.get("helloStep5")
            .tasklet(executionContextTasklet4)
            .build();
    }

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> flowFlowBuilder = new FlowBuilder<>("Flow");
        flowFlowBuilder.start(helloStep3())
            .next(helloStep4())
            .end();

        return flowFlowBuilder.build();
    }
}
