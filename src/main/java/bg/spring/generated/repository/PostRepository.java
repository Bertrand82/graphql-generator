package bg.spring.generated.repository;

import bg.spring.generated.pojo.Post;
import java.lang.Long;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Generated  with JavaPoet : SpringSourceRepository isVersion :false */
@RepositoryRestResource
public interface PostRepository extends JpaRepository<Post, Long> {
}
