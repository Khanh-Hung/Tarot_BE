package tarot.infrastructure.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import tarot.application.common.exceptions.AccountServiceException;
import tarot.application.common.exceptions.AccountServiceUnavailableException;
import tarot.application.dto.AccountUserDto;
import tarot.application.interfaces.AccountServiceClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountServiceClientImpl implements AccountServiceClient {

    private final RestClient restClient;

    @Autowired
    public AccountServiceClientImpl(
            @Value("${account-service.base-url}") String baseUrl,
            @Value("${account-service.timeout-ms:5000}") long timeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public AccountServiceClientImpl(RestClient restClient) {
        this.restClient = Objects.requireNonNull(restClient, "restClient cannot be null");
    }

    @Override
    public Optional<AccountUserDto> getUser(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            AccountUserDto dto = restClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, resp) -> {
                        // 404 is handled below by returning empty
                    })
                    .body(AccountUserDto.class);

            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.error("Client error when calling Account Service for user {}: status {}", userId, e.getStatusCode());
            throw new AccountServiceException("Account Service returned client error for user '" + userId + "': " + e.getMessage(), e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            log.error("Server error from Account Service for user {}: status {}", userId, e.getStatusCode());
            throw new AccountServiceUnavailableException("Account Service encountered an internal server error for user '" + userId + "'", e);
        } catch (ResourceAccessException e) {
            log.error("Network or timeout failure communicating with Account Service for user {}: {}", userId, e.getMessage());
            throw new AccountServiceUnavailableException("Failed to communicate with Account Service for user '" + userId + "'. Service may be unreachable or offline.", e);
        } catch (Exception e) {
            log.error("Unexpected error querying Account Service for user {}: {}", userId, e.getMessage());
            throw new AccountServiceException("Unexpected error communicating with Account Service: " + e.getMessage(), e, 500);
        }
    }

    @Override
    public Map<UUID, AccountUserDto> getUsers(Collection<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UUID> distinctIds = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> requestPayload = Map.of("userIds", distinctIds);

            List<AccountUserDto> users = restClient.post()
                    .uri("/api/v1/users/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AccountUserDto>>() {});

            if (users == null) {
                return Collections.emptyMap();
            }

            return users.stream()
                    .filter(u -> u.userId() != null)
                    .collect(Collectors.toMap(AccountUserDto::userId, u -> u, (existing, replacement) -> existing));
        } catch (HttpClientErrorException e) {
            log.error("Client error during batch lookup in Account Service: status {}", e.getStatusCode());
            throw new AccountServiceException("Account Service returned client error during batch lookup: " + e.getMessage(), e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            log.error("Server error from Account Service during batch lookup: status {}", e.getStatusCode());
            throw new AccountServiceUnavailableException("Account Service encountered an internal server error during batch lookup", e);
        } catch (ResourceAccessException e) {
            log.error("Network or timeout failure during batch lookup in Account Service: {}", e.getMessage());
            throw new AccountServiceUnavailableException("Failed to communicate with Account Service during batch lookup. Service may be unreachable or offline.", e);
        } catch (Exception e) {
            log.error("Unexpected error during batch lookup in Account Service: {}", e.getMessage());
            throw new AccountServiceException("Unexpected error during batch lookup in Account Service: " + e.getMessage(), e, 500);
        }
    }
}
