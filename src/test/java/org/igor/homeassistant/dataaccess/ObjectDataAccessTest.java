package org.igor.homeassistant.dataaccess;

import org.igor.homeassistant.exception.DataAccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDataAccessTest {

    @Test
    void testInit() {
        ObjectDataAccess objectDataAccess = new ObjectDataAccess();
        DataAccessException exception = assertThrows(DataAccessException.class,() -> objectDataAccess.init());
        assertEquals("Currently only 'file' storage is supported.",exception.getMessage());
        objectDataAccess.setStorage("database");
        exception = assertThrows(DataAccessException.class,() -> objectDataAccess.init());
        assertEquals("Currently only 'file' storage is supported.",exception.getMessage());
        objectDataAccess.setStorage("file");
        exception = assertThrows(DataAccessException.class,() -> objectDataAccess.init());
        assertEquals("File storage directory is not defined.",exception.getMessage());
        objectDataAccess.setFileDirectory("/this/directory/should/not/exist");
        exception = assertThrows(DataAccessException.class,() -> objectDataAccess.init());
        assertEquals("File storage directory is not a directory that exists.",exception.getMessage());
        objectDataAccess.setFileDirectory("/tmp");
        assertDoesNotThrow(() -> objectDataAccess.init());
    }

    @Test
    void testSearchObjects() throws DataAccessException, IOException {
        ObjectDataAccess objectDataAccess = new ObjectDataAccess();
        objectDataAccess.setStorage("file");
        objectDataAccess.setFileDirectory("/tmp");
        objectDataAccess.init();
        List<TestModel> testData = new ArrayList<>();
        testData.add(new TestModel("1","name"));
        testData.add(new TestModel("2","the name"));
        testData.add(new TestModel("3","three"));
        objectDataAccess.safeToFile("testData",testData);
        DataAccessException exception = assertThrows(DataAccessException.class,() -> objectDataAccess.searchObjects(null));
        assertEquals("Can not do an object search on a null value.",exception.getMessage());
        List<TestModel> matched = objectDataAccess.searchObjects(new TestModel(null,"name"));
        assertEquals(1,matched.size());
        assertEquals("1",matched.get(0).getId());
        matched = objectDataAccess.searchObjects(new TestModel(null,null));
        assertEquals(3,matched.size());
        assertEquals("1",matched.get(0).getId());
        assertEquals("2",matched.get(1).getId());
        assertEquals("3",matched.get(2).getId());
    }

    @Test
    void testReadFileAndSafeToFile() throws IOException, DataAccessException {
        ObjectDataAccess objectDataAccess = new ObjectDataAccess();
        objectDataAccess.setStorage("file");
        objectDataAccess.setFileDirectory("/tmp");
        objectDataAccess.init();
        List<TestModel> testData = new ArrayList<>();
        testData.add(new TestModel("1","one"));
        testData.add(new TestModel("2","two"));
        testData.add(new TestModel("3","three"));
        assertDoesNotThrow(() -> objectDataAccess.safeToFile("test_data",testData));
        List<TestModel> fileContents = objectDataAccess.readFile("test_data",new TestModel());
        assertEquals(3,fileContents.size());
        assertEquals("1",fileContents.get(0).getId());
        assertEquals("2",fileContents.get(1).getId());
        assertEquals("3",fileContents.get(2).getId());
    }
}