package com.khaoula.transactionsservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionsServiceApplicationTests {

    @Test
    void main_ShouldRunApplication() {
        TransactionsServiceApplication.main(new String[]{"--server.port=0"});
    }

    @Test
    void contextLoads() {
    }

}
