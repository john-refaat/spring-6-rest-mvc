package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.domain.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

/**
 * Author:john
 * Date:20/02/2025
 * Time:04:54
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BeerPatchEvent {
    private Beer beer;
    private Authentication authentication;
}
