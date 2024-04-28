package com.jwt.security.repositories;

import com.jwt.security.dto.PostsDto;
import com.jwt.security.user.Posts;
import com.jwt.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByAuthor(User author);
    List<Posts> findByTitle(String title);
    List<Posts> findByContent(String content);
    List<Posts> findAllByOrderByTitleAsc();
    List<Posts> findAllByOrderByTitleDesc();
}
