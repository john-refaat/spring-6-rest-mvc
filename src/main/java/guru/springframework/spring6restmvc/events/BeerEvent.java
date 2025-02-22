package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.domain.Beer;
import org.springframework.security.core.Authentication;

/**
 * Author:john
 * Date:22/02/2025
 * Time:04:09
 */
public interface BeerEvent {

    Beer getBeer();
    Authentication getAuthentication();
}
