package tarot.infrastructure.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tarot.application.common.exceptions.AccountServiceException;
import tarot.application.common.exceptions.AccountServiceUnavailableException;
import tarot.application.dto.AccountUserDto;
import tarot.domain.enums.Gender;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class AccountServiceClientTest {

    private MockRestServiceServer mockServer;
    private AccountServiceClientImpl client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:5000");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        client = new AccountServiceClientImpl(builder.build());
    }

    @Test
    @DisplayName("getUser - Returns AccountUserDto when Account Service returns 200 OK")
    void getUser_Success() {
        UUID userId = UUID.randomUUID();
        String jsonResponse = String.format("""
            {
                "userId": "%s",
                "userName": "tarot_fan",
                "displayName": "Tarot Master",
                "avatarUrl": "https://avatar.com/tarot.png",
                "email": "tarot@example.com",
                "dateOfBirth": "1995-08-15",
                "gender": "Female",
                "isEmailVerified": true
            }
            """, userId);

        mockServer.expect(requestTo("http://localhost:5000/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Optional<AccountUserDto> result = client.getUser(userId);

        mockServer.verify();
        assertThat(result).isPresent();
        AccountUserDto user = result.get();
        assertThat(user.userId()).isEqualTo(userId);
        assertThat(user.getUserName()).isEqualTo("tarot_fan");
        assertThat(user.getDisplayName()).isEqualTo("Tarot Master");
        assertThat(user.getAvatarUrl()).isEqualTo("https://avatar.com/tarot.png");
        assertThat(user.email()).isEqualTo("tarot@example.com");
        assertThat(user.dateOfBirth()).isEqualTo(LocalDate.of(1995, 8, 15));
        assertThat(user.gender()).isEqualTo(Gender.FEMALE);
        assertThat(user.isEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("getUser - Returns Optional.empty when Account Service returns 404 Not Found")
    void getUser_NotFound() {
        UUID userId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:5000/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<AccountUserDto> result = client.getUser(userId);

        mockServer.verify();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getUser - Throws AccountServiceException when Account Service returns 401 Unauthorized")
    void getUser_Unauthorized() {
        UUID userId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:5000/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.getUser(userId))
                .isInstanceOf(AccountServiceException.class)
                .hasMessageContaining("Account Service returned client error")
                .satisfies(ex -> assertThat(((AccountServiceException) ex).getStatusCode()).isEqualTo(401));

        mockServer.verify();
    }

    @Test
    @DisplayName("getUser - Throws AccountServiceUnavailableException when Account Service returns 500 Internal Server Error")
    void getUser_ServerError() {
        UUID userId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:5000/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getUser(userId))
                .isInstanceOf(AccountServiceUnavailableException.class)
                .hasMessageContaining("Account Service encountered an internal server error");

        mockServer.verify();
    }

    @Test
    @DisplayName("getUser - Returns Optional.empty when userId is null")
    void getUser_NullUserId() {
        Optional<AccountUserDto> result = client.getUser(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getUsers - Returns map of users for batch lookup")
    void getUsers_BatchSuccess() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        String jsonResponse = String.format("""
            [
                {
                    "userId": "%s",
                    "userName": "user1",
                    "displayName": "User One",
                    "avatarUrl": "https://avatar.com/1.png"
                },
                {
                    "userId": "%s",
                    "userName": "user2",
                    "displayName": "User Two",
                    "avatarUrl": "https://avatar.com/2.png"
                }
            ]
            """, u1, u2);

        mockServer.expect(requestTo("http://localhost:5000/api/v1/users/batch"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Map<UUID, AccountUserDto> result = client.getUsers(List.of(u1, u2));

        mockServer.verify();
        assertThat(result).hasSize(2);
        assertThat(result.get(u1).getDisplayName()).isEqualTo("User One");
        assertThat(result.get(u2).getDisplayName()).isEqualTo("User Two");
    }

    @Test
    @DisplayName("getUsers - Returns empty map when list is empty or null")
    void getUsers_EmptyList() {
        assertThat(client.getUsers(null)).isEmpty();
        assertThat(client.getUsers(Collections.emptyList())).isEmpty();
    }
}
