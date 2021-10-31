package de.mkcode.speechprocessing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.mkcode.speechprocessing.model.ProcessingResult;

public class SpeechProcessingControllerTest {
    
    @Test
    public void testProcessStatisticsFromStatisticsRow_Successful() {
        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics.csv");

        Map<String, String> params = new HashMap<>();
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertEquals("Bernhard Belling", result.getMostSpeeches());
        assertEquals("Alexander Abel", result.getMostSecurity());
        assertEquals("Caesare Collins", result.getLeastWordy());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_UnknownParam() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(SpeechProcessingController.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics.csv");

        Map<String, String> params = new HashMap<>();
        params.put("url", "file://" + file.getAbsolutePath());
        params.put("urleins", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertNull(result.getMostSpeeches());
        assertNull(result.getMostSecurity());
        assertNull(result.getLeastWordy());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("parameter '{}' is not supported", logsList.get(0).getMessage());
        assertEquals("WARN", logsList.get(1).getLevel().levelStr);
        assertEquals("parameter '{}' is not supported", logsList.get(1).getMessage());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_Url1ParamMissing() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(SpeechProcessingController.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics.csv");

        Map<String, String> params = new HashMap<>();
        params.put("url2", "file://" + file.getAbsolutePath());
        params.put("url3", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertNull(result.getMostSpeeches());
        assertNull(result.getMostSecurity());
        assertNull(result.getLeastWordy());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("parameters seem not to be correctly ordered", logsList.get(0).getMessage());
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("parameters seem not to be correctly ordered", logsList.get(1).getMessage());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_WrongParamOrder() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(SpeechProcessingController.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics.csv");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("url2", "file://" + file.getAbsolutePath());
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertEquals("Bernhard Belling", result.getMostSpeeches());
        assertEquals("Alexander Abel", result.getMostSecurity());
        assertEquals("Caesare Collins", result.getLeastWordy());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("parameters seem not to be correctly ordered", logsList.get(0).getMessage());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_DoubledRowFiltered() {
        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics_doubleRow.csv");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        // CSV contains the same Casesare Collins row twice.
        // This row is filtered out. Otherwise Caesare Collins wouldn't be the least wordy speaker.
        assertNotNull(result);
        assertEquals("Bernhard Belling", result.getMostSpeeches());
        assertEquals("Alexander Abel", result.getMostSecurity());
        assertEquals("Caesare Collins", result.getLeastWordy());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_NotUniqueMostSpeeches() {
        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics_notUniqueMostSpeeches.csv");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertEquals(null, result.getMostSpeeches());
        assertEquals("Alexander Abel", result.getMostSecurity());
        assertEquals("Caesare Collins", result.getLeastWordy());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_NotUniqueMostSecurity() {
        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics_notUniqueMostSecurity.csv");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertEquals("Bernhard Belling", result.getMostSpeeches());
        assertEquals(null, result.getMostSecurity());
        assertEquals("Caesare Collins", result.getLeastWordy());
    }

    @Test
    public void testProcessStatisticsFromStatisticsRow_NotUniqueLeastWordy() {
        SpeechProcessingController spc = new SpeechProcessingController();

        File file = new File("src/test/resources/test_statistics_notUniqueLeastWordy.csv");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("url1", "file://" + file.getAbsolutePath());

        ProcessingResult result = spc.processStatistics(params);

        assertNotNull(result);
        assertEquals("Bernhard Belling", result.getMostSpeeches());
        assertEquals("Alexander Abel", result.getMostSecurity());
        assertEquals(null, result.getLeastWordy());
    }
}
