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
        if(storage == null || !storage.equals("file")){
            throw new DataAccessException("Currently only 'file' storage is supported.");
        }
        if(fileDirectory == null || fileDirectory.isBlank()){
            throw new DataAccessException("File storage directory is not defined.");
        }else{
            storageDirectory = new File(fileDirectory);
            if(!storageDirectory.isDirectory() || !storageDirectory.exists()){
                throw new DataAccessException("File storage directory is not a directory that exists.");
            }
        }
    }

    /**
     * Takes a data object and uses that as search parameters to search through the collection
     * located by the HomeAssistantDAO annotation on data.  Just a simple .equals() compare on the
     * field.
     *
     * @param data - search parameters
     * @param <D>
     * @return - list of all matching
     * @throws DataAccessException
     */
    public <D> List<D> searchObjects(D data) throws DataAccessException {
        if(data == null){
            throw new DataAccessException("Can not do an object search on a null value.");
        }
        HomeAssistantDAO haDOA = data.getClass().getAnnotation(HomeAssistantDAO.class);
        List<D> matchedEntries = new ArrayList<>();
        try {
            List<D> allData = readFile(haDOA.location(),data);
            for(D fileData:allData){
                if(matches(data,fileData)){
                    matchedEntries.add(fileData);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException(e);
        }
        return matchedEntries;
    }

    public <D> List<D> saveObject(D newData) throws DataAccessException {
        /*
        read the existing list, look for a match and update the location in the list if found.

        save the list.
         */
        return null;
    }

    public <D> List<D> saveObjects(List<D> newData) throws DataAccessException {
        /*
        save the list to the location, overwriting.  return saved list
         */
        return null;
    }

    public <D> D readObject(D data) throws DataAccessException {
        /*
        look at the annotation, if no primary indicator fields throw exception

        read file then look through the list for the matching entry
         */
        return null;
    }

    public <D> D removeObject(D data) throws DataAccessException {
        /*
        look at the annotation, if no primary indicator fields throw exception

        read file then look through the list for the matching entry,
        remove it from the list and save to return,
        then save the trimmed list.
         */
        return null;
    }

    /*
    The method that determines if the fileData is a match to the search parameter data object.
     */
    private <D> boolean matches(D data, D fileData) {
        boolean isEmpty = true;
        boolean matches = false;
        for (Field f : data.getClass().getDeclaredFields()) {
            try {
                boolean accessible = f.canAccess(data);
                f.setAccessible(true);
                Object dataField = f.get(data);
                Object fileDataField = f.get(fileData);
                if (dataField != null) {
                    isEmpty = false;
                }
                if (fileDataField != null && fileDataField.equals(dataField)){
                    matches = true;
                    break;
                }
                f.setAccessible(accessible);
            } catch (IllegalAccessException ignore) {
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
        Type listType = new TypeToken<ArrayList<D>>(){}.getType();
        ArrayList<D> objects = gson.fromJson(new FileReader(dataStorage),listType);
        ArrayList<D> fileContents = new ArrayList<>();
        for(Object object:objects){
            fileContents.add((D)gson.fromJson(gson.toJson(object), data.getClass()));
        }
        return fileContents;
    }

    /*
    Takes a List and saves it off to a location file
     */
    protected <D> void safeToFile(String location, List<D> data) throws IOException {
        File dataStorage = getDataStorage(location);
        System.out.println("saving ("+data.size()+") items to "+dataStorage.getAbsolutePath());
        FileWriter fw = new FileWriter(dataStorage);
        gson.toJson(data,fw);
        fw.flush();
        fw.close();
    }

    private File getDataStorage(String location) throws IOException {
        File dataStorage = new File(storageDirectory, location);
        if(!dataStorage.exists()){
            dataStorage.createNewFile();
            dataStorage.setWritable(true,false);
            dataStorage.setReadable(true,false);
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

