package bg.spring.generated.pojo;

import javax.persistence.Entity;

/**
 * Pojo Generated by JavaPoet : SourcePojoEnhanced v4 
 *  isVersion :false */
@Entity
public class Post {
    protected String id;

    protected String title;

    protected String text;

    protected String category;

    protected Author author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}