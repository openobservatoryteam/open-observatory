package fr.openobservatory.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchResultsDto<T> {
  private List<T> data;
  private int count;
  private int totalCount;
  private int page;
  private int pageCount;
}
