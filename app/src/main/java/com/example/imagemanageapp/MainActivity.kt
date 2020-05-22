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
import android.widget.Switch
import android.widget.Toast
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

// openCV에서 흔들림 정도를 판단할 임계값
// 이 임계값보다 값이 작으면 흔들린 것, 크면 안 흔들린 것
const val SHAKEN_THRESHOLD: Double = 350.0

// 유사사진 판단할 초(10초)
const val SIMILAR_TIME: Int = 10000

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    val context: Context = this
    private lateinit var auth: FirebaseAuth
    private lateinit var preTimeString: String

    // Cloud storage 인스턴스 생성
    val storage = FirebaseStorage.getInstance()
    // 인스턴스의 reference 생성
    val storageRef = storage.reference

    // Firestore 인스턴스 생성
    val db = FirebaseFirestore.getInstance()

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

        openMediaStore()

        // HeaderView 접근하여 프로필 변경
        val headerView = navView.getHeaderView(0)
        val pref = this.getSharedPreferences("id", Context.MODE_PRIVATE)
        headerView.idField.text = pref.getString("id", "User")
        headerView.emailField.text = pref.getString("email", "Email")
    }

    // 상단 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        //자동 삭제 item에서 빼오기
        val item: MenuItem = menu.findItem(R.id.app_bar_switch)
        val deleteSwitch: Switch = item.actionView.findViewById<Switch>(R.id.switch1)

        deleteSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(this, "Switch On", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Switch Off", Toast.LENGTH_SHORT).show()
            }
        }

            return true
    }


    // 상단 메뉴 중 검색 선택 시
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
    private val images = MutableLiveData<List<Meta>>()

    // query문 돌려서 이미지 불러와 이미지 List에 저장
    private fun showImages() {
        GlobalScope.launch {
            val imageList = queryImages()
            images.postValue(imageList)
            var size = imageList.size
            var i = 0
            Log.d("size", size.toString())
            if (size != 0) {
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
    private suspend fun queryImages(): List<Meta> {
        val images = mutableListOf<Meta>()

        // 구글 로그인한 id 받아오기
        val pref = this.getSharedPreferences("id", Context.MODE_PRIVATE)
        val id = pref.getString("id", "")!!


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

                        val title = cursor.getString(titleColumn)
                        val path = cursor.getString(pathColumn)
                        val date = cursor.getLong(dateColumn)
                        val latitude = cursor.getDouble(latitudeColumn)
                        val longitude = cursor.getDouble(longitudeColumn)
                        val token = ""

                        // 이미지 배열에 이미지 저장
                        val image = Meta(id, title, path, date, latitude, longitude, token, false)
                        images += image
                        Log.d("pre", preTimeString)
                        Log.d("date", date.toString())
                        Log.d("meta", image.toString())
                    }
                }
            }
        }
        return images
    }

    // Storage에 사진 업로드
    fun uploadToStorage(img: Meta) {
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
            mountainImagesRef.downloadUrl.addOnCompleteListener {
                if (it.isComplete) {
                    Log.d("token", it.toString())
                    img.token = it.result.toString()
                    uploadToDB(img)
                }
            }
        }
    }

    fun uploadToDB(img: Meta) {
        val docTitle = String.format("%s-%s", img.id, img.title)

        // meta DB에 업로드
        db.collection("meta")
            .document(docTitle)
            .set(img)
            .addOnSuccessListener { documentReference ->
                Log.d("DB upload result", "DocumentSnapshot written with ID: ${docTitle}")
            }
            .addOnFailureListener { e ->
                Log.w("DB upload result", "Error adding document", e)
            }

        // auto DB에 업로드
        val auto = Auto(
            img.id,
            img.title,
            person = false,
            animal = false,
            traffic = false,
            furniture = false,
            book = false,
            bag = false,
            sport = false,
            device = false,
            plant = false,
            food = false,
            things = false
        )
        db.collection("auto")
            .document(docTitle)
            .set(auto)
            .addOnSuccessListener { documentReference ->
                Log.d("DB auto upload", "DocumentSnapshot written with ID: ${docTitle}")
                // 스크린샷이 아닌 사진들에 대해서만 태그 체크
                checkAuto(img)
            }
            .addOnFailureListener { e ->
                Log.w("DB auto upload", "Error adding document", e)
            }

        // remove DB에 업로드
        val remove = Remove(
            img.id,
            img.title,
            similar = false,
            shaken = false,
            darked = false,
            screenshot = false
        )
        db.collection("remove")
            .document(docTitle)
            .set(remove)
            .addOnSuccessListener { documentReference ->
                Log.d("DB remove upload", "DocumentSnapshot written with ID: ${docTitle}")
                // 색 추출 후 저장
                saveColor(img)
                // 스크린샷이 아닌 사진들에 대해서만 태그 체크
                if (!checkScreenshot(img)) {
                    checkShaken(img)
                    checkDarkImage(img)
                    checkSimilar(img)
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB remove upload", "Error adding document", e)
            }

    }

    // 스크린샷 체크하는 함수
    fun checkScreenshot(img: Meta): Boolean {
        val imgTitle = img.title
        val isScreenshot = imgTitle!!.contains("Screenshot", true)
        if (isScreenshot) {
            val docTitle = String.format("%s-%s", img.id, img.title)

            db.collection("remove")
                .document(docTitle)
                .update("screenshot", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("DB Screenshot upload", "DocumentSnapshot written with ID: ${docTitle}")
                }
                .addOnFailureListener { e ->
                    Log.w("DB Screenshot upload", "Error adding document", e)
                }
            return true
        }
        return false
    }

    // 흔들린 사진 체크하는 함수
    fun checkShaken(img: Meta) {
        val opencv: OpenCV = OpenCV(this)
        val fm: Double = opencv.isShaken(img.path)
        Log.d("fm", String.format("%s - %.5f", img.path, fm))
        if (fm < SHAKEN_THRESHOLD) {
            val docTitle = String.format("%s-%s", img.id, img.title)

            db.collection("remove")
                .document(docTitle)
                .update("shaken", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("DB Shaken upload", "DocumentSnapshot written with ID: ${docTitle}")
                }
                .addOnFailureListener { e ->
                    Log.w("DB Shaken upload", "Error adding document", e)
                }
        }
    }

    // 어두운 사진 체크하는 함수
    fun checkDarkImage(img: Meta) {
        val isDark: DarkImage = DarkImage(this)
        val final: String = isDark.checkDarkImg(img.path)
        val docTitle = String.format("%s-%s", img.id, img.title)

        if (final.equals("dark")) {
            db.collection("remove")
                .document(docTitle)
                .update("darked", true)
                .addOnSuccessListener { documentReference ->
                    Log.d("DB darked upload", "DocumentSnapshot written with ID: ${docTitle}")
                }
                .addOnFailureListener { e ->
                    Log.w("DB darked upload", "Error adding document", e)
                }
        }

    }

    //auto 태그 체크해주는 함수
    fun checkAuto(img: Meta) {
        var highPercent: Array<String> = emptyArray()
        val docTitle = String.format("%s-%s", img.id, img.title)

        var person: Array<String> = arrayOf("person", "tie")
        var animal: Array<String> = arrayOf(
            "bird",
            "cat",
            "dog",
            "horse",
            "sheep",
            "cow",
            "elephant",
            "bear",
            "zebra",
            "giraffe",
            "teddy bear"
        )
        var traffic: Array<String> = arrayOf(
            "bicycle",
            "car",
            "motorcycle",
            "airplane",
            "bus",
            "train",
            "truck",
            "boat",
            "traffic light",
            "stop sign",
            "parking meter",
            "fire hydrant"
        )
        var furniture: Array<String> = arrayOf(
            "chair",
            "couch",
            "bed",
            "refrigerator",
            "sink",
            "toilet",
            "dining table",
            "clock"
        )
        var book: Array<String> = arrayOf("book")
        var bag: Array<String> = arrayOf("handbag", "backpack", "suitcase")
        var sport: Array<String> = arrayOf(
            "frisbee",
            "skis",
            "snowboard",
            "sports ball",
            "kite",
            "baseball bat",
            "baseball glove",
            "skateboard",
            "surfboard",
            "tennis racket"
        )
        var food: Array<String> = arrayOf(
            "wine glass",
            "cup",
            "fork",
            "knife",
            "spoon",
            "bowl",
            "banana",
            "apple",
            "sandwich",
            "orange",
            "broccoli",
            "carrot",
            "hot dog",
            "pizza",
            "donut",
            "cake",
            "bottle"
        )
        var device: Array<String> = arrayOf(
            "hair drier",
            "toaster",
            "oven",
            "microwave",
            "cell phone",
            "keyboard",
            "remote",
            "mouse",
            "laptop",
            "tv",
            "refrigerator"
        )
        var plant: Array<String> = arrayOf("potted plant", "vase", "bench")
        var things: Array<String> = arrayOf("umbrella", "scissors", "toothbrush")

        var isAuto: AutoImage = AutoImage(this)
        var final: Array<Array<String>> = isAuto.checkAutoImg(img.path)


        if(final[0][1].toDouble()>= 0.5&&final[1][1].toDouble()>= 0.5){
            highPercent = arrayOf(final[0][0], final[1][0])
            Log.d("highPercent", highPercent[0])
            Log.d("highPercent", highPercent[1])
        }else if(final[0][1].toDouble()>= 0.5 &&final[1][1].toDouble() < 0.5){
            highPercent = arrayOf(final[0][0])
            Log.d("highPercent", highPercent[0])
        }else if(final[0][1].toDouble() < 0.5 &&final[1][1].toDouble() >= 0.5){
            highPercent = arrayOf(final[1][0])
            Log.d("highPercent", highPercent[0])
        }

        var doc = db.collection("auto").document(docTitle)
        for (highPercentIndex in highPercent) {
            for (p in person) {
                if (highPercentIndex == p) {
                    doc.update("person", true)
                }
            }
            for (a in animal) {
                if (highPercentIndex == a) {
                    doc.update("animal", true)
                }
            }
            for (t in traffic) {
                if (highPercentIndex == t) {
                    doc.update("traffic", true)
                }
            }
            for (f in furniture) {
                if (highPercentIndex == f) {
                    doc.update("furniture", true)
                }
            }
            for (b in book) {
                if (highPercentIndex == b) {
                    doc.update("book", true)
                }
            }
            for (b in bag) {
                if (highPercentIndex == b) {
                    doc.update("bag", true)
                }
            }
            for (s in sport) {
                if (highPercentIndex == s) {
                    doc.update("sport", true)
                }
            }
            for (f in food) {
                if (highPercentIndex == f) {
                    doc.update("food", true)
                }
            }
            for (d in device) {
                if (highPercentIndex == d) {
                    doc.update("device", true)
                }
            }
            for (p in plant) {
                if (highPercentIndex == p) {
                    doc.update("plant", true)
                }
            }
            for (t in things) {
                if (highPercentIndex == t) {
                    doc.update("things", true)
                }
            }
        }
    }

    // Color 3가지 추출하여 저장
    fun saveColor(img: Meta) {
        val opencv: OpenCV = OpenCV(this)
        val rgbList = opencv.color(img.path)
        val docTitle = String.format("%s-%s", img.id, img.title)

        var collection = db.collection("color").document(docTitle).collection("colors")

        // color 3가지 저장
        for (i in 0..2) {
            var color: Color = Color(
                android.graphics.Color.rgb(rgbList[i].r, rgbList[i].g, rgbList[i].b),
                rgbList[i].r,
                rgbList[i].g,
                rgbList[i].b
            )
            collection.document(String.format("%s%d", "color", i + 1)).set(color)
        }
    }

    // 유사 사진 체크하는 함수
    fun checkSimilar(img: Meta) {
        compareTime(img)
    }

    // 유사사진 - 시간
    fun compareTime(img: Meta) {
        // 시간 비교
        db.collection("meta")
            // 현재 사진 10초 전 ~ 현재 사진 ~ 현재 사진 10초 후 시간인게 하나라도 있으면 위치 비교 실행
            .whereGreaterThan("date", img.date - SIMILAR_TIME)
            .whereLessThan("date", img.date + SIMILAR_TIME)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("compare", "time")
                var resultDocuments = documents.documents
                resultDocuments.clear()
                for(d in documents) {
                    if(!(d.get("title").toString().equals(img.title)))
                        resultDocuments.add(d)
                }
                if (resultDocuments.size != 0)
                    compareLocation(img, resultDocuments)
            }

    }

    // 유사사진 - 위치
    fun compareLocation(img: Meta, resultDocuments: List<DocumentSnapshot>) {
        Log.d("compare", "location")
        // 위경도 비교
        // 현재 이미지의 위경도를 넘겨받은 문서들의 위경도와 비교하여 하나라도 있으면 색 비교 실행
        for (d in resultDocuments) {
            if (d.get("latitude").toString().toDouble() == img.latitude &&
                d.get("longitude").toString().toDouble() == img.longitude
            ) {
                compareColor(img, resultDocuments)
                break;
            }
        }
    }

    // 유사사진 - 색
    fun compareColor(img: Meta, resultDocuments: List<DocumentSnapshot>) {
        Log.d("compare", "color")
        var docTitle = String.format("%s-%s", img.id, img.title)
        var colors = arrayListOf<Color>()
        var docTitle2: String
        var compareColors = arrayListOf<Color>()

        // 현재 이미지를 intColor가 큰 순으로 정렬하여 colors에 저장함
        db.collection("color").document(docTitle).collection("colors")
            .orderBy("intColor").get().addOnSuccessListener { documents ->
                for (d in documents) {  // 3번 실행
                    var color = Color(
                        d.get("intColor").toString().toInt(),
                        d.get("r").toString().toInt(),
                        d.get("g").toString().toInt(),
                        d.get("b").toString().toInt()
                    )
                    colors.add(color)
                }
                // 넘겨받은 문서 값 받아와서 비교
                for (d in resultDocuments) {
                    docTitle2 = String.format("%s-%s", d.get("id").toString(), d.get("title").toString())
                    db.collection("color").document(docTitle2).collection("colors")
                        .orderBy("intColor").get().addOnSuccessListener { documents ->
                            for (d in documents) {  // 3번 실행
                                var color = Color(
                                    d.get("intColor").toString().toInt(),
                                    d.get("r").toString().toInt(),
                                    d.get("g").toString().toInt(),
                                    d.get("b").toString().toInt()
                                )
                                compareColors.add(color)
                            }
                            compareRGB(docTitle, colors, compareColors)
                            compareColors.clear()
                        }
                }

            }


    }
    // RGB 각각 범위 비교 (오차범위 : +- 20)
    fun isInRange(n1: Int, n2: Int) = n2 in n1 - 20..n1 + 20
    fun compareRGB(docTitle : String, colors: ArrayList<Color>, compareColors: ArrayList<Color>)  : Boolean{
        if (isInRange(colors[0].r, compareColors[0].r) && isInRange(colors[0].g, compareColors[0].g) && isInRange(colors[0].g, compareColors[0].g)) {
            if (isInRange(colors[1].r, compareColors[1].r) && isInRange(colors[1].g, compareColors[1].g) && isInRange(colors[1].g, compareColors[1].g)) {
                if (isInRange(colors[2].r, compareColors[2].r) && isInRange(colors[2].g, compareColors[2].g) && isInRange(colors[2].g, compareColors[2].g)) {
                    db.collection("remove").document(docTitle)
                        .update("similar", true)
                    return true
                }
            }
        }
        return false

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