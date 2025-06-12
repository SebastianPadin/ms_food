package pe.edu.vallegrande.foodcost.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.vallegrande.foodcost.dto.reception.FoodDto;
import pe.edu.vallegrande.foodcost.dto.reception.HensDto;
import pe.edu.vallegrande.foodcost.dto.transfer.FoodCostRequestDto;
import pe.edu.vallegrande.foodcost.exception.client.FoodClientException;
import pe.edu.vallegrande.foodcost.exception.client.HensClientException;
import pe.edu.vallegrande.foodcost.exception.service.FoodCostNotFoundException;
import pe.edu.vallegrande.foodcost.exception.service.FoodNotFoundException;
import pe.edu.vallegrande.foodcost.exception.service.InvalidFoodAmountException;
import pe.edu.vallegrande.foodcost.model.FoodCost;
import pe.edu.vallegrande.foodcost.repository.FoodCostsRepository;
import pe.edu.vallegrande.foodcost.webclient.client.FoodClient;
import pe.edu.vallegrande.foodcost.webclient.client.HensClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCostService {

    private final FoodClient foodClient;
    private final HensClient hensClient;
    private final FoodCostsRepository foodCostsRepository;

    public Mono<Void> updateFoodCost(Long idFoodCosts, FoodCostRequestDto request) {
        return foodCostsRepository.findById(idFoodCosts)
                .switchIfEmpty(Mono.error(new FoodCostNotFoundException("No se encontró el registro con ID: " + idFoodCosts)))
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
        return Mono.zip(
                foodClient.findFoodById(request.getFoodId())
                        .switchIfEmpty(Mono.error(new FoodNotFoundException("No se encontró el alimento con ID: " + request.getFoodId())))
                        .onErrorMap(ex -> new FoodClientException("Error en la consulta del alimento: " + ex.getMessage())),
                hensClient.findHensById(request.getHensId())
                        .onErrorMap(ex -> new HensClientException("Error en la consulta de las gallinas: " + ex.getMessage()))
        );
    }

    private void validateFoodAmount(Integer amount) {
        if (amount == null || BigDecimal.valueOf(amount).compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidFoodAmountException("❌ Cantidad inválida de alimento");
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
        existing.setShedName(request.getShedName());
        existing.setShedId(hens.getShedId());
        existing.setHensId(request.getHensId());
    }
}
