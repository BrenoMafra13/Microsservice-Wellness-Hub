package ca.gbc.comp3095.wellnessresourceservice.repository;

import ca.gbc.comp3095.wellnessresourceservice.model.WellnessResource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WellnessResourceRepository extends JpaRepository<WellnessResource, Long> {

    List<WellnessResource> findByCategoryIgnoreCase(String category);

    @Query("""
            select r from WellnessResource r
            where lower(r.title) like lower(concat('%', :keyword, '%'))
               or lower(r.description) like lower(concat('%', :keyword, '%'))
            """)
    List<WellnessResource> searchByKeyword(@Param("keyword") String keyword);
}
