package d.ward.blockmatch.services;

import java.util.UUID;

public class FirebaseUtility {
    public static final String userPhotosFolderName = "userPhotos";
    public static final String storageDomainUrl = "gs://blockmatch-26d2b.appspot.com";
    public static final String userPhotosFolderPath = storageDomainUrl+"/"+userPhotosFolderName;

    /**
     * Returns a unique string
     * @return name as a string
     */
    public static String getUniqueName() {
        String name = UUID.randomUUID().toString();

        return name;
    }
}
