package social.com.userservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import social.com.userservice.follow.service.FollowService;
import social.com.userservice.user.model.User;
import social.com.userservice.web.dto.FollowRequest;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{userId}/followings")
    public  ResponseEntity<?> getUserFollowings(@PathVariable(name="userId") UUID userId) {
        List<UUID> userFollowings = followService.getUserFollowings(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userFollowings);
    }
}
