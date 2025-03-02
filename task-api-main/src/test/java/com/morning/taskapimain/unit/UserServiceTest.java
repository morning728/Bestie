/*
package com.morning.taskapimain.unit;

import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks

    private UserService userService;

    @Mock

    private UserRepository userRepository;

    @Mock
    private RestTemplate template;

    @Mock
    private JwtService jwtService;

    @Mock
    private DatabaseClient client;

    @Value("${application.security.db}")
    private String securityDatabasePath;

    @Value("${application.security.db-username}")
    private String securityDatabaseUsername;

    @Value("${application.security.db-password}")
    private String securityDatabasePassword;

    @Test
    void testGetUserContactsByUsername() throws Exception {
        String testUsername = "admin";
        //String query = "select username, telegram_id, email from public.auth_user WHERE username = ?";

        // Mocking JDBC connection and statements
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Setup ResultSet to return expected data
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("admin");
        when(mockResultSet.getString("email")).thenReturn("admin@example.com");
        when(mockResultSet.getString("telegram_id")).thenReturn("123456");

        // Injecting mock connection
        userService = spy(userService);
        doReturn(mockConnection).when(userService).getConnection();

        Mono<Contacts> result = userService.getUserContactsByUsername(testUsername);

        // Asserting result using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(contacts -> contacts.getUsername().equals("admin") &&
                        contacts.getEmail().equals("admin@example.com") &&
                        contacts.getTelegramId().equals("123456"))
                .verifyComplete();

        // Verifying close operations
        verify(mockResultSet).close();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void testGetUserRoleInProject() {
        Long projectId = 1L;
        String token = "mockedToken";
        String username = "admin";

        // Мокируем JWT-сервис для возврата username из токена
        when(jwtService.extractUsername(token)).thenReturn(username);

        // Мокируем DatabaseClient и цепочку вызовов
        DatabaseClient.GenericExecuteSpec mockExecuteSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        FetchSpec<Map<String, Object>> mockFetchSpec = mock(FetchSpec.class);

        // Настройка цепочки вызовов
        when(client.sql(anyString())).thenReturn(mockExecuteSpec);
        when(mockExecuteSpec.fetch()).thenReturn(mockFetchSpec);
        when(mockFetchSpec.first()).thenReturn(Mono.just(Map.of("role", "ADMIN")));

        // Тестируемый метод
        Mono<String> result = UserService.getUserRoleInProject(projectId, token, jwtService, client);
///
        // Используем StepVerifier для проверки результата
        StepVerifier.create(result)
                .expectNext("ADMIN")
                .verifyComplete();

        // Проверяем, что username был извлечен из токена
        verify(jwtService, times(1)).extractUsername(token);
    }


}

*/
