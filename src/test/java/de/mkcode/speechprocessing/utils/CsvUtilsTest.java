package de.mkcode.speechprocessing.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.mkcode.speechprocessing.model.StatisticsRow;

public class CsvUtilsTest {

    @Test
    public void testReadCsvFromUrl_MalformedUrl() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(CsvUtils.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);
        
        File file = new File("src/test/resources/test_statistics.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl(file.getAbsolutePath());

        assertNotNull(rows);
        assertEquals(0, rows.size());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("ERROR", logsList.get(0).getLevel().levelStr);
        assertEquals("URL '{}' is malformed", logsList.get(0).getMessage());
    }

    @Test
    public void testReadCsvFromUrl_SkippedHeader() {
        File file = new File("src/test/resources/test_statistics_withHeader.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl("file://" + file.getAbsolutePath());
        
        assertNotNull(rows);
        assertEquals(4, rows.size());

        assertEquals("Alexander Abel", rows.get(0).getSpeaker());
        assertEquals("Education Policy", rows.get(0).getTopic());
        assertEquals(LocalDate.of(2012, 10, 30), rows.get(0).getDate());
        assertEquals(5310, rows.get(0).getWords());

        assertEquals("Bernhard Belling", rows.get(1).getSpeaker());
        assertEquals("Coal Subsidies", rows.get(1).getTopic());
        assertEquals(LocalDate.of(2012, 11, 5), rows.get(1).getDate());
        assertEquals(1210, rows.get(1).getWords());

        assertEquals("Caesare Collins", rows.get(2).getSpeaker());
        assertEquals("Coal Subsidies", rows.get(2).getTopic());
        assertEquals(LocalDate.of(2012, 11, 6), rows.get(2).getDate());
        assertEquals(1119, rows.get(2).getWords());

        assertEquals("Alexander Abel", rows.get(3).getSpeaker());
        assertEquals("Internal Security", rows.get(3).getTopic());
        assertEquals(LocalDate.of(2012, 12, 11), rows.get(3).getDate());
        assertEquals(911, rows.get(3).getWords());
    }

    @Test
    public void testReadCsvFromUrl_SkipMalformedDate() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(CsvUtils.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        File file = new File("src/test/resources/test_statistics_withMalformedDate.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl("file://" + file.getAbsolutePath());
        
        assertNotNull(rows);
        assertEquals(1, rows.size());

        assertEquals("Alexander Abel", rows.get(0).getSpeaker());
        assertEquals("Education Policy", rows.get(0).getTopic());
        assertEquals(LocalDate.of(2012, 10, 30), rows.get(0).getDate());
        assertEquals(5310, rows.get(0).getWords());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("Date could not be parsed. Row {} not added.", logsList.get(0).getMessage());
    }

    @Test
    public void testReadCsvFromUrl_SkipMalformedWords() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(CsvUtils.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        File file = new File("src/test/resources/test_statistics_withMalformedWords.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl("file://" + file.getAbsolutePath());
        
        assertNotNull(rows);
        assertEquals(1, rows.size());

        assertEquals("Alexander Abel", rows.get(0).getSpeaker());
        assertEquals("Education Policy", rows.get(0).getTopic());
        assertEquals(LocalDate.of(2012, 10, 30), rows.get(0).getDate());
        assertEquals(5310, rows.get(0).getWords());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("Words could not be parsed. Row {} not added.", logsList.get(0).getMessage());
    }

    @Test
    public void testReadCsvFromUrl_SkipMalformedRow() {
        // get Logback Logger 
        Logger fooLogger = (Logger) LoggerFactory.getLogger(CsvUtils.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        fooLogger.addAppender(listAppender);

        File file = new File("src/test/resources/test_statistics_withMalformedRow.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl("file://" + file.getAbsolutePath());
        
        assertNotNull(rows);
        assertEquals(1, rows.size());

        assertEquals("Alexander Abel", rows.get(0).getSpeaker());
        assertEquals("Education Policy", rows.get(0).getTopic());
        assertEquals(LocalDate.of(2012, 10, 30), rows.get(0).getDate());
        assertEquals(5310, rows.get(0).getWords());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().levelStr);
        assertEquals("CSV row {} malformed.", logsList.get(0).getMessage());
    }

    @Test
    public void testReadCsvFromUrl_Successful() {
        File file = new File("src/test/resources/test_statistics.csv");
        List<StatisticsRow> rows = CsvUtils.readCsvFromUrl("file://" + file.getAbsolutePath());
        
        assertNotNull(rows);
        assertEquals(4, rows.size());

        assertEquals("Alexander Abel", rows.get(0).getSpeaker());
        assertEquals("Education Policy", rows.get(0).getTopic());
        assertEquals(LocalDate.of(2012, 10, 30), rows.get(0).getDate());
        assertEquals(5310, rows.get(0).getWords());

        assertEquals("Bernhard Belling", rows.get(1).getSpeaker());
        assertEquals("Coal Subsidies", rows.get(1).getTopic());
        assertEquals(LocalDate.of(2013, 11, 5), rows.get(1).getDate());
        assertEquals(1210, rows.get(1).getWords());

        assertEquals("Caesare Collins", rows.get(2).getSpeaker());
        assertEquals("Coal Subsidies", rows.get(2).getTopic());
        assertEquals(LocalDate.of(2012, 11, 6), rows.get(2).getDate());
        assertEquals(1119, rows.get(2).getWords());

        assertEquals("Alexander Abel", rows.get(3).getSpeaker());
        assertEquals("Internal Security", rows.get(3).getTopic());
        assertEquals(LocalDate.of(2012, 12, 11), rows.get(3).getDate());
        assertEquals(911, rows.get(3).getWords());
    }
}
