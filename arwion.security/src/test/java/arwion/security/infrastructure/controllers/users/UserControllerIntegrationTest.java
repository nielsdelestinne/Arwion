package arwion.security.infrastructure.controllers.users;

import arwion.IntegrationTestConfiguration;
import arwion.security.infrastructure.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserControllerIntegrationTest(MockMvc mockMvc, JwtTokenProvider jwtTokenProvider) {
        this.mockMvc = mockMvc;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Test
    void adminAuthorizedOnly_givenAuthWithCorrectAuthority_thenAcceptRequest() throws Exception {
        mockMvc
                .perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearerJwtForAdmin()))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .string("Only authenticated ADMIN (authorized) users can see this"));
    }

    @Test
    void adminAuthorizedOnly_givenAuthWithNotCorrectAuthority_thenDoNotAcceptRequest() throws Exception {
        mockMvc
                .perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearerJwtForNormalUser()))
                .andDo(print())
                .andExpect(status()
                        .isForbidden());
    }

    @Test
    void adminAuthorizedOnly_givenAuthWithInvalidJwt_thenDoNotAcceptRequest() throws Exception {
        mockMvc
                .perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, "NotAGoodJwt"))
                .andDo(print())
                .andExpect(status()
                        .isForbidden());
    }


    private String bearerJwtForAdmin() {
        return bearerJwt("FakeAdminUser", "ADMIN");
    }

    private String bearerJwtForNormalUser() {
        return bearerJwt("FakeNormalUser", "USER");
    }

    private String bearerJwt(String username, String... roles) {
        return format("Bearer %s", jwtTokenProvider.createToken(username, List.of(roles)));
    }
}