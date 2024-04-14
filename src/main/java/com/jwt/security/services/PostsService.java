package com.jwt.security.services;

import com.jwt.security.dto.PostsDto;
import com.jwt.security.user.Posts;

import java.util.List;
import java.util.Optional;

public interface PostsService {
    void addPost(PostsDto posts, String username);

    List<PostsDto> getAllPosts();

    Optional<Posts> getPostById(Long postId);

    void deletePost(Posts posts);

    void savePost(Posts posts);


}
