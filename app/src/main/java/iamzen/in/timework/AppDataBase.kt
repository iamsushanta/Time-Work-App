package iamzen.`in`.timework

/**
 * Author Name: Sushanta Das
 *
 * App DataBase only know [AppProvider] class
 */
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDataBase"
const val DATABASE_NAME = "TimeWork.db"
const val DATABASE_VERSION = 5

internal class AppDataBase private constructor(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null, DATABASE_VERSION
) {

    init {
        Log.d(TAG, "AppDataBase is Initialize")
    }

    override fun onCreate(db: SQLiteDatabase) {
        //CREATE TABLE Task(_id INTEGER PRIMARY KEY NOT NULL,Name TEXT NOT NULL,Description TEXT,ShortOrder INTEGER ); ̥

        Log.d(TAG,"onCreate Start ")
        val sSql = """CREATE TABLE ${TaskContract.TABLE_NAME}(
            ${TaskContract.Collum.TASK_ID} INTEGER PRIMARY KEY NOT NULL,
            ${TaskContract.Collum.TASK_NAME} TEXT NOT NULL,
            ${TaskContract.Collum.TASK_DESCRIPTION} TEXT,
            ${TaskContract.Collum.TASK_SHORT_ORDER} INTEGER);""".replaceIndent(" ")

        Log.d(TAG,sSql)

        db.execSQL(sSql)

        addTimingTable(db)
        addCurrentTimingView(db)
        addDuration(db)
        parametersView(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG,"OnUpgrade start")
        when(oldVersion){
            1 -> {
                 addTimingTable(db)
                addCurrentTimingView(db)
                addDuration(db)
                parametersView(db)

            }
            2 -> {
                addCurrentTimingView(db)
                addDuration(db)
                parametersView(db)
            }
            3 -> {
                addDuration(db)
                parametersView(db)
            }
            4 -> {
                parametersView(db)
            }
            else -> {
                Log.d(TAG,"Exception Unknown newVersion $newVersion")
            }
        }
    }

    private fun addTimingTable(db: SQLiteDatabase){
        // create table TimingTable
        val sqlTiming = """CREATE TABLE ${TimingContract.TABLE_NAME}(
            ${TimingContract.Collum.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TimingContract.Collum.TIMING_TASK_ID} INTEGER NOT NULL,
            ${TimingContract.Collum.TIMING_START_TIME} INTEGER,
            ${TimingContract.Collum.TIMING_TASK_DURATION} INTEGER);""".replaceIndent(" ")

        Log.d(TAG,"sqlTiming is $sqlTiming")
        db.execSQL(sqlTiming)

        val removeSql = """CREATE TRIGGER remove_task
            AFTER DELETE ON ${TaskContract.TABLE_NAME}
            FOR EACH ROW 
            BEGIN
            DELETE FROM ${TimingContract.TABLE_NAME}
            WHERE ${TimingContract.Collum.TIMING_TASK_ID} = OLD.${TaskContract.Collum.TASK_ID};
            END;
        """.trimMargin()

        Log.d(TAG,"removeSql is $removeSql")
        db.execSQL(removeSql)
    }

    private fun addCurrentTimingView(db: SQLiteDatabase) {
        /*
        CREATE VIEW vwCurrentTiming
             AS SELECT Timings._id,
                 Timings.TaskId,
                 Timings.StartTime,
                 Tasks.Name
             FROM Timings
             JOIN Tasks
             ON Timings.TaskId = Tasks._id
             WHERE Timings.Duration = 0
             ORDER BY Timings.StartTime DESC;
         */
        val sSQLTimingView = """CREATE VIEW ${CurrentTimingContract.TABLE_NAME}
        AS SELECT ${TimingContract.TABLE_NAME}.${TimingContract.Collum.ID},
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_ID},
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME},
            ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_NAME}
        FROM ${TimingContract.TABLE_NAME}
        JOIN ${TaskContract.TABLE_NAME}
        ON ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_ID} = ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_ID}
        WHERE ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_DURATION} = 0
        ORDER BY ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME} DESC;
    """.replaceIndent(" ")
        Log.d(TAG, sSQLTimingView)
        db.execSQL(sSQLTimingView)
    }

    private fun addDuration(db: SQLiteDatabase){

        /*
      CREATE VIEW vwTaskDurations AS
      SELECT Tasks.Name,
      Tasks.Description,
      Timings.StartTime,
      DATE(Timings.StartTime, 'unixepoch', 'localtime') AS StartDate,
      SUM(Timings.Duration) AS Duration
      FROM Tasks INNER JOIN Timings
      ON Tasks._id = Timings.TaskId
      GROUP BY Tasks._id, StartDate;
      */

        val sSQLDurationView = """ CREATE VIEW  ${DurationsContract.TABLE_NAME}
            AS SELECT ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_NAME},
            ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_DESCRIPTION},
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME},
            DATE (${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME},'unixepoch','localtime')
            AS ${DurationsContract.Collum.START_DATE},
            SUM(${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_DURATION})
            AS ${DurationsContract.Collum.DURATION}
            FROM ${TaskContract.TABLE_NAME} INNER JOIN ${TimingContract.TABLE_NAME}
            ON ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_ID}=
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_ID}
            GROUP BY ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_ID},
            ${DurationsContract.Collum.START_DATE}
             ;""".replaceIndent(" ")

        
        Log.d(TAG, sSQLDurationView)

        db.execSQL(sSQLDurationView)
    }

    private fun parametersView(db: SQLiteDatabase){
        var sSQL = """CREATE TABLE ${ParametersContract.TABLE_NAME}
        (${ParametersContract.Collum.ID} INTEGER PRIMARY KEY NOT NULL,
        ${ParametersContract.Collum.VALUE} INTEGER NOT NULL);""".trimMargin()
        Log.d(TAG,sSQL)

        db.execSQL(sSQL)
        sSQL = "DROP VIEW IF EXISTS ${DurationsContract.TABLE_NAME};"

        Log.d(TAG, sSQL)
        db.execSQL(sSQL)

        /**
        CREATE VIEW vwTaskDurations AS
        SELECT Tasks.Name,
        Tasks.Description,
        Timings.StartTime,
        DATE(Timings.StartTime, 'unixepoch') AS StartDate,
        SUM(Timings.Duration) AS Duration
        FROM Tasks INNER JOIN Timings
        ON Tasks._id = Timings.TaskId
        WHERE Timings.Duration > (SELECT Parameters.value FROM Parameters WHERE Parameters._id = 1)
        GROUP BY Tasks._id, StartDate;
         **/

        sSQL = """CREATE VIEW ${DurationsContract.TABLE_NAME}
            AS SELECT ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_NAME},
            ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_DESCRIPTION},
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME},
            DATE(${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_START_TIME}, 'unixepoch', 'localtime')
            AS ${DurationsContract.Collum.START_DATE},
            SUM(${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_DURATION})
            AS ${DurationsContract.Collum.DURATION}
            FROM ${TaskContract.TABLE_NAME} INNER JOIN ${TimingContract.TABLE_NAME}
            ON ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_ID} =
            ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_ID}
            WHERE ${TimingContract.TABLE_NAME}.${TimingContract.Collum.TIMING_TASK_DURATION} >
                (SELECT ${ParametersContract.TABLE_NAME}.${ParametersContract.Collum.VALUE}
                FROM ${ParametersContract.TABLE_NAME}
                WHERE ${ParametersContract.TABLE_NAME}.${ParametersContract.Collum.ID} = ${ParametersContract.ID_SHORT_TIMING})
            GROUP BY ${TaskContract.TABLE_NAME}.${TaskContract.Collum.TASK_ID}, ${DurationsContract.Collum.START_DATE}
            ;""".replaceIndent(" ")
        Log.d(TAG, sSQL)
        db.execSQL(sSQL)

        sSQL = """INSERT INTO ${ParametersContract.TABLE_NAME} VALUES (${ParametersContract.ID_SHORT_TIMING}, 0);"""
        Log.d(TAG, sSQL)
        db.execSQL(sSQL)

    }




    // This code SingletonHolder Class call easy way for understand
    companion object : SingletonHolder<AppDataBase,Context> (::AppDataBase)

}