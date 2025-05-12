package pe.edu.vallegrande.FoodCost.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.FoodCost.dto.reception.FoodDto;
import pe.edu.vallegrande.FoodCost.dto.reception.HensDto;
import pe.edu.vallegrande.FoodCost.dto.transfer.FoodCostRequestDto;
import pe.edu.vallegrande.FoodCost.model.FoodCost;
import pe.edu.vallegrande.FoodCost.repository.FoodCostsRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
public class InsertCostService {

    private final WebClient webClient;
    private final FoodCostsRepository foodCostsRepository;

    @Value("${api.food-service-url}")
    private String foodServiceUrl;

    @Value("${api.hens-service-url}")
    private String hensServiceUrl;

    public Mono<Void> addFoodCost(FoodCostRequestDto request) {
        System.out.println("Request recibido: " + request);

        return Mono.zip(getFood(request), getHensById(request))
                .flatMap(tuple -> processFoodCost(tuple.getT1(), tuple.getT2(), request));
    }

    private Mono<FoodDto> getFood(FoodCostRequestDto request) {
        return webClient.get()
                .uri(foodServiceUrl)
                .retrieve()
                .bodyToFlux(FoodDto.class)
                .filter(f -> f.getId_food().equals(request.getFoodId()))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "No se encontró alimento con ID: " + request.getFoodId())));
    }

    // Método actualizado que recibe FoodCostRequestDto
    private Mono<HensDto> getHensById(FoodCostRequestDto request) {
        return webClient.get()
                .uri(hensServiceUrl)
                .retrieve()
                .bodyToFlux(HensDto.class)
                .filter(h -> h.getId().equals(request.getHensId()) &&
                        !h.getArrivalDate().isAfter(LocalDate.now()))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "No se encontró gallina con ID: " + request.getHensId())));
    }

    private Mono<Void> processFoodCost(FoodDto food, HensDto hens, FoodCostRequestDto request) {
        System.out.println("Alimento seleccionado: " + food);
        System.out.println("Gallinas seleccionadas: " + hens);

        if (food.getAmount() == null || BigDecimal.valueOf(food.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
            return Mono.error(new RuntimeException(
                    "Cantidad inválida de alimento con ID: " + request.getFoodId()));
        }

        BigDecimal totalKg = calculateTotalKg(request.getGramsPerChicken(), request.getQuantity());
        System.out.println("Total de Kg calculado: " + totalKg);

        BigDecimal costPerKg = calculateCostPerKg(request.getUnitPrice(), BigDecimal.valueOf(food.getAmount()));
        System.out.println("Costo por Kg calculado: " + costPerKg);

        BigDecimal totalCost = calculateTotalCost(totalKg, costPerKg);
        System.out.println("Costo total calculado: " + totalCost);

        return saveFoodCost(request, totalKg, totalCost, hens);
    }

    private BigDecimal calculateTotalKg(BigDecimal gramsPerChicken, int quantity) {
        return gramsPerChicken.multiply(BigDecimal.valueOf(quantity))
                .multiply(BigDecimal.valueOf(7))
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCostPerKg(BigDecimal unitPrice, BigDecimal amount) {
        return unitPrice.divide(amount, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCost(BigDecimal totalKg, BigDecimal costPerKg) {
        return totalKg.multiply(costPerKg).setScale(2, RoundingMode.HALF_UP);
    }

    private Mono<Void> saveFoodCost(FoodCostRequestDto request, BigDecimal totalKg, BigDecimal totalCost,
                                    HensDto hens) {
        return foodCostsRepository.findTopByShedIdOrderByStartDateDesc(hens.getShedId())
                .switchIfEmpty(Mono.defer(() -> {
                    LocalDate startDate = hens.getArrivalDate();
                    LocalDate endDate = calculateEndDate(startDate);
                    FoodCost foodCost = buildFoodCost(request, totalKg, totalCost, startDate, endDate, hens);

                    System.out.println("Registro FoodCost inicial para galpón " + hens.getShedId() + ": " + foodCost);

                    return saveAndLogFoodCost(foodCost, true);
                }))
                .flatMap(lastFoodCost -> {
                    LocalDate startDate = lastFoodCost.getEndDate().plusDays(1);
                    LocalDate endDate = calculateEndDate(startDate);
                    FoodCost foodCost = buildFoodCost(request, totalKg, totalCost, startDate, endDate, hens);

                    System.out.println("Registro FoodCost nuevo para galpón " + hens.getShedId() + ": " + foodCost);

                    return saveAndLogFoodCost(foodCost, false);
                })
                .then();
    }

    private LocalDate calculateEndDate(LocalDate startDate) {
        return startDate.plusDays(6);
    }

    private FoodCost buildFoodCost(FoodCostRequestDto request, BigDecimal totalKg, BigDecimal totalCost,
                                   LocalDate startDate, LocalDate endDate, HensDto hens) {
        FoodCost foodCost = new FoodCost();
        foodCost.setWeekNumber(request.getWeekNumber());
        foodCost.setFoodType(request.getFoodType());
        foodCost.setGramsPerChicken(request.getGramsPerChicken());
        foodCost.setTotalKg(totalKg);
        foodCost.setTotalCost(totalCost);
        foodCost.setStartDate(startDate);
        foodCost.setEndDate(endDate);
        foodCost.setShedId(hens.getShedId());
        foodCost.setShedName(request.getShedName());
        foodCost.setStatus("A");
        return foodCost;
    }

    private Mono<FoodCost> saveAndLogFoodCost(FoodCost foodCost, boolean isInitial) {
        return foodCostsRepository.save(foodCost)
                .doOnSuccess(saved -> {
                    String msg = isInitial ? "Registro inicial guardado" : "Registro guardado exitosamente";
                    System.out.println(msg + ": " + saved);
                })
                .doOnError(error -> {
                    String msg = isInitial ? "Error al guardar registro inicial" : "Error al guardar el registro";
                    System.out.println(msg + ": " + error.getMessage());
                });
    }
    }
}
