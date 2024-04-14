package com.jwt.security.services.impl;

import com.jwt.security.dto.PostsDto;
import com.jwt.security.repositories.PostsRepository;
import com.jwt.security.repositories.UserRepository;
import com.jwt.security.services.PostsService;
import com.jwt.security.user.Posts;
import com.jwt.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostsServiceImpl implements PostsService {
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void addPost(PostsDto postsDto, String username) {
        User author = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        Posts post = Posts.builder()
                .title(postsDto.getTitle())
                .content(postsDto.getContent())
                .author(author)
                .build();
        postsRepository.save(post);
    }

    @Override
    public List<PostsDto> getAllPosts() {
        List<Posts> posts = postsRepository.findAll();
        List<PostsDto> postDTOs = new ArrayList<>();
        for (Posts post : posts) {
            PostsDto postDTO = new PostsDto();
            postDTO.setId(post.getId());
            postDTO.setAuthorName(post.getAuthor().getFirstname() + " " + post.getAuthor().getLastname());
            postDTO.setTitle(post.getTitle());
            postDTO.setContent(post.getContent());
            postDTOs.add(postDTO);
        }
        return postDTOs;
    }

    @Override
    public Optional<Posts> getPostById(Long postId) {
        return postsRepository.findById(postId);
    }

    @Override
    public void deletePost(Posts posts) {
        postsRepository.delete(posts);
    }

    @Override
    public void savePost(Posts posts) {
        postsRepository.save(posts);
    }


}
