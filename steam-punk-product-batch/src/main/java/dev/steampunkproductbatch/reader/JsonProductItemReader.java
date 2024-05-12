package dev.steampunkproductbatch.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.steampunkproductbatch.domain.Product;
import dev.steampunkproductbatch.domain.ProductCategory;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class JsonProductItemReader implements ItemReader<Product> {

    private final Iterator<JsonNode> productIterator;
    private final Map<String, Long> internalCacheMap;

    public JsonProductItemReader(String filePath, Map<String, Long> cacheMap) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        this.productIterator = rootNode.elements();
        this.internalCacheMap = cacheMap;
    }

    @Override
    public synchronized Product read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (productIterator != null && productIterator.hasNext()) {
            JsonNode node = productIterator.next();
            String name = node.get("name").asText();
            double price = node.get("price").asDouble();
            String shortDescription = node.get("short_description").asText();
            String headerImage = node.get("header_image").asText();
            String webSite = node.get("website").asText();
            JsonNode developersJsonNode = node.get("developers");
            String developer;
            if (developersJsonNode == null || developersJsonNode.isEmpty()) {
                developer = "Anonymous";
            } else {
                developer = developersJsonNode.get(0).asText();
            }
            Product product = new Product(name, price, shortDescription, headerImage, webSite, developer);
            JsonNode genresJsonNode = node.get("genres");
            if (genresJsonNode != null && !genresJsonNode.isEmpty()) {
                Iterator<JsonNode> iterator = genresJsonNode.iterator();
                iterator.forEachRemaining(g -> {
                    String categoryName = g.asText();
                    Long categoryId = internalCacheMap.get(categoryName);
                    product.getProductCategories().add(new ProductCategory(product, categoryId));
                });
            }
            return product;
        }
        return null;
    }
}
