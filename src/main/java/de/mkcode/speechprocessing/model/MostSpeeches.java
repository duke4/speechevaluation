package de.mkcode.speechprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model to hold the speaker with the currently 
 * highest count of speeches in 2013 while processing CSV rows.
 * 
 * @author Marcel König
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class MostSpeeches {
    
    private String speaker;

    private int count;
}
