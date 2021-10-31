package de.mkcode.speechprocessing.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model for a single row from statistics CSV file.
 * 
 * @author Marcel König
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class StatisticsRow {
    
    private String speaker;

    private String topic;

    private LocalDate date;

    private int words;
}
