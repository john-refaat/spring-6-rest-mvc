package guru.springframework.spring6restmvc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author john
 * @since 02/07/2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private UUID id;
    private String name;
    private Integer version;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastModifiedDate;

}
