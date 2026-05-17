package cl.dressed.backend.module.catalog.service;

import cl.dressed.backend.module.catalog.dto.GarmentRequestDTO;
import cl.dressed.backend.module.catalog.dto.GarmentResponseDTO;
import cl.dressed.backend.module.catalog.entity.Garment;
import cl.dressed.backend.module.catalog.repository.GarmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GarmentService {

    private final GarmentRepository garmentRepository;

    public GarmentResponseDTO importGarment(GarmentRequestDTO dto) {
        if (dto.getName() == null || dto.getProductLink() == null || dto.getStoreId() == null) {
            throw new IllegalArgumentException("Campos obligatorios faltantes: name, productLink o storeId");
        }

        Garment garment = new Garment();
        garment.setStoreId(dto.getStoreId());
        garment.setName(dto.getName());
        garment.setPrice(dto.getPrice());
        garment.setImageUrl(dto.getImageUrl());
        garment.setProductLink(dto.getProductLink());
        garment.setCategory(dto.getCategory());
        garment.setSize(dto.getSizes() != null ? String.join(",", dto.getSizes()) : null);
        garment.setMainColor(dto.getMainColor());
        garment.setFit(dto.getFit());
        garment.setStyle(dto.getStyle());
        garment.setInStock(dto.getInStock() != null ? dto.getInStock() : true);

        Garment saved = garmentRepository.save(garment);
        return GarmentResponseDTO.from(saved);
    }

    public Page<GarmentResponseDTO> getProducts(String category, String size, Boolean inStock, Pageable pageable) {
        if (category != null) {
            return garmentRepository.findByCategory(category, pageable).map(GarmentResponseDTO::from);
        }
        if (size != null) {
            return garmentRepository.findBySizeContaining(size, pageable).map(GarmentResponseDTO::from);
        }
        if (inStock != null) {
            return garmentRepository.findByInStock(inStock, pageable).map(GarmentResponseDTO::from);
        }
        return garmentRepository.findAll(pageable).map(GarmentResponseDTO::from);
    }
}