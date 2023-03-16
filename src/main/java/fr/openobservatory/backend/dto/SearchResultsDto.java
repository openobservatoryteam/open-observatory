package fr.openobservatory.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Data
public class SearchResultsDto<T> {
  private List<T> data;
  private int count;
  private long totalCount;
  private int page;
  private int pageCount;

  public static <E> SearchResultsDto<E> from(Page<E> page) {
    return new SearchResultsDto<E>(
        page.toList(),
        page.getNumberOfElements(),
        page.getTotalElements(),
        page.getNumber(),
        page.getTotalPages());
  }
}
