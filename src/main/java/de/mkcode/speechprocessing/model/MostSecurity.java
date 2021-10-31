package de.mkcode.speechprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model to hold the speaker with the currently 
 * highest count of speeches about 'internal security' while processing CSV rows.
 * 
 * @author Marcel König
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class MostSecurity {
    
    private String speaker;

    private Integer count;
}
