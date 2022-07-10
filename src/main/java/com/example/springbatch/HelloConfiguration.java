package com.example.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
public class HelloConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JdbcCursorItemReaderBuilder<PushAlarmVO> jdbcCursorItemReaderBuilder;
    private final JdbcBatchItemWriterBuilder<PushAlarmVO> jdbcBatchItemWriterBuilder;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job pushAlarmJob() {
        return jobBuilderFactory.get("pushAlarmJob")
                .incrementer(new RunIdIncrementer())
//                .listener(jobListener)
                .flow(pushAlarmToDeviceChunkStep())
                .end()
                .build();
    }

    @Bean
    public Step pushAlarmToDeviceChunkStep() {
        return stepBuilderFactory.get("pushAlarmToDeviceChunkStep")
//                .listener(stepListener)
                .<PushAlarmVO, PushAlarmVO> chunk(CHUNK_SIZE)
                .reader(jdbcCursorItemReader())
                .processor(sendPushAlarmChunkProcessor())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<PushAlarmVO> jdbcCursorItemReader() {
        return jdbcCursorItemReaderBuilder
                .name("jdbcCursorItemReader")
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(PushAlarmVO.class))
                .sql("SELECT * from PUSH_RECEPTION")
                .build();
    }

    @Bean
    public PushAlarmProcessor sendPushAlarmChunkProcessor() {
        return new PushAlarmProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<PushAlarmVO> jdbcBatchItemWriter() {
        return jdbcBatchItemWriterBuilder
                .dataSource(dataSource)
                .sql("insert into PUSH_RESULT_TABLE(userId, deviceId, successFail) values (:userId, :deviceId, :successFail)")
                .beanMapped()
                .build();
    }

}
