package arwion.security.infrastructure.controllers.users;

import arwion.IntegrationTestConfiguration;
import arwion.security.infrastructure.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static arwion.IntegrationTestConfiguration.*;
import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Nested
    @DisplayName("Sample Test")
    class AdminAuthorizedOnly {

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
            assertRequestForbiddenForToken(HttpMethod.GET, "/api/users", bearerJwtForNormalUser());
        }

        @Test
        void adminAuthorizedOnly_givenAuthWithInvalidJwt_thenDoNotAcceptRequest() throws Exception {
            assertRequestForbiddenForToken(HttpMethod.GET, "/api/users", "NotAGoodJwt");
        }

    }

    @Nested
    @DisplayName("Register User")
    class Register {

        @Test
        void register_givenAuthWithCorrectAuthority_thenAcceptRequest() throws Exception {
            mockMvc
                    .perform(post("/api/users/register")
                            .header(HttpHeaders.AUTHORIZATION, bearerJwtForAdmin())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(new ObjectMapper().writeValueAsString(
                                    new UserRegistrationRequest()
                                            .setUsername("jacob@mail.be")
                                            .setPassword("123456")
                                            .setRoles(List.of("USER")))))
                    .andDo(print())
                    .andExpect(status()
                            .isCreated());
        }

        @Test
        void register_givenAuthWithNotCorrectAuthority_thenDoNotAcceptRequest() throws Exception {
            assertRequestForbiddenForToken(HttpMethod.POST, "/api/users/register", bearerJwtForNormalUser());
        }

        @Test
        void register_givenAuthWithInvalidJwt_thenDoNotAcceptRequest() throws Exception {
            assertRequestForbiddenForToken(HttpMethod.POST, "/api/users/register", "NotAGoodJwt");
        }

    }

    @Nested
    @DisplayName("Sign-in User")
    class Signin {

        @Test
        void signin_givenAuthWithCorrectAuthorityAndExistingUser_thenReturnToken() throws Exception {
            var result = mockMvc
                    .perform(put("/api/users/signin")
                            .header(HttpHeaders.AUTHORIZATION, bearerJwtForAdmin())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(new ObjectMapper().writeValueAsString(
                                    new UserAuthenticationRequest()
                                            .setUsername(FAKE_ADMIN_USER_USERNAME)
                                            .setPassword(FAKE_ADMIN_USER_PASSWORD))))
                    .andDo(print())
                    .andExpect(status()
                            .isOk())
                    .andReturn()
                        .getResponse()
                            .getContentAsString();

            Assertions.assertThat(result)
                    .contains(String.format("\"username\":\"%s\",\"token\":\"ey", FAKE_ADMIN_USER_USERNAME));
        }

        @Test
        void signin_givenNoAuthAndExistingUser_thenReturnToken() throws Exception {
            var result = mockMvc
                    .perform(put("/api/users/signin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(new ObjectMapper().writeValueAsString(
                                    new UserAuthenticationRequest()
                                            .setUsername(FAKE_ADMIN_USER_USERNAME)
                                            .setPassword(FAKE_ADMIN_USER_PASSWORD))))
                    .andDo(print())
                    .andExpect(status()
                            .isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertThat(result)
                    .contains(String.format("\"username\":\"%s\",\"token\":\"ey", FAKE_ADMIN_USER_USERNAME));
        }

        @Test
        void signin_givenAuthWithCorrectAuthorityButNonExistingUser_thenDoNotAcceptRequest() throws Exception {
            mockMvc
                    .perform(put("/api/users/signin")
                            .header(HttpHeaders.AUTHORIZATION, bearerJwtForAdmin())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(new ObjectMapper().writeValueAsString(
                                    new UserAuthenticationRequest()
                                            .setUsername("WrongUsername")
                                            .setPassword("WrongPassword"))))
                    .andDo(print())
                    .andExpect(status()
                            .isForbidden());
        }

        @Test
        void signin_givenNoAuthAndNonExistingUser_thenDoNotAcceptRequest() throws Exception {
            mockMvc
                    .perform(put("/api/users/signin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(new ObjectMapper().writeValueAsString(
                                    new UserAuthenticationRequest()
                                            .setUsername(FAKE_ADMIN_USER_USERNAME)
                                            .setPassword("Wrong Password"))))
                    .andDo(print())
                    .andExpect(status()
                            .isForbidden());
        }
    }

    private void assertRequestForbiddenForToken(HttpMethod httpMethod, String url, String token) throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.request(httpMethod, url)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status()
                        .isForbidden());
    }

    private String bearerJwtForAdmin() {
        return bearerJwt(FAKE_ADMIN_USER_USERNAME, "ADMIN");
    }

    private String bearerJwtForNormalUser() {
        return bearerJwt(FAKE_NORMAL_USER_USERNAME, "USER");
    }

    private String bearerJwt(String username, String... roles) {
        return format("Bearer %s", jwtTokenProvider.createToken(username, List.of(roles)));
    }
}