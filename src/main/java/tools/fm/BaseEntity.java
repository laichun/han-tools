package tools.fm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Getter
@Setter
public class BaseEntity implements Serializable {
  private static final long serialVersionUID = 8948437944054606982L;

  private String id;

  @JsonIgnore
  public <T> void copyToDTO(T dto, String... ignoreProperties) {
    BeanUtils.copyProperties(this, dto, ignoreProperties);
  }

}
