package fr.openobservatory.backend.dto.input;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class PaginationDto {

  @Range(message = "itemsPerPage.range", min = 1, max = 100)
  private Integer itemsPerPage;

  @Range(message = "page.range", min = 0)
  private Integer page;
}
