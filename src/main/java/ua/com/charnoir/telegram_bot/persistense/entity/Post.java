package ua.com.charnoir.telegram_bot.persistense.entity;

import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post extends AbstractBaseEntity {
    @Lob
    @Column(name = "descr", nullable = false)
    private String descr;

    @Lob
    @Column(name = "file")
    private String file;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category idCategory;

    public Category getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Category idCategory) {
        this.idCategory = idCategory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}