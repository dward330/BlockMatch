package derrick.ward.blockmatch.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import derrick.ward.blockmatch.models.Settings;

public class SettingsDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public static final String DATABASE_NAME = "settings.db";
    public static final String TABLE_NAME = "BlockMatchSettings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAY_MUSIC = "play_music";
    public static final String COLUMN_PUBLISH_SCORE = "publish_score";
    public static final String COLUMN_BLOCK_COVER_IMAGE = "block_cover_image";

    public SettingsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAY_MUSIC + " INTEGER NOT NULL, " +
                COLUMN_PUBLISH_SCORE + " INTEGER NOT NULL, " +
                COLUMN_BLOCK_COVER_IMAGE + " INTEGER NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create Database
        this.onCreate(db);
    }

    /*
     * Save Settings to the database
     */
    public void saveNewSettings(Settings settings, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues dbRecord = new ContentValues();
        dbRecord.put(COLUMN_PLAY_MUSIC, settings.playMusic);
        dbRecord.put(COLUMN_PUBLISH_SCORE, settings.publishScore);
        dbRecord.put(COLUMN_BLOCK_COVER_IMAGE, settings.blockCoverImage);
        db.insert(TABLE_NAME, null, dbRecord);
        db.close();

        Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
    }

    /*
     * Get Settings
     * */
    private List<Settings> getAllSetsOfSettings() {
        String query = "SELECT * FROM " + TABLE_NAME;

        List<Settings> settingsList = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Settings settings;

        if (cursor.moveToFirst()) {
            do {
                settings = new Settings();
                settings.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                settings.playMusic = cursor.getInt(cursor.getColumnIndex(COLUMN_PLAY_MUSIC));
                settings.publishScore = cursor.getInt(cursor.getColumnIndex(COLUMN_PUBLISH_SCORE));
                settings.blockCoverImage = cursor.getInt(cursor.getColumnIndex(COLUMN_BLOCK_COVER_IMAGE));
                settingsList.add(settings);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return settingsList;
    }

    /**
     * Get Settings
     * @return Settings
     */
    public derrick.ward.blockmatch.models.Settings getSettings() {
        derrick.ward.blockmatch.models.Settings settings = null;

        List<derrick.ward.blockmatch.models.Settings> setOfSettings = this.getAllSetsOfSettings();

        if (!setOfSettings.isEmpty()) {
            settings = setOfSettings.get(0);
        } else { // Save Default Settings, since non were found
            derrick.ward.blockmatch.models.Settings newSettings = new derrick.ward.blockmatch.models.Settings();
            newSettings.blockCoverImage = 0;
            newSettings.publishScore = 1;
            newSettings.playMusic = 1;

            this.saveNewSettings(newSettings, context);
            settings = this.getAllSetsOfSettings().get(0);
        }

        return settings;
    }

    /*
     * Updates Settings with the supplied id
     * */
    public void updateSettings(long settingsId, Context context, Settings settings) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_NAME + " SET play_music = '" + settings.playMusic +
                "', publish_score = '" + settings.publishScore + "', block_cover_image = '" + settings.blockCoverImage +
                "' WHERE _id='" + settingsId + "'");

        Toast.makeText(context, "Preferences/Settings Updated Successfully", Toast.LENGTH_SHORT).show();
    }
}

