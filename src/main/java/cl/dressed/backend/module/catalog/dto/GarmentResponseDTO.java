package cl.dressed.backend.module.catalog.dto;

import cl.dressed.backend.module.catalog.entity.Garment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class GarmentResponseDTO {

    private Integer id;
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
    private LocalDateTime createdAt;

    public static GarmentResponseDTO from(Garment g) {
        GarmentResponseDTO dto = new GarmentResponseDTO();
        dto.id = g.getId();
        dto.storeId = g.getStoreId();
        dto.name = g.getName();
        dto.price = g.getPrice();
        dto.imageUrl = g.getImageUrl();
        dto.productLink = g.getProductLink();
        dto.category = g.getCategory();
        dto.sizes = g.getSize() != null ? Arrays.asList(g.getSize().split(",")) : List.of();
        dto.mainColor = g.getMainColor();
        dto.fit = g.getFit();
        dto.style = g.getStyle();
        dto.inStock = g.getInStock();
        dto.createdAt = g.getCreatedAt();
        return dto;
    }

    public Integer getId() { return id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
}