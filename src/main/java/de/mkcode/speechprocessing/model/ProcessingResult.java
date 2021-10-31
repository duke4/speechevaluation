package de.mkcode.speechprocessing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model for the result of the evaluation.
 * It is automatically parsed as JSON when used as return object in REST API.
 * 
 * @author Marcel König
 */
@Data
@AllArgsConstructor
public class ProcessingResult {
    
    private String mostSpeeches;

    private String mostSecurity;

    private String leastWordy;
}
