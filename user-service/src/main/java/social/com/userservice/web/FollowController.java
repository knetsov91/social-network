package social.com.userservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import social.com.userservice.follow.service.FollowService;
import social.com.userservice.user.model.User;
import social.com.userservice.web.dto.FollowRequest;

@RestController
@RequestMapping("/api/v1/users/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<?> follow(@AuthenticationPrincipal UserDetails authUser, @RequestBody FollowRequest followRequest) {
        User user = (User) authUser;
        followService.follow(user.getId(), followRequest.getFolloweId());
        return ResponseEntity.ok().build();
    }
}
