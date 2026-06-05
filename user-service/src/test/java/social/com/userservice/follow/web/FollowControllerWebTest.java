package social.com.userservice.follow.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import social.com.userservice.auth.service.AuthService;
import social.com.userservice.follow.service.FollowService;
import social.com.userservice.user.repository.UserRepository;
import social.com.userservice.web.FollowController;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
class FollowControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FollowService followService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void whenGetFollowings_thenReturnsFolloweeIds() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID followeeId1 = UUID.randomUUID();
        UUID followeeId2 = UUID.randomUUID();

        when(followService.getUserFollowings(userId)).thenReturn(List.of(followeeId1, followeeId2));

        mockMvc.perform(get("/api/v1/users/{userId}/followings", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(followeeId1.toString()))
                .andExpect(jsonPath("$[1]").value(followeeId2.toString()));
    }
}
