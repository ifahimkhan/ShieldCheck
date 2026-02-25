package com.fahim.shieldcheck.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fahim.shieldcheck.data.local.db.converter.DateConverter
import com.fahim.shieldcheck.data.local.db.converter.ListConverter
import com.fahim.shieldcheck.data.local.db.dao.AppScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.DeviceScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.NetworkScanResultDao
import com.fahim.shieldcheck.data.local.db.entity.AppScanResultEntity
import com.fahim.shieldcheck.data.local.db.entity.DeviceScanResultEntity
import com.fahim.shieldcheck.data.local.db.entity.NetworkScanResultEntity
import android.util.Log
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        AppScanResultEntity::class,
        DeviceScanResultEntity::class,
        NetworkScanResultEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class ShieldCheckDatabase : RoomDatabase() {

    abstract fun appScanResultDao(): AppScanResultDao
    abstract fun deviceScanResultDao(): DeviceScanResultDao
    abstract fun networkScanResultDao(): NetworkScanResultDao

    companion object {
        private const val TAG = "ShieldCheckDatabase"
        private const val DATABASE_NAME = "shieldcheck_database"

        @Volatile
        private var INSTANCE: ShieldCheckDatabase? = null

        fun getInstance(context: Context, passphrase: ByteArray): ShieldCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, passphrase).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, passphrase: ByteArray): ShieldCheckDatabase {
            val factory = SupportFactory(passphrase)

            return try {
                createDatabase(context, factory).also {
                    // Verify the database can be opened
                    it.openHelper.readableDatabase
                }
            } catch (e: Exception) {
                Log.w(TAG, "Database could not be opened, deleting and recreating", e)
                context.applicationContext.deleteDatabase(DATABASE_NAME)
                createDatabase(context, factory)
            }
        }

        private fun createDatabase(context: Context, factory: SupportFactory): ShieldCheckDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ShieldCheckDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
