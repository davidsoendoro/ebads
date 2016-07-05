package teadui.models.repositories;

import org.springframework.data.repository.CrudRepository;
import teadui.models.Knowledge;

import java.util.List;

/**
 * Created by dx on 5/19/16.
 */
public interface KnowledgeRepository extends CrudRepository<Knowledge, Long> {

    List<Knowledge> findByLabelIgnoreCase(String label);
}
