package mx.utng.smarthealthmonitor_shared_ccdm.db

import androidx.room.*
import androidx.room.Entity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true)
    val id          : Int = 0,
    val bpm         : Int,
    val estado      : String,
    val dispositivo : String = "app", // wear | app | tv
    val hora        : String,
    @ColumnInfo(name = "sincronizado")
    val sincronizado: Boolean = false // false = pendiente de sync
)
