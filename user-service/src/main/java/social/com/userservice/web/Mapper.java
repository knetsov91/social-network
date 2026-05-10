package social.com.userservice.web;

import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.user.model.User;
import social.com.userservice.web.dto.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static TokenIssueRequest mapUserToTokenIssueRequest(User user) {
        TokenIssueRequest tokenIssueRequest = new TokenIssueRequest();
        tokenIssueRequest.setUserId(user.getId());
        tokenIssueRequest.setUsername(user.getUsername());
        return tokenIssueRequest;
    }

    public static List<UserResponse> mapUsersToUserResponse(List<User> users) {
        List<UserResponse> usersResponse = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = new UserResponse(user.getId(), user.getUsername());
            usersResponse.add(userResponse);
        }

        return usersResponse;
    }
}
