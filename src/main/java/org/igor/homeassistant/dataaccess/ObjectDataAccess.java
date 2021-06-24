package org.igor.homeassistant.dataaccess;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.igor.homeassistant.annotations.HomeAssistantDAO;
import org.igor.homeassistant.exception.DataAccessException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple data access service that will read write to local file.
 */
@Component
@ConfigurationProperties(prefix = "data.access")
public class ObjectDataAccess {
    private String storage;
    private String fileDirectory;
    private File storageDirectory;
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() throws DataAccessException {
        if(storage == null || !storage.equals("file")) {
            throw new DataAccessException("Currently only 'file' storage is supported.");
        }
        if(fileDirectory == null || fileDirectory.isBlank()) {
            throw new DataAccessException("File storage directory is not defined.");
        } else {
            storageDirectory = new File(fileDirectory);
            if(!storageDirectory.exists()) {
                storageDirectory.mkdirs();
            }
            if(!storageDirectory.isDirectory() || !storageDirectory.exists()) {
                throw new DataAccessException("File storage directory is not a directory that exists.");
            }
        }
    }

    /**
     * Takes a data object and uses that as search parameters to search through the collection located by the
     * HomeAssistantDAO annotation on data.  Just a simple .equals() compare on the field.
     *
     * @param data - search parameters
     * @param <D>
     *
     * @return - list of all matching
     *
     * @throws DataAccessException
     */
    public <D> List<D> searchObjects(D data) throws DataAccessException {
        if(data == null) {
            throw new DataAccessException("Can not do an object search on a null value.");
        }
        HomeAssistantDAO haDOA = data.getClass()
                                     .getAnnotation(HomeAssistantDAO.class);
        List<D> matchedEntries = new ArrayList<>();
        try {
            List<D> allData = readFile(haDOA.location(), data);
            for(D fileData : allData) {
                if(matches(data, fileData)) {
                    matchedEntries.add(fileData);
                }
            }
        } catch(IOException e) {
            throw new DataAccessException(e);
        }
        return matchedEntries;
    }

