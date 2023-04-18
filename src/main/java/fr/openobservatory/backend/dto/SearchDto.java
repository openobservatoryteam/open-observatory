package fr.openobservatory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchDto {

  @Range(message = "itemsPerPage.range", min = 1, max = 100)
  private Integer itemsPerPage = 10;

  @Range(message = "page.range", min = 0)
  private Integer page = 0;
}
