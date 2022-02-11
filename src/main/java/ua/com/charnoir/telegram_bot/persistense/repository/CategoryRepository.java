package ua.com.charnoir.telegram_bot.persistense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> getCategoryByIdParent_Id(Long parent);
}
