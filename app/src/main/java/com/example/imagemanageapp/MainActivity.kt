package com.example.imagemanageapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    val context: Context = this
    private lateinit var auth: FirebaseAuth
    private lateinit var preTimeString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

        // 시간 저장
        saveTime()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_image, R.id.nav_album, R.id.nav_recommend,
                R.id.nav_mypage, R.id.nav_trash
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // FileObserver 부분, 혹시 몰라서 놥둠
        /*val fileObserver = PathFileObserver("/sdcard/DCIM/Screenshots/")
         fileObserver.startWatching()
        Toast.makeText(this, Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString(), Toast.LENGTH_SHORT).show()
        val fs = File.separator
        val path: String = (Environment.getExternalStorageDirectory().absolutePath + fs).toString()
        Log.d("test", "path is $path")
        val fileObserver: FileObserver = object : FileObserver(path, ALL_EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                Log.d("test", "event dectect $event $path")
            }
        }
        fileObserver.startWatching()*/

        openMediaStore()
    }

    // 상단 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // 상단 메뉴 중 검색 버튼 선택 시
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        if (id == R.id.action_search) {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    // 왼쪽 카테고리 메뉴
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // ******** 여기서부터 MediaStore로 저장소 접근해서 이미지 업로드 하는 부분 ********
    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
        const val TAG = "MainActivity"
    }


    // Permission 검사?
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                } else {
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )

                    if (!showRationale) {
                        goToSettings()
                    }
                }
                return
            }
        }
    }

    // 이미지 담을 List
    private val images = MutableLiveData<List<MediaStoreImage>>()

    // query문 돌려서 이미지 불러와 이미지 List에 저장
    private fun showImages() {
        GlobalScope.launch {
            val imageList = queryImages()
            images.postValue(imageList)
            var size = imageList.size
            var i = 0
            Log.d("size", size.toString())
            if (size != null) {
                for (i in 0..size - 1) {
                    uploadToStorage(imageList[i])
                }
            }
        }
    }

    private fun openMediaStore() {
        //  Permission이 있으면 query문 돌림
        if (haveStoragePermission()) {
            showImages()
        } else {    // 없으면 Permission 요청
            requestPermission()
        }
    }

    private fun goToSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    // Permission 있는지 확인
    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    // Permission 요청
    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    // query문 만들어서 돌리기, 저장소의 이미지 모두 불러옴
    private suspend fun queryImages(): List<MediaStoreImage> {
        val images = mutableListOf<MediaStoreImage>()

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
            )

            // ID로 정렬
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} ASC"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, // selection
                null, // selectionArgs
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val titleColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val pathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val dateColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val latitudeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE)
                val longitudeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE)
                Log.d("pre", preTimeString)
                // 저장소의 존재하는 모든 파일에 대해
                while (cursor.moveToNext()) {
                    if (preTimeString.toLong() < cursor.getLong(dateColumn)) {
                        val id = cursor.getInt(idColumn)
                        val title = cursor.getString(titleColumn)
                        val path = cursor.getString(pathColumn)
                        val date = cursor.getLong(dateColumn)
                        val latitude = cursor.getDouble(latitudeColumn)
                        val longitude = cursor.getDouble(longitudeColumn)

                        // 이미지 배열에 이미지 저장
                        val image = MediaStoreImage(id, title, path, date, latitude, longitude)
                        images += image
                        Log.d("pre", preTimeString)
                        Log.d("date", date.toString())
                        Log.d("date-pre", (date - preTimeString.toLong()).toString())
                        Log.d("meta", image.toString())
                    }
                }
            }
        }
        return images
    }

    // Storage에 사진 업로드
    fun uploadToStorage(img: MediaStoreImage) {
        // Cloud storage 인스턴스 생성
        val storage = FirebaseStorage.getInstance()
        // 인스턴스의 reference 생성
        val storageRef = storage.reference

        // ??
        val mountainsRef = storageRef.child("images")
        // Storage에 올릴 위치/파일이름
        val mountainImagesRef = storageRef.child("images/" + img.title)

        // ??
        mountainsRef.name == mountainImagesRef.name // true
        mountainsRef.path == mountainImagesRef.path // false

        // 최종 Path로 파일 불러옴
        val file = Uri.fromFile(File(img.path))
        // file 업로드
        val uploadTask = mountainImagesRef.putFile(file)
        uploadTask.addOnFailureListener {
            // 업로드 실패 시
            Log.d("Storage upload result", "failed")
        }.addOnSuccessListener {
            // 업로드 성공 시
            Log.d("Storage upload result", img.path)
        }
    }

    fun uploadToDB(img : MediaStoreImage, token : String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        // 구글 로그인한 id 받아오기
        val pref = this.getSharedPreferences("id", Context.MODE_PRIVATE)
        val email = pref.getString("id", "")!!
        var id : String? = null
        Log.d("email", email)

        db.collection("meta")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    id = document.id.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        // long형 Date형으로 바꾸기
        val date = Date(img.date)
        val dateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        val dateStr = dateFormat.format(date);

        val meta = Meta(id, img.title, img.path, dateStr, img.latitude, img.longitude, token)
        val docTitle = String.format("%s-%s", id, img.title)
        //db.collection("meta").document(docTitle).set(meta)
        db.collection("meta")
            .document(docTitle)
            .set(meta)
            .addOnSuccessListener { documentReference ->
                Log.d("DB upload result", "DocumentSnapshot written with ID: ${docTitle}")
            }
            .addOnFailureListener { e ->
                Log.w("DB upload result", "Error adding document", e)
            }

    }

    // 앱 켜질 때 시간 저장하는 함수
    fun saveTime() {
        // 현재 시간
        val nowTime = Calendar.getInstance().time.time
        val nowTimeString = nowTime.toString()
        Log.d("nowTime", nowTimeString)

        val pref = this.getSharedPreferences("currentTime", Context.MODE_PRIVATE)
        val editor = pref.edit()

        // 현재 시간을 pre에 저장, 처음에는 0 저장
        preTimeString = pref.getString("currentTime", Date(-1).time.toString())!!
        Log.d("preTime", preTimeString)

        editor.putString("currentTime", nowTimeString)
        editor.apply()
        Log.d("changeTime", pref.getString("currentTime", ""))
    }

    override fun onDestroy() {
        super.onDestroy()

        // 로그아웃
        /*AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }*/

    }
}

