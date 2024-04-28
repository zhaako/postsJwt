package com.jwt.security.demo;

import com.jwt.security.config.JwtService;
import com.jwt.security.dto.PostsDto;
import com.jwt.security.repositories.PostsRepository;
import com.jwt.security.repositories.UserRepository;
import com.jwt.security.services.PostsService;
import com.jwt.security.services.UserService;
import com.jwt.security.user.Posts;
import com.jwt.security.user.Role;
import com.jwt.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<String> addPost(@RequestBody PostsDto postsDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Требуется аутентификация");
        }
        String username = userDetails.getUsername();
        postsService.addPost(postsDto, username);
        return ResponseEntity.ok("Пост успешно добавлен");
    }
    @GetMapping("/pp")
    public List<PostsDto> getAllPosts() {
        return postsService.getAllPosts();
    }

    @GetMapping("/ppf")
    public List<PostsDto> getFAllPosts(@RequestParam(required = false) User authorName,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) String content){
        List<Posts> filteredPosts;
        if (authorName != null) {
            filteredPosts = postsRepository.findByAuthor(authorName);
        } else if (title != null) {
            filteredPosts = postsRepository.findByTitle(title);
        } else if (content != null) {
            filteredPosts = postsRepository.findByContent(content);
        } else {
            filteredPosts = postsRepository.findAll();
        }
        return filteredPosts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PostsDto convertToDTO(Posts post) {
        PostsDto postDTO = new PostsDto();
        postDTO.setAuthorName(post.getAuthor().getFirstname() + post.getAuthor().getLastname());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        return postDTO;
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findUserByEmail(currentUsername);
        Optional<Posts> optionalPost = postsService.getPostById(postId);
        if (optionalPost.isPresent()) {
            Posts post = optionalPost.get();
            if (currentUser.getRole().equals(Role.ADMIN) || currentUser.getId().equals(post.getAuthor().getId())) {
                postsService.deletePost(post);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав для удаления этого поста.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{putId}")
    public ResponseEntity<String> putPost(@PathVariable Long putId, @RequestBody PostsDto postsDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findUserByEmail(currentUsername);
        Optional<Posts> optionalPost = postsService.getPostById(putId);
        if (optionalPost.isPresent()) {
            Posts post = optionalPost.get();
            if (currentUser.getRole().equals(Role.ADMIN) || currentUser.getId().equals(post.getAuthor().getId())) {
                post.setTitle(postsDto.getTitle());
                post.setContent(postsDto.getContent());
                postsService.savePost(post);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав для обновления этого поста.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/asc")
    public List<Posts> getPostsByNameAsc() {
        return postsService.getPostsByNameAsc();
    }

    @GetMapping("/desc")
    public List<Posts> getPostsByNameDesc() {
        return postsService.findPostsByNameDesc();
    }
}
