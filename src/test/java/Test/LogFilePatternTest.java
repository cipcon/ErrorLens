package Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

import DBConnection.DBConnection;
import LogFilePattern.LogFilePattern;
import Requests.PatternLogFileRequest;
import Requests.PatternRequest;
import Requests.UpdatePatternsRanksRequest;
import Responses.MessageChangeResponse;

@RunWith(MockitoJUnitRunner.class)
public class LogFilePatternTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    private LogFilePattern logFilePattern;

    @Before
    public void setUp() throws Exception {
        logFilePattern = new LogFilePattern();

        // Mock the DBConnection.connectToDB() method
        try (var mocked = mockStatic(DBConnection.class)) {
            mocked.when(DBConnection::connectToDB).thenReturn(mockConnection);
        }
    }

    @Test
    public void testAddPatternToLogFile_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        PatternLogFileRequest request = new PatternLogFileRequest(1, 1, 1);
        MessageChangeResponse response = logFilePattern.addPatternToLogFile(request);

        assertTrue(response.isChanged());
        assertEquals("Pattern erfolgreich hinzugefügt", response.getMessage());
    }

    @Test
    public void testAddPatternToLogFile_AlreadyExists() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(true);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        PatternLogFileRequest request = new PatternLogFileRequest(1, 1, 1);
        MessageChangeResponse response = logFilePattern.addPatternToLogFile(request);

        assertFalse(response.isChanged());
        assertEquals("Das Pattern ist bereits in diesem Logfile vorhanden", response.getMessage());
    }

    @Test
    public void testGetPatternsForLogFile() throws SQLException {
        // Ensure the ArrayList is populated
        when(logFilePattern.getPatternsForLogFile(anyInt())).thenReturn(new ArrayList<>(Arrays.asList(
                new PatternRequest(1, "Pattern1", "regex1", "desc1", "high", 1),
                new PatternRequest(2, "Pattern2", "regex2", "desc2", "medium", 2))));

        ArrayList<PatternRequest> patterns = logFilePattern.getPatternsForLogFile(1);

        assertEquals(2, patterns.size());
        assertEquals(1, patterns.get(0).getPatternId());
        assertEquals("Pattern1", patterns.get(0).getPatternName());
        assertEquals(2, patterns.get(1).getPatternId());
        assertEquals("Pattern2", patterns.get(1).getPatternName());
    }

    @Test
    public void testUpdatePatternRanks_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeBatch()).thenReturn(new int[] { 1, 1 });

        ArrayList<PatternRequest> patterns = new ArrayList<>();
        patterns.add(new PatternRequest(1, "Pattern1", "regex1", "desc1", "high", 1));
        patterns.add(new PatternRequest(2, "Pattern2", "regex2", "desc2", "medium", 2));
        UpdatePatternsRanksRequest request = new UpdatePatternsRanksRequest(1, patterns);

        MessageChangeResponse response = logFilePattern.updatePatternRanks(request);

        assertTrue(response.isChanged());
        assertEquals("", response.getMessage());
    }

    @Test
    public void testDeletePatternFromLogFile_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(true);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.executeUpdate()).thenReturn(1);

        PatternLogFileRequest request = new PatternLogFileRequest(1, 1, 1);
        MessageChangeResponse response = logFilePattern.deletePatternFromLogFile(request);

        // Add this line to verify the executeUpdate() method is called
        verify(mockStatement).executeUpdate();

        assertTrue(response.isChanged());
        assertEquals("Pattern erfolgreich aus Logfile gelöscht", response.getMessage());
    }

    @Test
    public void testDeletePatternFromLogFile_NotFound() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        PatternLogFileRequest request = new PatternLogFileRequest(1, 1, 1);
        MessageChangeResponse response = logFilePattern.deletePatternFromLogFile(request);

        assertFalse(response.isChanged());
        assertEquals("Das Pattern ist in diesem Logfile nicht vorhanden", response.getMessage());
    }

    @Test
    public void testCheckIfPatternIsInLogFile_True() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(true);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = logFilePattern.checkIfPatternIsInLogFile(1, 1);

        assertTrue(result);
    }

    @Test
    public void testCheckIfPatternIsInLogFile_False() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = logFilePattern.checkIfPatternIsInLogFile(1, 1);

        assertFalse(result);
    }

    @Test
    public void testCheckIfPatternExistsInLogFile_True() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(true);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = logFilePattern.checkIfPatternExistsInLogFile(1, 1);

        assertTrue(result);
    }

    @Test
    public void testCheckIfPatternExistsInLogFile_False() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = logFilePattern.checkIfPatternExistsInLogFile(1, 1);

        assertFalse(result);
    }
}