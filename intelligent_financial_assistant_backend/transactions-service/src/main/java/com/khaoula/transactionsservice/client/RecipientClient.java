// Ajout d'un client OpenFeign pour interroger le recipient-service
package com.khaoula.transactionsservice.client;

import com.khaoula.transactionsservice.dto.RecipientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "recipient-service")
public interface RecipientClient {

    @GetMapping("/api/recipients/iban/{iban}")
    RecipientDTO getByIban(@PathVariable("iban") String iban);
}

