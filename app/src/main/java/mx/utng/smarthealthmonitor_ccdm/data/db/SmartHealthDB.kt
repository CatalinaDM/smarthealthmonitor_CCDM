package mx.utng.smarthealthmonitor_ccdm.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
@Database(
    entities = [LecturaFC::class],
    version  = 1,
    exportSchema = false  // true en producción para migraciones
)
abstract class SmartHealthDB : RoomDatabase() {
    abstract fun lecturaDao(): LecturaFCDao

    companion object {
        @Volatile
        private var INSTANCE: SmartHealthDB? = null

        fun getDatabase(context: Context): SmartHealthDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SmartHealthDB::class.java,
                    "smarthealthmonitor_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

