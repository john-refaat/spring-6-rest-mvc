package guru.springframework.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

/**
 * @author john
 * @since 23/07/2024
 */
@Slf4j
@Service
public class BeerCSVServiceImpl implements BeerCSVService {
    @Override
    public List<BeerCSVRecord> convertCSV(File csvFile) {
        log.info("Loading CSV file {}", csvFile.getName());
        try {
            List<BeerCSVRecord> records = new CsvToBeanBuilder<BeerCSVRecord>(new FileReader(csvFile)).withType(BeerCSVRecord.class).build().parse();
            log.info("{} records loaded", records.size());
            return records;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
