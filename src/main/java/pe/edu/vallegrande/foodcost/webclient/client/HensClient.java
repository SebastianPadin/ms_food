package pe.edu.vallegrande.foodcost.webclient.client;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.foodcost.dto.reception.HensDto;
import reactor.core.publisher.Mono;

@Component
public class HensClient {

    private final WebClient webClient;
    private final String hensServiceUrl;

    public HensClient(@Qualifier("hensWebClient") WebClient webClient,
                      @Value("${api.hens-service-url}") String hensServiceUrl) {
        this.webClient = webClient;
        this.hensServiceUrl = hensServiceUrl;
    }

    public Mono<HensDto> findHensById(Long hensId) {
        return webClient.get()
                .uri(hensServiceUrl)
                .retrieve()
                .bodyToFlux(HensDto.class)
                .filter(h -> h.getId().equals(hensId) && !h.getArrivalDate().isAfter(LocalDate.now()))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró gallina con ID: " + hensId)));
    }
}
