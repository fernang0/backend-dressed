package cl.dressed.backend.module.catalog.service;

import cl.dressed.backend.module.catalog.dto.GarmentRequestDTO;
import cl.dressed.backend.module.catalog.dto.GarmentResponseDTO;
import cl.dressed.backend.module.catalog.entity.Garment;
import cl.dressed.backend.module.catalog.repository.GarmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GarmentService - Pruebas unitarias")
class GarmentServiceTest {

    @Mock
    private GarmentRepository garmentRepository;

    @InjectMocks
    private GarmentService garmentService;

    private GarmentRequestDTO requestDTO;
    private Garment garmentMock;

    @BeforeEach
    void setUp() {
        requestDTO = new GarmentRequestDTO();
        setField(requestDTO, "storeId", 1);
        setField(requestDTO, "name", "Polera Básica");
        setField(requestDTO, "productLink", "https://tienda.cl/polera-1");
        setField(requestDTO, "price", new BigDecimal("9990"));
        setField(requestDTO, "category", "poleras");
        setField(requestDTO, "inStock", true);

        garmentMock = new Garment();
        garmentMock.setId(1);
        garmentMock.setStoreId(1);
        garmentMock.setName("Polera Básica");
        garmentMock.setProductLink("https://tienda.cl/polera-1");
        garmentMock.setPrice(new BigDecimal("9990"));
        garmentMock.setCategory("poleras");
        garmentMock.setInStock(true);
        garmentMock.setDedupHash("somehash");
    }

    // Helper para setear campos privados sin setters
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("importGarment: debe guardar prenda nueva correctamente")
    void importGarment_nuevaPrenda_retornaDTO() {
        when(garmentRepository.existsByDedupHash(anyString())).thenReturn(false);
        when(garmentRepository.save(any(Garment.class))).thenReturn(garmentMock);

        GarmentResponseDTO resultado = garmentService.importGarment(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getName()).isEqualTo("Polera Básica");
        assertThat(resultado.getStoreId()).isEqualTo(1);
        verify(garmentRepository).save(any(Garment.class));
    }

    @Test
    @DisplayName("importGarment: debe lanzar excepción cuando la prenda ya existe")
    void importGarment_prendaDuplicada_lanzaExcepcion() {
        when(garmentRepository.existsByDedupHash(anyString())).thenReturn(true);

        assertThatThrownBy(() -> garmentService.importGarment(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya existe");

        verify(garmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("importGarment: debe lanzar excepción cuando faltan campos obligatorios")
    void importGarment_camposObligatoriosFaltantes_lanzaExcepcion() {
        GarmentRequestDTO incompleto = new GarmentRequestDTO();
        // sin name, productLink ni storeId

        assertThatThrownBy(() -> garmentService.importGarment(incompleto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Campos obligatorios");
    }

    @Test
    @DisplayName("getProducts: debe retornar todos cuando no hay filtros")
    void getProducts_sinFiltros_retornaTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garment> page = new PageImpl<>(List.of(garmentMock));
        when(garmentRepository.findAll(pageable)).thenReturn(page);

        Page<GarmentResponseDTO> resultado = garmentService.getProducts(null, null, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(garmentRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getProducts: debe filtrar por categoría cuando se proporciona")
    void getProducts_conCategoria_filtraPorCategoria() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garment> page = new PageImpl<>(List.of(garmentMock));
        when(garmentRepository.findByCategory("poleras", pageable)).thenReturn(page);

        Page<GarmentResponseDTO> resultado = garmentService.getProducts("poleras", null, null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getCategory()).isEqualTo("poleras");
        verify(garmentRepository).findByCategory("poleras", pageable);
    }

    @Test
    @DisplayName("getProducts: debe filtrar por talla cuando se proporciona")
    void getProducts_conTalla_filtraPorTalla() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garment> page = new PageImpl<>(List.of(garmentMock));
        when(garmentRepository.findBySizeContaining("M", pageable)).thenReturn(page);

        Page<GarmentResponseDTO> resultado = garmentService.getProducts(null, "M", null, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(garmentRepository).findBySizeContaining("M", pageable);
    }

    @Test
    @DisplayName("getProducts: debe filtrar por stock cuando se proporciona")
    void getProducts_conInStock_filtraPorStock() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garment> page = new PageImpl<>(List.of(garmentMock));
        when(garmentRepository.findByInStock(true, pageable)).thenReturn(page);

        Page<GarmentResponseDTO> resultado = garmentService.getProducts(null, null, true, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        verify(garmentRepository).findByInStock(true, pageable);
    }
}