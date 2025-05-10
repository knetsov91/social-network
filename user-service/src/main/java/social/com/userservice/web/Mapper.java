package social.com.userservice.web;

import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.user.model.User;
import social.com.userservice.web.dto.GetAllUsersResponse;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static TokenIssueRequest mapUserToTokenIssueRequest(User user) {
        TokenIssueRequest tokenIssueRequest = new TokenIssueRequest();
        tokenIssueRequest.setUserId(user.getId());
        tokenIssueRequest.setUsername(user.getUsername());
        return tokenIssueRequest;
    }

    public static List<GetAllUsersResponse> mapUsersToGetAllUsersResponse(List<User> users) {
        List<GetAllUsersResponse> usersResponse = new ArrayList<>();
        for (User user : users) {
            GetAllUsersResponse userResponse = new GetAllUsersResponse(user.getUsername());
            usersResponse.add(userResponse);
        }

        return usersResponse;
    }
}
