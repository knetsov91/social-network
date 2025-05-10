package social.com.userservice.web;

import social.com.userservice.auth.client.dto.TokenIssueRequest;
import social.com.userservice.user.model.User;

public class Mapper {

    public static TokenIssueRequest mapUserToTokenIssueRequest(User user) {
        TokenIssueRequest tokenIssueRequest = new TokenIssueRequest();
        tokenIssueRequest.setUserId(user.getId());
        tokenIssueRequest.setUsername(user.getUsername());
        return tokenIssueRequest;
    }
}
