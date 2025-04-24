package posts.social.com.postservice.web;

import lombok.experimental.UtilityClass;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.AuthorPostsResponse;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Mapper {

    public static List<AuthorPostsResponse> mapListPostsToListAuthorPostsResponse(List<Post> posts) {
        List<AuthorPostsResponse> authorPostsResponses = new ArrayList<>();

        posts.forEach(post -> {
            AuthorPostsResponse mapped = AuthorPostsResponse.builder()
                    .authorId(post.getAuthorId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .likes(post.getLikes())
                    .build();
            authorPostsResponses.add(mapped);
        });

        return authorPostsResponses;
    }
}
