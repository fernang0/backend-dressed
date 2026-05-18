package cl.dressed.backend.module.catalog.controller;

import cl.dressed.backend.module.catalog.dto.GarmentRequestDTO;
import cl.dressed.backend.module.catalog.dto.GarmentResponseDTO;
import cl.dressed.backend.module.catalog.security.ScraperApiKeyService;
import cl.dressed.backend.module.catalog.service.GarmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogController - Pruebas unitarias")
class CatalogControllerTest {

    @Mock
    private GarmentService garmentService;

    @Mock
    private ScraperApiKeyService scraperApiKeyService;

    @InjectMocks
    private CatalogController catalogController;

    private GarmentResponseDTO mockDTO() {
        GarmentResponseDTO dto = new GarmentResponseDTO();
        setField(dto, "id", 1);
        setField(dto, "name", "Polera Básica");
        setField(dto, "storeId", 1);
        setField(dto, "productLink", "https://tienda.cl/polera-1");
        setField(dto, "price", new BigDecimal("9990"));
        setField(dto, "inStock", true);
        return dto;
    }

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
    @DisplayName("importGarment: debe retornar 201 cuando la API key es válida")
    void importGarment_apiKeyValida_retorna201() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        GarmentRequestDTO dto = new GarmentRequestDTO();
        doNothing().when(scraperApiKeyService).validateRequest(request);
        when(garmentService.importGarment(dto)).thenReturn(mockDTO());

        ResponseEntity<GarmentResponseDTO> respuesta = catalogController.importGarment(dto, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getName()).isEqualTo("Polera Básica");
        verify(scraperApiKeyService).validateRequest(request);
        verify(garmentService).importGarment(dto);
    }

    @Test
    @DisplayName("importGarment: debe lanzar excepción cuando la API key es inválida")
    void importGarment_apiKeyInvalida_lanzaExcepcion() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        GarmentRequestDTO dto = new GarmentRequestDTO();
        doThrow(new IllegalArgumentException("API key inválida o ausente"))
                .when(scraperApiKeyService).validateRequest(request);

        assertThatThrownBy(() -> catalogController.importGarment(dto, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("API key inválida o ausente");

        verify(garmentService, never()).importGarment(any());
    }

    @Test
    @DisplayName("getProducts: debe retornar 200 con página de productos sin filtros")
    void getProducts_sinFiltros_retorna200() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<GarmentResponseDTO> page = new PageImpl<>(List.of(mockDTO()));
        when(garmentService.getProducts(null, null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<GarmentResponseDTO>> respuesta =
                catalogController.getProducts(null, null, null, pageable);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getProducts: debe pasar el filtro de categoría al servicio")
    void getProducts_conCategoria_pasaFiltroAlServicio() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<GarmentResponseDTO> page = new PageImpl<>(List.of(mockDTO()));
        when(garmentService.getProducts("poleras", null, null, pageable)).thenReturn(page);

        catalogController.getProducts("poleras", null, null, pageable);

        verify(garmentService).getProducts("poleras", null, null, pageable);
    }
}