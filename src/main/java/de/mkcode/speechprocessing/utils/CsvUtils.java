package de.mkcode.speechprocessing.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.mkcode.speechprocessing.model.StatisticsRow;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling CSV files.
 * 
 * @author Marcel König
 */
@Slf4j
public class CsvUtils {
    
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * It is tried to load the CSV file from the given URL string.
     * If a date or the words cannot be parsed to {@link LocalDate} or int,
     * a warning is logged and the row is skipped.
     * (Header line is therefore optional in CSV file; if present, it will be skipped)
     * 
     * @param urlString URL of the CSV file
     * @return list of {@link StatisticsRow} (every correct row from CSV file)
     */
    public static List<StatisticsRow> readCsvFromUrl(String urlString) {
        List<StatisticsRow> rows = new ArrayList<>();

        try {
            URL url = new URL(urlString);
            CSVParser csvParser = CSVParser.parse(url, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
            for(CSVRecord csvRecord : csvParser) {
                try {
                    String speaker = csvRecord.get(0).strip();
                    String topic = csvRecord.get(1).strip();
                    LocalDate date = LocalDate.parse(csvRecord.get(2).strip(), dtf);
                    int words = Integer.parseInt(csvRecord.get(3).strip());

                    rows.add(new StatisticsRow(speaker, topic, date, words));
                } catch (DateTimeParseException e1) {
                    log.warn("Date could not be parsed. Row {} not added.", csvRecord);
                } catch (NumberFormatException e2) {
                    log.warn("Words could not be parsed. Row {} not added.", csvRecord);
                }
            }
        } catch (MalformedURLException e1) {
            log.error("URL '{}' is malformed", urlString);
        } catch (IOException e2) {
            log.error("Error reading file from URL '{}'", urlString);
        }

        return rows;
    }
}
