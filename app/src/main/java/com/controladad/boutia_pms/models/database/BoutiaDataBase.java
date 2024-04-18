package com.controladad.boutia_pms.models.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

@Database(version = 77,entities = {BazdidYaTamirEntity.class,OprLinesEntity.class,TrackEntity.class,
        Mission_Entity.class,Mission_Tamirat_Entity.class,IradatDakalEntity.class,TamirDakalEntity.class,
        OtherRepairTypeEntity.class})

public abstract class BoutiaDataBase extends RoomDatabase {
    public abstract Mission_Dao getMissionDao();
    public abstract BazdidYaTamirDao getBazdidYaTamir();
    public abstract OprLineDao getOprLineDao ();
    public abstract TrackDao getTrackDao();
    public abstract MissionTamiratDao getMissionTamiratDao();
    public abstract IradatDakalDao getIradatDakalDao();
    public abstract TamirDakalDao getTamirDakalDao();
    public abstract OtherRepairTypeDao getOtherRepairTypeDao();

    public static final Migration MIGRATION_65_66 = new Migration(65, 66) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Tamir_Dakal "
                    + " ADD COLUMN scan_type TEXT");

            database.execSQL("ALTER TABLE BazdidYaTamir "
                    + " ADD COLUMN scan_type TEXT");
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static final Migration MIGRATION_66_67 = new Migration(66, 67) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Track ADD COLUMN year TEXT");
            database.execSQL("ALTER TABLE Track ADD COLUMN month TEXT");
            database.execSQL("ALTER TABLE Track ADD COLUMN day TEXT");
            database.execSQL("ALTER TABLE Track ADD COLUMN hour TEXT");
            database.execSQL("ALTER TABLE Track ADD COLUMN minute TEXT");
            database.execSQL("ALTER TABLE Track ADD COLUMN second TEXT");

            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static final Migration MIGRATION_64_65 = new Migration(64, 65) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    public static final Migration MIGRATION_67_68 = new Migration(67, 68) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE Tamir_Gheyre_Routin (nId INTEGER NOT NULL, mId TEXT, lat TEXT, lng TEXT, submit_date TEXT," +
                            "elate_takhir TEXT, operation_time TEXT, repair_type TEXT, repair_dec TEXT, PRIMARY KEY(nId))");
        }
    };
    public static final Migration MIGRATION_68_69 = new Migration(68, 69) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Mission_Tamirat "
                    + " ADD COLUMN last_update TEXT");

        }
    };

    public static final Migration MIGRATION_69_70 = new Migration(69, 70) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("UPDATE Tamir_Dakal "
                    + " SET operation_time = 'delay'");

        }
    };

    public static final Migration MIGRATION_70_71 = new Migration(70, 71) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Mission ADD COLUMN group_name TEXT");
            database.execSQL("ALTER TABLE Mission ADD COLUMN dispatching_code TEXT");
        }
    };

    public static final Migration MIGRATION_71_72 = new Migration(71, 72) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Mission_Tamirat ADD COLUMN group_name TEXT");
        }
    };
    public static final Migration MIGRATION_72_73 = new Migration(72, 73) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("update Track set mId = 2000637 where mId = 0");
            database.execSQL("update BazdidYaTamir set mId = 2000637 where mId = 0");
        }
    };

    public static final Migration MIGRATION_73_74 = new Migration(73, 74) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Iradat_Dakal ADD COLUMN time TEXT");
        }
    };

    public static final Migration MIGRATION_74_75 = new Migration(74, 75) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Tamir_Dakal ADD COLUMN start_time TEXT");
            database.execSQL("ALTER TABLE Tamir_Dakal ADD COLUMN end_time TEXT");
        }
    };

    public static final Migration MIGRATION_75_76 = new Migration(75, 76) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE BazdidYaTamir ADD COLUMN joshkari TEXT");
            database.execSQL("ALTER TABLE BazdidYaTamir ADD COLUMN goy TEXT");

        }
    };

    public static final Migration MIGRATION_76_77 = new Migration(76, 77) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE BazdidYaTamir "
                    + " ADD COLUMN dakal_bohrani TEXT");
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

}
