package cl.dressed.backend.module.catalog.service;

import cl.dressed.backend.module.catalog.dto.CatalogDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    public CatalogDto.ProductResponse findProduct(Long id) {
        return new CatalogDto.ProductResponse(id, "Demo Product", BigDecimal.valueOf(9990));
    }
}
