package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DieselLogEntity
import com.example.data.model.WorkLogEntity

@Database(
    entities = [WorkLogEntity::class, DieselLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TractorDatabase : RoomDatabase() {
    abstract fun tractorDao(): TractorDao

    companion object {
        @Volatile
        private var INSTANCE: TractorDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE work_logs ADD COLUMN startTimeMinutes INTEGER")
                db.execSQL("ALTER TABLE work_logs ADD COLUMN endTimeMinutes INTEGER")
                db.execSQL("ALTER TABLE work_logs ADD COLUMN breakMinutes INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): TractorDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TractorDatabase::class.java,
                    "tractor_nond_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
