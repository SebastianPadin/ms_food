package pe.edu.vallegrande.foodcost.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import pe.edu.vallegrande.foodcost.dto.reception.FoodDto;
import pe.edu.vallegrande.foodcost.dto.reception.HensDto;
import pe.edu.vallegrande.foodcost.dto.transfer.FoodCostRequestDto;
import pe.edu.vallegrande.foodcost.model.FoodCost;
import pe.edu.vallegrande.foodcost.repository.FoodCostsRepository;
import pe.edu.vallegrande.foodcost.service.InsertCostService;
import pe.edu.vallegrande.foodcost.webclient.client.FoodClient;
import pe.edu.vallegrande.foodcost.webclient.client.HensClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class InsertCostServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private FoodCostsRepository foodCostsRepository;

    @Mock
    private FoodClient foodClient;

    @Mock
    private HensClient hensClient;

    @InjectMocks
    private InsertCostService insertCostService;

    // Mocks para la cadena del WebClient para la llamada de alimento
    @Mock
    private WebClient.RequestHeadersUriSpec foodRequestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec foodRequestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec foodResponseSpec;

    // Mocks para la cadena del WebClient para la llamada de gallinas
    @Mock
    private WebClient.RequestHeadersUriSpec hensRequestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec hensRequestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec hensResponseSpec;

    @BeforeEach
    void setUp() {
        // Para el repositorio, stub para que al guardar retorne el objeto guardado
        lenient().when(foodCostsRepository.save(any(FoodCost.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    /**
     * Escenario exitoso cuando no existe registro previo para el galpón.
     * Se espera que el flujo guarde dos registros (registro inicial y el siguiente).
     */
    @Test
    void testAddFoodCost_noExistingFoodCost() {
        // Datos falsos para FoodDto
        FoodDto foodDto = new FoodDto();
        foodDto.setIdFood(1L);
        foodDto.setAmount(50);

        // Datos falsos para HensDto
        HensDto hensDto = new HensDto();
        hensDto.setId(1L);
        hensDto.setArrivalDate(LocalDate.now().minusDays(5)); // fecha de llegada anterior a hoy
        hensDto.setShedId(1L);

        // Datos falsos para FoodCostRequestDto
        FoodCostRequestDto request = new FoodCostRequestDto();
        request.setFoodId(1L);
        request.setHensId(1L);
        request.setGramsPerChicken(BigDecimal.valueOf(150));  // gramos por gallina
        request.setQuantity(10);
        request.setUnitPrice(BigDecimal.valueOf(200));
        request.setFoodType("Inicio de gallina ponedora");
        request.setWeekNumber("Semana 1");
        request.setShedName("Galpon A");

        // Simulación de la llamada a FoodClient
        when(foodClient.findFoodById(1L)).thenReturn(Mono.just(foodDto));

        // Simulación de la llamada a HensClient
        when(hensClient.findHensById(1L)).thenReturn(Mono.just(hensDto));

        // No existe un registro previo para el galpón, por lo que el repositorio retorna Mono.empty()
        when(foodCostsRepository.findTopByShedIdOrderByStartDateDesc(1L))
                .thenReturn(Mono.empty());

        // Ejecutamos el método a probar
        Mono<Void> result = insertCostService.addFoodCost(request);

        // Verificamos que el flujo se complete sin error y que se guarden 2 registros
        StepVerifier.create(result)
                .verifyComplete();
        verify(foodCostsRepository, times(1)).save(any());
    }

    /**
     * Escenario exitoso cuando ya existe un registro previo para el galpón.
     * En este caso se guarda únicamente el nuevo registro (branch flatMap).
     */
    @Test
    void testAddFoodCost_existingFoodCost() {
        // Datos falsos para FoodDto
        FoodDto foodDto = new FoodDto();
        foodDto.setIdFood(1L);
        foodDto.setAmount(25);

        // Datos falsos para HensDto
        HensDto hensDto = new HensDto();
        hensDto.setId(4L);
        hensDto.setArrivalDate(LocalDate.now().minusDays(10));
        hensDto.setShedId(5L);

        // Datos falsos para FoodCostRequestDto
        FoodCostRequestDto request = new FoodCostRequestDto();
        request.setFoodId(1L);
        request.setHensId(4L);
        request.setGramsPerChicken(BigDecimal.valueOf(150));
        request.setQuantity(10);
        request.setUnitPrice(BigDecimal.valueOf(200));
        request.setFoodType("Postura");
        request.setWeekNumber("Semana 2");
        request.setShedName("Galpon B");

        // Creamos un registro previo falso para el galpón
        FoodCost existingFoodCost = new FoodCost();
        existingFoodCost.setStartDate(LocalDate.now().minusDays(8));
        existingFoodCost.setEndDate(LocalDate.now().minusDays(1));

        // Simulación de la llamada a FoodClient
        when(foodClient.findFoodById(1L)).thenReturn(Mono.just(foodDto));

        // Simulación de la llamada a HensClient
        when(hensClient.findHensById(4L)).thenReturn(Mono.just(hensDto));

        // El repositorio retorna un registro previo para el galpón
        when(foodCostsRepository.findTopByShedIdOrderByStartDateDesc(5L))
                .thenReturn(Mono.just(existingFoodCost));

        Mono<Void> result = insertCostService.addFoodCost(request);

        StepVerifier.create(result)
                .verifyComplete();
        // En este escenario se realiza una sola operación de guardado (solo el flatMap)
        verify(foodCostsRepository, times(1)).save(any());
    }

    /**
     * Escenario en el que la cantidad del alimento es inválida (nula o cero).
     * Se espera que se emita un error controlado.
     */
    @Test
    void testAddFoodCost_invalidFoodAmount() {
        // Creamos FoodDto con cantidad inválida (cero)
        FoodDto foodDto = new FoodDto();
        foodDto.setIdFood(1L);
        foodDto.setAmount(0);

        // Datos válidos para HensDto
        HensDto hensDto = new HensDto();
        hensDto.setId(1L);
        hensDto.setArrivalDate(LocalDate.now().minusDays(5));
        hensDto.setShedId(1L);

        // Configuramos el request
        FoodCostRequestDto request = new FoodCostRequestDto();
        request.setFoodId(1L);
        request.setHensId(1L);
        request.setGramsPerChicken(BigDecimal.valueOf(150));
        request.setQuantity(10);
        request.setUnitPrice(BigDecimal.valueOf(200));
        request.setFoodType("Pre-postura");
        request.setWeekNumber("Semana 2");
        request.setShedName("Galpon B");

        // Simulación de la llamada a FoodClient con cantidad inválida
        when(foodClient.findFoodById(1L)).thenReturn(Mono.just(foodDto));

        // Simulación de la llamada a HensClient con datos válidos
        when(hensClient.findHensById(1L)).thenReturn(Mono.just(hensDto));

        // Ejecutamos el método a probar
        Mono<Void> result = insertCostService.addFoodCost(request);

        // Validamos que se lance una excepción por cantidad inválida de alimento
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Cantidad inválida de alimento"))
                .verify();
    }

    /**
     * Escenario en el que no se encuentra la gallina (por ejemplo, por fecha de llegada inválida o
     * por no existir).
     */
    @Test
    void testAddFoodCost_hensNotFound() {
        // Datos falsos para FoodDto
        FoodDto foodDto = new FoodDto();
        foodDto.setIdFood(1L);
        foodDto.setAmount(100);

        // Configuramos el request
        FoodCostRequestDto request = new FoodCostRequestDto();
        request.setFoodId(1L);
        request.setHensId(2L);
        request.setGramsPerChicken(BigDecimal.valueOf(150));
        request.setQuantity(10);
        request.setUnitPrice(BigDecimal.valueOf(200));
        request.setFoodType("Inicio");
        request.setWeekNumber("Semana 22");
        request.setShedName("Galpon B");

        // Simulación de la llamada a FoodClient
        when(foodClient.findFoodById(1L)).thenReturn(Mono.just(foodDto));

        // Simulación de la llamada a HensClient con flujo vacío para indicar que no se encontró la gallina
        when(hensClient.findHensById(2L)).thenReturn(Mono.error(new RuntimeException("No se encontró gallina")));

        // Ejecutamos el método a probar
        Mono<Void> result = insertCostService.addFoodCost(request);

        // Validamos que se lance una excepción por gallina no encontrada
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("No se encontró gallina"))
                .verify();
    }
}
