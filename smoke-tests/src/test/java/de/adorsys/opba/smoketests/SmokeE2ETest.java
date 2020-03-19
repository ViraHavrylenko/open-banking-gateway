package de.adorsys.opba.smoketests;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_SMOKE_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.SMOKE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * Happy-path smoke test to validate that OpenBanking environment is in sane state.
 */
@EnabledIfEnvironmentVariable(named = ENABLE_SMOKE_TESTS, matches = TRUE_BOOL)
@ExtendWith(SeleniumExtension.class)
@SpringBootTest(classes = {JGivenConfig.class}, webEnvironment = NONE)
@ActiveProfiles(profiles = {SMOKE_TEST})
class SmokeE2ETest extends SpringScenarioTest<SandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private static final LocalDate DATE_FROM = LocalDate.parse("2018-01-01");
    private static final LocalDate DATE_TO = LocalDate.now();
    private static final String BOTH_BOOKING = "BOTH";

    @Value("${test.smoke.opba.server-uri}")
    private String opbaServerUri;

    @Value("${test.smoke.aspsp-profile.server-uri}")
    private String aspspProfileServerUri;

    @MockBean // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private BankProfileJpaRepository profiles;

    @MockBean // Stubbing out as they are not available, but currently breaking hierarchy has no sense as we can replace this with REST in future
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private ConsentRepository consents;

    private List<String> memoizedApproaches;

    @BeforeEach
    void memoizeConsentAuthorizationPreference() {
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(aspspProfileServerUri + "/api/v1/aspsp-profile/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract();

        this.memoizedApproaches = response.body().as(new TypeRef<List<String>>() {});
    }

    @AfterEach
    void restoreConsentAuthorizationPreference() {
        if (null != this.memoizedApproaches) {
            RestAssured
                    .given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(memoizedApproaches)
                    .when()
                        .put(aspspProfileServerUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                    .then()
                        .statusCode(HttpStatus.OK.value());
            this.memoizedApproaches = null;
        }
    }

    @Test
    public void testAccountsListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        redirectListAntonBruecknerAccounts(firefoxDriver);
    }

    @Test
    public void testTransactionListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
            .parse(redirectListAntonBruecknerAccounts(firefoxDriver)).read("$.accounts[0].resourceId");

        given()
            .enabled_redirect_sandbox_mode(aspspProfileServerUri)
            .rest_assured_points_to_opba_server(opbaServerUri);

        when()
            .fintech_calls_list_transactions_for_anton_brueckner(accountResourceId)
            .and()
            .user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
            .and()
            .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button(firefoxDriver);

        then()
            .open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
        embeddedListMaxMustermanAccounts();
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
        String accountResourceId = JsonPath
            .parse(embeddedListMaxMustermanAccounts())
            .read("$.accounts[0].resourceId");

        given()
            .enabled_embedded_sandbox_mode(aspspProfileServerUri)
            .rest_assured_points_to_opba_server(opbaServerUri);

        when()
            .fintech_calls_list_transactions_for_max_musterman(accountResourceId)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email1_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_redirect_to_fintech_ok();
        then()
            .open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
                accountResourceId, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    private String embeddedListMaxMustermanAccounts() {
        given()
            .enabled_embedded_sandbox_mode(aspspProfileServerUri)
            .rest_assured_points_to_opba_server(opbaServerUri);

        when()
            .fintech_calls_list_accounts_for_max_musterman()
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_redirect_to_fintech_ok();

        AccountInformationResult result = then()
            .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }

    private String redirectListAntonBruecknerAccounts(FirefoxDriver firefoxDriver) {
        given()
            .enabled_redirect_sandbox_mode(aspspProfileServerUri)
            .rest_assured_points_to_opba_server(opbaServerUri);

        when()
            .fintech_calls_list_accounts_for_anton_brueckner()
            .and()
            .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
            .and()
            .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
            .and()
            .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
            .and()
            .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button(firefoxDriver);

        AccountInformationResult result = then()
            .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(false);

        return result.getResponseContent();
    }
}