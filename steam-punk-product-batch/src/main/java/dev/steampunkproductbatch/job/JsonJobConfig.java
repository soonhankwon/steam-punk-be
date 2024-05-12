package dev.steampunkproductbatch.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.steampunkproductbatch.domain.Category;
import dev.steampunkproductbatch.domain.Product;
import dev.steampunkproductbatch.reader.JsonProductItemReader;
import dev.steampunkproductbatch.respository.CategoryRepository;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JsonJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final CategoryRepository categoryRepository;
    private static final int CHUNK_SIZE = 100;
    private final Set<String> categorySet = new HashSet<>();
    private final Map<String, Long> internalCacheStore = new ConcurrentHashMap<>();

    @Bean
    public Job jsonConvertAndAddDatabaseJob(PlatformTransactionManager transactionManager, JobRepository jobRepository)
            throws IOException {
        return new JobBuilder("jsonConvertAndAddDatabaseJob", jobRepository)
                .start(getDistinctCategoriesStep(transactionManager, jobRepository))
                .next(addCategoriesStep(transactionManager, jobRepository))
                .next(jsonConvertAndAddDatabaseJobStep(transactionManager, jobRepository))
                .build();
    }

    @Bean
    @JobScope
    public Step getDistinctCategoriesStep(PlatformTransactionManager transactionManager,
                                          JobRepository jobRepository) {
        return new StepBuilder("getDistinctCategoriesStep", jobRepository)
                .tasklet(readJsonCollectDistinctCategoriesTasklet(), transactionManager)
                .build();
    }

    public Tasklet readJsonCollectDistinctCategoriesTasklet() {
        return ((contribution, chunkContext) -> {
            readJsonCollectDistinctCategories();
            return RepeatStatus.FINISHED;
        });
    }

    private void readJsonCollectDistinctCategories() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(
                    new File("/Users/soon/Downloads/archive/games.json"));

            Iterator<String> appIds = rootNode.fieldNames();

            while (appIds.hasNext()) {
                String id = appIds.next();
                JsonNode gameNode = rootNode.get(id);
                JsonNode genresJsonNode = gameNode.get("genres");

                if (genresJsonNode != null && !genresJsonNode.isEmpty()) {
                    Iterator<JsonNode> iterator = genresJsonNode.iterator();
                    iterator.forEachRemaining(g -> {
                        String categoryName = g.asText();
                        categorySet.add(categoryName);
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed Batch");
        }
    }

    @Bean
    @JobScope
    public Step addCategoriesStep(PlatformTransactionManager transactionManager,
                                  JobRepository jobRepository) {
        return new StepBuilder("addCategoriesStep", jobRepository)
                .tasklet(addCategoriesTasklet(), transactionManager)
                .build();
    }

    public Tasklet addCategoriesTasklet() {
        return ((contribution, chunkContext) -> {
            addCategories();
            return RepeatStatus.FINISHED;
        });
    }

    private void addCategories() {
        categorySet.iterator().forEachRemaining(c -> {
            Category category = new Category(c);
            category = categoryRepository.save(category);
            internalCacheStore.put(category.getName(), category.getId());
        });
    }

    @Bean
    public Step jsonConvertAndAddDatabaseJobStep(PlatformTransactionManager transactionManager,
                                                 JobRepository jobRepository)
            throws IOException {
        return new StepBuilder("jsonConvertAndAddDatabaseJobStep", jobRepository)
                .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
                .reader(jsonProductItemReader())
                .writer(jpaProductItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Product> jsonProductItemReader() throws IOException {
        return new JsonProductItemReader(
                "/Users/soon/Downloads/archive/games.json", internalCacheStore);
    }

    @Bean
    public JpaItemWriter<Product> jpaProductItemWriter() {
        JpaItemWriter<Product> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
