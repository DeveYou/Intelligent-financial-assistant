package com.khaoula.transactionsservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author radouane
 **/
@FeignClient(name = "recipient-service")
public interface RecipientClient {

    @GetMapping("/api/recipients/{id}")
    ApiResponse<RecipientResponse> getRecipientById(@PathVariable("id") Long id,
                                                    @RequestHeader("Authorization") String authorization);

    @GetMapping("/api/recipients/iban/{iban}")
    ApiResponse<RecipientResponse> getRecipientByIban(@PathVariable("iban") String iban, @RequestHeader("Authorization") String authorization);

    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    @Data
    class RecipientResponse {
        private Long id;
        private String bank;
        private String iban;
        private String fullName;
    }
}
