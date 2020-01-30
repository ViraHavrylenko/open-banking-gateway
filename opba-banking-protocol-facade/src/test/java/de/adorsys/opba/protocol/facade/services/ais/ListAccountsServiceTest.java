package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ListAccountsServiceTest.TestConfig.class)
class ListAccountsServiceTest {

    @Autowired
    private ListAccountsService listAccountsService;

    @Test
    @SneakyThrows
    void testXs2aWired() {
        assertThat(listAccountsService.list(
                ListAccountsRequest.builder().bankID("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46").build()).get()
        ).isInstanceOf(RedirectionResult.class);
    }

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}