    public <D> void saveObject(D newData) throws DataAccessException {
        if(newData == null) {
            throw new DataAccessException("Can not do a save on a null value.");
        }
        HomeAssistantDAO haDOA = newData.getClass()
                                        .getAnnotation(HomeAssistantDAO.class);
        String[] primaryIdentifiers = haDOA.primaryIdentifiers();
        if(primaryIdentifiers.length < 1) {
            throw new DataAccessException("Can not do a save on a DAO with no primary identifier fields.");
        }
        try {
            List<D> allData = readFile(haDOA.location(), newData);
            ArrayList<Field> primaryFields = getPrimaryFields(newData, primaryIdentifiers);
            ArrayList<D> updatedList = new ArrayList<>();
            boolean updatedExisting = false;
            for(D fileData : allData) {
                if(isMatched(newData, primaryFields, fileData)) {
                    updatedList.add(newData);
                    updatedExisting = true;
                } else {
                    updatedList.add(fileData);
                }
            }
            if(!updatedExisting) {
                updatedList.add(newData);
            }
            safeToFile(haDOA.location(), updatedList);
        } catch(IOException | NoSuchFieldException | IllegalAccessException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Saves the list as a new collection, overwriting the existing one.
     *
     * @param newData - new collection to save
     * @param <D>
     *
     * @throws DataAccessException
     */
    public <D> void saveObjects(List<D> newData) throws DataAccessException {
        if(newData == null || newData.isEmpty()) {
            throw new DataAccessException(
                    "Can not clear out the collection by overwriting with empty file.  Use the remove instead.");
        }
        HomeAssistantDAO haDOA = newData.get(0)
                                        .getClass()
                                        .getAnnotation(HomeAssistantDAO.class);
        try {
            safeToFile(haDOA.location(), newData);
        } catch(IOException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Read the data store for the object that matches the primary identifiers.
     *
     * @param data - object containing primary id search info
     * @param <D>
     *
     * @return - The matching object or null if not found
     *
     * @throws DataAccessException
     */
    public <D> D readObject(D data) throws DataAccessException {
        if(data == null) {
            throw new DataAccessException("Can not do a read on a null value.");
        }
        HomeAssistantDAO haDOA = data.getClass()
                                     .getAnnotation(HomeAssistantDAO.class);
        String[] primaryIdentifiers = haDOA.primaryIdentifiers();
        if(primaryIdentifiers.length < 1) {
            throw new DataAccessException("Can not do a read on a DAO with no primary identifier fields.");
        }
        D foundEntry = null;
        try {
            List<D> allData = readFile(haDOA.location(), data);
            ArrayList<Field> primaryFields = getPrimaryFields(data, primaryIdentifiers);
            for(D fileData : allData) {
                if(isMatched(data, primaryFields, fileData)) {
                    foundEntry = fileData;
                    break;
                }
            }
        } catch(IOException | NoSuchFieldException | IllegalAccessException e) {
            throw new DataAccessException(e);
        }
        return foundEntry;
    }

    private <D> ArrayList<Field> getPrimaryFields(D data, String[] primaryIdentifiers) throws NoSuchFieldException {
        ArrayList<Field> primaryFields = new ArrayList<>();
        for(String idField : primaryIdentifiers) {
            Field field = data.getClass()
                              .getDeclaredField(idField);
            field.setAccessible(true);
            primaryFields.add(field);
        }
        return primaryFields;
    }

    private <D> boolean isMatched(D data, ArrayList<Field> primaryFields, D fileData) throws IllegalAccessException {
        boolean matched = true;
        for(Field primaryField : primaryFields) {
            Object lookup = primaryField.get(data);
            Object row = primaryField.get(fileData);
            if(lookup != null && lookup.equals(row)) {
                continue;
            } else {
                matched = false;
            }
        }
        return matched;
    }

    /**
     * Uses the data object to find matching entries, like the search, and the matched ones are removed from the
     * collection.
     *
     * @param data - search criteria
     * @param <D>
     *
     * @return - list of removed entries
     *
     * @throws DataAccessException
     */
    public <D> List<D> removeObjects(D data) throws DataAccessException {
        if(data == null) {
            throw new DataAccessException("Can not remove objects on a null value.");
        }
        HomeAssistantDAO haDOA = data.getClass()
                                     .getAnnotation(HomeAssistantDAO.class);
        String[] primaryIdentifiers = haDOA.primaryIdentifiers();
        if(primaryIdentifiers.length < 1) {
            throw new DataAccessException("Can not do a removal on a DAO with no primary identifier fields.");
        }
        List<D> removedEntries = new ArrayList<>();
        List<D> savedEntries = new ArrayList<>();
        try {
            List<D> allData = readFile(haDOA.location(), data);
            ArrayList<Field> primaryFields = getPrimaryFields(data, primaryIdentifiers);
            for(D fileData : allData) {
                if(isMatched(data, primaryFields, fileData)) {
                    removedEntries.add(fileData);
                } else {
                    savedEntries.add(fileData);
                }
            }
            safeToFile(haDOA.location(), savedEntries);
        } catch(IOException | NoSuchFieldException | IllegalAccessException e) {
            throw new DataAccessException(e);
        }
        return removedEntries;
    }

    /*
    The method that determines if the fileData is a match to the search parameter data object.
     */
    private <D> boolean matches(D data, D fileData) {
        boolean isEmpty = true;
        boolean matches = false;
        for(Field f : data.getClass()
                          .getDeclaredFields()) {
            try {
                boolean accessible = f.canAccess(data);
                f.setAccessible(true);
                Object dataField = f.get(data);
                Object fileDataField = f.get(fileData);
                if(dataField != null) {
                    isEmpty = false;
                }
                if(fileDataField != null && fileDataField.equals(dataField)) {
                    matches = true;
                    break;
                }
                f.setAccessible(accessible);
            } catch(IllegalAccessException ignore) {
                //we can ignore, if access is illegal then we should not be looking at it for a match
            }
        }
        return isEmpty || matches;
    }

    /*
    Reads the location file and returns a list of entries.
     */
    protected <D> List<D> readFile(String location, D data) throws IOException {
        File dataStorage = getDataStorage(location);
        Type listType = new TypeToken<ArrayList<D>>() {
        }.getType();
        ArrayList<D> objects = gson.fromJson(new FileReader(dataStorage), listType);
        ArrayList<D> fileContents = new ArrayList<>();
        if(objects != null) {
            for(Object object : objects) {
                fileContents.add((D) gson.fromJson(gson.toJson(object), data.getClass()));
            }
        }
        return fileContents;
    }

    /*
    Takes a List and saves it off to a location file
     */
    protected <D> void safeToFile(String location, List<D> data) throws IOException {
        File dataStorage = getDataStorage(location);
        FileWriter fw = new FileWriter(dataStorage);
        gson.toJson(data, fw);
        fw.flush();
        fw.close();
    }

    private File getDataStorage(String location) throws IOException {
        File dataStorage = new File(storageDirectory, location);
        if(!dataStorage.exists()) {
            dataStorage.createNewFile();
            dataStorage.setWritable(true, false);
            dataStorage.setReadable(true, false);
        }
        return dataStorage;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

}

