package fr.openobservatory.backend.dto.output;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Builder
@Data
public class SearchResultsDto<T> {

  private List<T> data;
  private int itemsPerPage;
  private long itemCount;
  private int page;
  private int pageCount;

  public static <E> SearchResultsDto<E> from(Page<E> page) {
    return SearchResultsDto.<E>builder()
        .data(page.toList())
        .itemsPerPage(page.getNumberOfElements())
        .itemCount(page.getTotalElements())
        .page(page.getNumber())
        .pageCount(page.getTotalPages())
        .build();
  }
}
