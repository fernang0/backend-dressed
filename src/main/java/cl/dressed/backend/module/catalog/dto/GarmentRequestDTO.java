package cl.dressed.backend.module.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public class GarmentRequestDTO {

    private Integer storeId;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private String productLink;
    private String category;
    private List<String> sizes;
    private String mainColor;
    private String fit;
    private String style;
    private Boolean inStock;

    public Integer getStoreId() { return storeId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getProductLink() { return productLink; }
    public String getCategory() { return category; }
    public List<String> getSizes() { return sizes; }
    public String getMainColor() { return mainColor; }
    public String getFit() { return fit; }
    public String getStyle() { return style; }
    public Boolean getInStock() { return inStock; }
}