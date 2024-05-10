package dev.steampunkproductbatch.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.steampunkproductbatch.domain.Category;
import dev.steampunkproductbatch.domain.Product;
import dev.steampunkproductbatch.domain.ProductCategory;
import dev.steampunkproductbatch.respository.CategoryRepository;
import dev.steampunkproductbatch.respository.ProductCategoryRepository;
import dev.steampunkproductbatch.respository.ProductRepository;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfig {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Set<String> set = new HashSet<>();
    private final Map<String, Long> internalCache = new HashMap<>();

    @Bean
    public Job simpleJob(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        return new JobBuilder("simpleJob", jobRepository)
                .start(collectDistinctCategoriesStep(transactionManager, jobRepository))
                .next(addCategoriesStep(transactionManager, jobRepository))
                .next(addProductsStep(transactionManager, jobRepository))
                .build();
    }

    @Bean
    @JobScope
    public Step collectDistinctCategoriesStep(PlatformTransactionManager transactionManager,
                                              JobRepository jobRepository) {
        return new StepBuilder("collectDistinctCategoriesStep", jobRepository)
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
                        set.add(categoryName);
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
        set.iterator().forEachRemaining(c -> {
            Category category = new Category(c);
            category = categoryRepository.save(category);
            internalCache.put(category.getName(), category.getId());
        });
    }

    @Bean
    @JobScope
    public Step addProductsStep(PlatformTransactionManager transactionManager,
                                JobRepository jobRepository) {
        return new StepBuilder("addProductsStep", jobRepository)
                .tasklet(addProductsTasklet(), transactionManager)
                .build();
    }

    public Tasklet addProductsTasklet() {
        return ((contribution, chunkContext) -> {
            readJsonAndAddProducts();
            return RepeatStatus.FINISHED;
        });
    }

    private void readJsonAndAddProducts() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(
                    new File("/Users/soon/Downloads/archive/games.json"));

            Iterator<String> appIds = rootNode.fieldNames();

            while (appIds.hasNext()) {
                String id = appIds.next();
                JsonNode gameNode = rootNode.get(id);

                String name = gameNode.get("name").asText();
                double price = gameNode.get("price").asDouble();
                String shortDescription = gameNode.get("short_description").asText();
                String webSite = gameNode.get("website").asText();
                JsonNode developersJsonNode = gameNode.get("developers");
                String developer;
                if (developersJsonNode == null || developersJsonNode.isEmpty()) {
                    developer = "Anonymous";
                } else {
                    developer = developersJsonNode.get(0).asText();
                }
                JsonNode genresJsonNode = gameNode.get("genres");
                Product product = new Product(name, price, shortDescription, webSite, developer);
                productRepository.save(new Product(name, price, shortDescription, webSite, developer));

                if (genresJsonNode != null && !genresJsonNode.isEmpty()) {
                    Iterator<JsonNode> iterator = genresJsonNode.iterator();
                    iterator.forEachRemaining(g -> {
                        String categoryName = g.asText();
                        Long categoryId = internalCache.get(categoryName);
                        productCategoryRepository.save(new ProductCategory(product, categoryId));
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed Batch");
        }
    }
}
