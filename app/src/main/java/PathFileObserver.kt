import android.content.Context
import android.os.FileObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.imagemanageapp.MainActivity
import java.io.File
import kotlin.coroutines.coroutineContext


class PathFileObserver(root: String) : FileObserver(root, mask) {
    /**
     * should be ends with "/"
     */
    var rootPath: String
    override fun onEvent(event: Int, path: String?) {
        when (event) {
            ALL_EVENTS ->
                Log.d(
                    TAG,
                    "ALL:$rootPath$path"
                )
            CREATE ->
                Log.d(
                        TAG,
                    "CREATE:$rootPath$path"
            )
            DELETE -> Log.d(
                TAG,
                "DELETE:$rootPath$path"
            )
            DELETE_SELF -> Log.d(
                TAG,
                "DELETE_SELF:$rootPath$path"
            )
            MODIFY -> Log.d(
                TAG,
                "MODIFY:$rootPath$path"
            )
            MOVED_FROM -> Log.d(
                TAG,
                "MOVED_FROM:$rootPath$path"
            )
            MOVED_TO -> Log.d(TAG, "MOVED_TO:$path")
            MOVE_SELF -> Log.d(TAG, "MOVE_SELF:$path")
            else -> {
            }
        }
    }



    fun close() {
        super.finalize()
    }

    companion object {
        const val TAG = "FILEOBSERVER"
        const val mask = ALL_EVENTS or
                CREATE or
                DELETE or
                DELETE_SELF or
                MODIFY or
                MOVED_FROM or
                MOVED_TO or
                MOVE_SELF
    }

    init {
        var root = root
        if (!root.endsWith(File.separator)) {
            root += File.separator
        }
        rootPath = root

    }
}