package de.mkcode.speechprocessing.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mkcode.speechprocessing.model.MostSecurity;
import de.mkcode.speechprocessing.model.MostSpeeches;
import de.mkcode.speechprocessing.model.ProcessingResult;
import de.mkcode.speechprocessing.model.StatisticsRow;
import de.mkcode.speechprocessing.utils.CsvUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller class reacting to GET REST calls for path '/evaluation'.
 * 
 * CSV files are loaded from given URLs and parsed.
 * Lines which 'date' or 'words' value cannot be parsed are ignored.
 * Duplicated lines are filtered out.
 * 
 * The speech statistics from the CSV files are evaluated for the following questions:
 *  - Which politician gave the most speeches in 2013?
 *  - Which politician gave the most speeches on the topic 'internal security'?
 *  - Which politician spoke the fewest words overall?
 * 
 * If no or no unique answer is possible for a question, this field is filled with null.
 * 
 * @author Marcel König
 */
@Slf4j
@RestController
@RequestMapping("/")
public class SpeechProcessingController {
    
    /**
     * Method accepts a list of URLs.
     * Parameter must be named 'url1', 'url2' and so on (order is important).
     * Unsupported parameters are skipped.
     * Rows from CSV file are parsed into a set of {@link StatisticsRow}.
     * All rows are then processed and evaluated.
     * 
     * @param params Map with key-value pairs of the UrlParameters
     * @return result of evaluation
     */
    @Operation(summary = "Evaluate speech statistics retrieved from CSV files")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Statistics evaluated", 
          content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ProcessingResult.class)) })}
    )
    @GetMapping("evaluation")
    public ProcessingResult processStatistics(@Parameter(description = "list of URLs") @RequestParam Map<String, String> params) {
        int urlCount = 1;

        // Set to filter duplicates
        Set<StatisticsRow> allRows = new HashSet<>();

        // Parsing CSV file from every given URL
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if parameter keys are valid
            String keyValidation = "url" + urlCount;
            if(key.startsWith("url") && key.length() > 3 && key.substring(3).matches("^[0-9]+$")) {
                if (keyValidation.equals(key)) {
                    allRows.addAll(CsvUtils.readCsvFromUrl(value));
                    urlCount++;
                } else {
                    log.warn("parameters seem not to be correctly ordered", key);
                }
            } else {
                log.warn("parameter '{}' is not supported", key);
            }
        }

        return processStatisticsFromStatisticsRow(allRows);
    }

    /**
     * Each row from CSV file is processed and evaluated.
     * Speeches in 2013, speeches about 'internal security' and words are counted for each speaker.
     * 
     * For every question it is evaluated if there is a unique result.
     * This result is added to {@link ProcessingResult}, otherwise it is null.
     * 
     * @param rows collected rows from the given CSV files
     * @return result of evaluation
     */
    private ProcessingResult processStatisticsFromStatisticsRow(Set<StatisticsRow> rows) {
        
        MostSpeeches mostSpeeches = null;
        MostSecurity mostSecurity = null;

        Map<String, Integer> speechesCount = new HashMap<>();
        Map<String, Integer> securityCount = new HashMap<>();
        Map<String, Integer> wordsCount = new HashMap<>();

        for (StatisticsRow row : rows) {
            String speaker = row.getSpeaker();
            String topic = row.getTopic();
            LocalDate date = row.getDate();
            int words = row.getWords();

            // Count speeches from 2013 for the rows speaker.
            // If its the first occurence a new map entry is generated otherwise the current entry is updated
            // At the same time it is checked whether it is a new highest value (mostSpeeches)
            if (date.getYear() == 2013) {
                Integer speechesCountOfSpeaker = speechesCount.get(speaker);
                if(speechesCountOfSpeaker == null) {
                    speechesCountOfSpeaker = 0;
                }
                speechesCountOfSpeaker++;
                speechesCount.put(speaker, speechesCountOfSpeaker);

                if(mostSpeeches == null || speechesCountOfSpeaker > mostSpeeches.getCount()) {
                    mostSpeeches = new MostSpeeches(speaker, speechesCountOfSpeaker);
                } else if (speechesCountOfSpeaker == mostSpeeches.getCount()) {
                    mostSpeeches.setSpeaker(null);
                }
            }

            // Count speeches about 'internal security' for the rows speaker.
            // If its the first occurence a new map entry is generated otherwise the current entry is updated
            // At the same time it is checked whether it is a new highest value (mostSecurity)
            if (topic.equalsIgnoreCase("internal security")) {
                Integer securityCountOfSpeaker = securityCount.get(speaker);
                if(securityCountOfSpeaker == null) {
                    securityCountOfSpeaker = 0;
                }
                securityCountOfSpeaker++;
                securityCount.put(speaker, securityCountOfSpeaker);

                if(mostSecurity == null || securityCountOfSpeaker > mostSecurity.getCount()) {
                    mostSecurity = new MostSecurity(speaker, securityCountOfSpeaker);
                } else if (securityCountOfSpeaker == mostSecurity.getCount()) {
                    mostSecurity.setSpeaker(null);
                }
            }

            // Count words for the rows speaker.
            // If its the first occurence a new map entry is generated otherwise the current entry is updated
            if(words > 0) {
                Integer wordsCountOfSpeaker = wordsCount.get(speaker);
                if(wordsCountOfSpeaker == null) {
                    wordsCountOfSpeaker = 0;
                }
                wordsCountOfSpeaker += words;
                wordsCount.put(speaker, wordsCountOfSpeaker);
            }
        }

        // Sort the entries of the wordsCount map by its value (words)
        List<Entry<String, Integer>> sortedWordsCount = wordsCount.entrySet().stream().sorted(Entry.comparingByValue()).collect(Collectors.toList());
        
        // Find first unique entry
        // If no unique entry can be found, the result is null
        Entry<String, Integer> firstLeastWordy = null;
        Entry<String, Integer> secondLeastWordy = null;
        if (sortedWordsCount != null && !sortedWordsCount.isEmpty()) {
            firstLeastWordy = sortedWordsCount.get(0);
            secondLeastWordy = sortedWordsCount.get(1);

            if (firstLeastWordy.getValue().equals(secondLeastWordy.getValue())) {
                firstLeastWordy = null;
            }
        }
        
        return new ProcessingResult(
            mostSpeeches == null ? null : mostSpeeches.getSpeaker(),
            mostSecurity == null ? null : mostSecurity.getSpeaker(),
            firstLeastWordy == null ? null : firstLeastWordy.getKey());
    }
}
