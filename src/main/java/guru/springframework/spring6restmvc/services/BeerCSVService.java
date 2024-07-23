package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;

import java.io.File;
import java.util.List;

/**
 * @author john
 * @since 23/07/2024
 */
public interface BeerCSVService {

    List<BeerCSVRecord> convertCSV(File csvFile);
}
