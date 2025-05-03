package posts.social.com.postservice.web;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import posts.social.com.postservice.post.model.Post;
import posts.social.com.postservice.web.dto.AuthorPostsResponse;
import posts.social.com.postservice.web.dto.PageablePostsResponse;
import posts.social.com.postservice.web.dto.PostResponse;
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

    public static PageablePostsResponse mapPageablePostsToPageablePostsResponse(Page<Post> posts) {
        PageablePostsResponse pageablePostsResponse = new PageablePostsResponse();
        List<PostResponse> postResponses = new ArrayList<>();

        posts.getContent().forEach(post -> {
            PostResponse build = PostResponse.builder()
                    .id(post.getId())
                    .authorId(post.getAuthorId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .likes(post.getLikes())
                    .updatedAt(post.getUpdatedAt())
                    .createdAt(post.getCreatedAt())
                    .build();

            postResponses.add(build);
        });

        pageablePostsResponse.setPosts(postResponses);
        pageablePostsResponse.setTotalElements(posts.getTotalElements());
        pageablePostsResponse.setTotalPages(posts.getTotalPages());
        pageablePostsResponse.setFirst(posts.isFirst());
        pageablePostsResponse.setLast(posts.isLast());

        return pageablePostsResponse;
    }
}
