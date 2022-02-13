package ua.com.charnoir.telegram_bot.persistense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
