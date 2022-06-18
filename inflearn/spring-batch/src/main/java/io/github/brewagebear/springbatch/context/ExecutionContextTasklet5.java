package io.github.brewagebear.springbatch.context;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ExecutionContextTasklet5 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        System.out.println("name: " + chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get("name"));

        return RepeatStatus.FINISHED;
    }
}
