package com.gxun.thrity_music;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(entities = {Song.class},version = 1,exportSchema = false)
public abstract class SongDatebase extends RoomDatabase {
    private static SongDatebase INSTANCE;

    static synchronized SongDatebase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SongDatebase.class, "song_database").addMigrations(MIGRATION_1_2).build();
        }
        return INSTANCE;
    }

    abstract SongDao getSongDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table song add column isPlaying INTEGER not null default 0");
        }

    };
}