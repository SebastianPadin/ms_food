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
import reactor.util.function.Tuple2;


@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCostService {

    private final WebClient webClient;
    private final FoodCostsRepository foodCostsRepository;

    @Value("${api.food-service-url}")
    private String foodServiceUrl;

    @Value("${api.hens-service-url}")
    private String hensServiceUrl;

    public Mono<Void> updateFoodCost(Long idFoodCosts, FoodCostRequestDto request) {
        return foodCostsRepository.findById(idFoodCosts)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró el registro con ID: " + idFoodCosts)))
                .flatMap(existing -> getFoodAndHensData(request)
                        .flatMap(tuple -> {
                            FoodDto food = tuple.getT1();
                            HensDto hens = tuple.getT2();

                            System.out.println("🐔 Gallina encontrada: ID " + hens.getId());
                            System.out.println("🌽 Alimento encontrado: " + ", Cantidad: " + food.getAmount());

                            validateFoodAmount(food.getAmount());

                            // Se utiliza la cantidad de gallinas obtenida para recalcular el total de Kg
                            BigDecimal totalKg = calculateTotalKg(request.getGramsPerChicken(), request.getQuantity());
                            System.out.println("📦 Total de Kg calculado: " + totalKg);

                            BigDecimal costPerKg = calculateCostPerKg(request.getUnitPrice(), BigDecimal.valueOf(food.getAmount()));
                            System.out.println("💰 Costo por Kg calculado: " + costPerKg);

                            BigDecimal totalCost = calculateTotalCost(totalKg, costPerKg);
                            System.out.println("💸 Costo total calculado: " + totalCost);

                            updateEditableFields(existing, request, totalKg, totalCost, hens);

                            return foodCostsRepository.save(existing)
                                    .doOnSuccess(updated -> System.out.println("✅ Registro actualizado con éxito: ID " + updated.getIdFoodCosts()))
                                    .then();
                        }));
    }

    private Mono<Tuple2<FoodDto, HensDto>> getFoodAndHensData(FoodCostRequestDto request) {
        // Actualizamos el filtro para el alimento utilizando el ID, igual a lo realizado en InsertCostService.
        Mono<FoodDto> foodMono = webClient.get()
                .uri(foodServiceUrl)
                .retrieve()
                .bodyToFlux(FoodDto.class)
                .filter(f -> f.getId_food().equals(request.getFoodId()))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "❌ No se encontró alimento con ID: " + request.getFoodId())));

        // Actualizamos el filtro para las gallinas utilizando el ID (y no el shedId) y validamos la fecha, igual que en InsertCostService.
        Mono<HensDto> hensMono = webClient.get()
                .uri(hensServiceUrl)
                .retrieve()
                .bodyToFlux(HensDto.class)
                .filter(h -> h.getId().equals(request.getHensId()))
                .filter(h -> !h.getArrivalDate().isAfter(LocalDate.now()))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "❌ No se encontró gallina con ID: " + request.getHensId())));

        return Mono.zip(foodMono, hensMono);
    }

    private void validateFoodAmount(Integer amount) {
        if (amount == null || BigDecimal.valueOf(amount).compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("❌ Cantidad inválida de alimento");
        }
    }

    private BigDecimal calculateTotalKg(BigDecimal gramsPerChicken, int quantity) {
        return gramsPerChicken.multiply(BigDecimal.valueOf(quantity))
                .multiply(BigDecimal.valueOf(7)) // 7 días de consumo
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP); // Conversión a Kg
    }

    private BigDecimal calculateCostPerKg(BigDecimal unitPrice, BigDecimal amount) {
        return unitPrice.divide(amount, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCost(BigDecimal totalKg, BigDecimal costPerKg) {
        return totalKg.multiply(costPerKg).setScale(2, RoundingMode.HALF_UP);
    }

    private void updateEditableFields(FoodCost existing, FoodCostRequestDto request, BigDecimal totalKg,
                                      BigDecimal totalCost, HensDto hens) {
        existing.setWeekNumber(request.getWeekNumber());
        existing.setFoodType(request.getFoodType());
        existing.setGramsPerChicken(request.getGramsPerChicken());
        existing.setTotalKg(totalKg);
        existing.setTotalCost(totalCost);
        existing.setShedId(hens.getShedId()); 
        existing.setShedName(request.getShedName());
    }
}
