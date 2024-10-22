package Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import DBConnection.DBConnection;
import LogFiles.LogFile;
import Requests.LogFileRequest;
import Responses.MessageChangeResponse;

public class LogFileTests {
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    private LogFile logFile;

    private static boolean isDBConnectionMocked = false;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        logFile = new LogFile();

        // Mock the static method connectToDB only once
        if (!isDBConnectionMocked) {
            mockStatic(DBConnection.class);
            isDBConnectionMocked = true;
        }
        when(DBConnection.connectToDB()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
    }

    @Test
    void testAddLogFile_Success() throws SQLException {
        LogFileRequest request = new LogFileRequest(1, "test.log",
                "\\home\\ccon148\\LogAnalyzer\\test-logs\\Quarkus.log",
                new java.sql.Timestamp(new Date().getTime()), new java.sql.Timestamp(new Date().getTime()), 0);
        when(mockStatement.executeUpdate()).thenReturn(1);

        MessageChangeResponse response = logFile.addLogFile(request);

        assertTrue(response.isChanged());
        assertEquals("Logdatei wurde hinzugefügt", response.getMessage());
    }

    @Test
    void testAddLogFile_NameExists() throws SQLException {
        LogFileRequest request = new LogFileRequest(1, "existing.log", "/path/to/existing.log",
                new java.sql.Timestamp(new Date().getTime()), 0);

        // Mock the static method LogFileNameExists
        mockStatic(LogFile.class);
        when(LogFile.LogFileNameExists(anyString())).thenReturn(true);

        MessageChangeResponse response = logFile.addLogFile(request);

        assertFalse(response.isChanged());
        assertEquals("Der angegebene Dateipfad existiert nicht: " + request.getLogFilePath(),
                response.getMessage());
    }

    @Test
    void testListLogFiles() throws SQLException {
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("logfile_id")).thenReturn(1, 2);
        when(mockResultSet.getString("logfile_name")).thenReturn("test1.log", "test2.log");
        when(mockResultSet.getString("logfile_pfad")).thenReturn("/path/to/test1.log", "/path/to/test2.log");
        when(mockResultSet.getDate("geaendert_am")).thenReturn(new java.sql.Date(new Date().getTime()));

        ArrayList<LogFileRequest> result = LogFile.listLogFiles();

        assertEquals(2, result.size());
        assertEquals("test1.log", result.get(0).getLogFileName());
        assertEquals("test2.log", result.get(1).getLogFileName());
    }

    @Test
    void testUpdateLogFile_Success() throws SQLException {
        LogFileRequest secondRequest = new LogFileRequest(1, "Quarkus.log",
                "\\\\home\\\\ccon148\\\\LogAnalyzer\\\\test-logs\\\\Quarkus.log",
                new java.sql.Timestamp(new Date().getTime()), 0);
        when(mockStatement.executeUpdate()).thenReturn(1);
        MessageChangeResponse response = logFile.updateLogFile(secondRequest);

        assertFalse(response.isChanged());

        // Verify that the SQL statement was executed
    }

    @Test
    void testDeleteLogFile_Success() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(1);

        MessageChangeResponse response = logFile.deleteLogFile(1);

        assertTrue(response.isChanged());
        assertEquals("Logdatei wurde erfolgreich gelöscht", response.getMessage());
    }

    @Test
    void testLogFileNameExists_True() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = LogFile.LogFileNameExists("existing.log");

        assertTrue(result);
    }

}
