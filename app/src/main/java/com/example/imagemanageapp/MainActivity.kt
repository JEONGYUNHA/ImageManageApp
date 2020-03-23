package com.example.imagemanageapp

import PathFileObserver
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.FileObserver
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IntegerRes
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    val context: Context = this
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

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

    // 이미지 담을 List
    private val images = MutableLiveData<List<MediaStoreImage>>()

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

    // query문 돌려서 이미지 불러와 이미지 List에 저장
    private fun showImages() {
        GlobalScope.launch {
            val imageList = queryImages()
            images.postValue(imageList)
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
                MediaStore.Images.Media.DISPLAY_NAME
            )

            // ID로 정렬
            val sortOrder = "${MediaStore.Images.Media._ID} DESC"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, // selection
                null, // selectionArgs
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                // 저장소의 존재하는 모든 파일에 대해
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    // uri/id
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )

                    // 이미지 배열에 이미지 저장
                    val image = MediaStoreImage(id, displayName, contentUri)
                    images += image

                    // ******** Storage에 업로드 ********
                    // Cloud storage 인스턴스 생성
                    val storage = FirebaseStorage.getInstance()
                    // 인스턴스의 reference 생성
                    val storageRef = storage.reference

                    // ??
                    val mountainsRef = storageRef.child("images")
                    // Storage에 올릴 위치/파일이름
                    val mountainImagesRef = storageRef.child("images/" + displayName)

                    // ??
                    mountainsRef.name == mountainImagesRef.name // true
                    mountainsRef.path == mountainImagesRef.path // false

                    // MediaStore로 받아온 URI에 파일명 붙인 문자열 생성
                    val str = String.format(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString(), "/", displayName
                    )
                    // 생성한 문자열을 URI로 변환
                    val externalUri = Uri.parse(str)
                    // 생성한 URI를 유효한 Path로 변환
                    val path = getPathFromUri(externalUri)
                    // 최종 Path로 파일 불러옴
                    val file = Uri.fromFile(File(path))

                    // file 업로드
                    val uploadTask = mountainImagesRef.putFile(file)
                    uploadTask.addOnFailureListener {
                        // 업로드 실패 시
                        Log.d("upload result", "failed")
                    }.addOnSuccessListener {
                        // 업로드 성공 시
                        Log.d("upload result", displayName)
                    }
                }
            }
        }

        Log.d(TAG, "Found ${images.size} images")
        return images
    }

    // URI를 File Path로 바꿔주는 함수
    fun getPathFromUri(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null);
        cursor?.moveToNext();
        val path = cursor?.getString(cursor.getColumnIndex("_data"));

        return path;
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

