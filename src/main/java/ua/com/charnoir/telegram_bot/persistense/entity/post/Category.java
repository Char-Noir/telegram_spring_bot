package ua.com.charnoir.telegram_bot.persistense.entity.post;

import ua.com.charnoir.telegram_bot.persistense.entity.AbstractBaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Category extends AbstractBaseEntity {
    @Lob
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_parent")
    private Category idParent;

    public Category getIdParent() {
        return idParent;
    }

    public void setIdParent(Category idParent) {
        this.idParent = idParent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}