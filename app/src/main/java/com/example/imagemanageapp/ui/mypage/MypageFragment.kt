package com.example.imagemanageapp.ui.mypage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.MainActivity
import com.example.imagemanageapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MypageFragment : Fragment() {

    private lateinit var mypageViewModel: MypageViewModel
    private val db = FirebaseFirestore.getInstance()
    lateinit var storage: FirebaseStorage
    private var userEmail: String = ""
    private var ImageTitles = mutableListOf<String>()
    private var allSize:Long = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storage = FirebaseStorage.getInstance()
        val root = inflater.inflate(R.layout.fragment_mypage, container, false)


        readUserEmail()
        imageList()









        return root
    }

    override fun onStart() {
        super.onStart()
       // val navView: NavigationView = nav_view



        changeImageBtn.setOnClickListener {
            Log.d("BtnClicked","ok")


            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            startActivityForResult(intent, 1)
        }



    }

    //앨범에서 사진선택했을 때 프로필바꾸기
     override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {

            val selectedImageUri: Uri? = data.data
            val headerView = (activity as MainActivity).headerView!!

            headerView.imageView.setImageURI(selectedImageUri)
            headerView.imageView.setBackground(ShapeDrawable(OvalShape()))
            headerView.imageView.setClipToOutline(true)
            headerView.imageView.cropToPadding = true
           // changeImageBtn.setImageURI(selectedImageUri)

        }
    }



    //사용자 아이디 읽어오기
    private fun readUserEmail() {
        db.collection("user")
            .whereEqualTo("id", "hankki")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userEmail = document.get("email").toString()
                }
                user_email.text = userEmail//.toString()
                Log.d("userEmail", userEmail)
            }
            .addOnFailureListener {
            }
    }

    //삭제되지않은 모든 이미지 가져오기
    private fun imageList(){
        db.collection("auto")
            .whereEqualTo("deleted", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var title = document.get("title").toString()
                    ImageTitles.add(title)
                }
                user_email.text = userEmail//.toString()
                Log.d("ImageTitles",ImageTitles.toString())
                allAmount()
            }
            .addOnFailureListener {
            }
    }

    //총 사용용량
    private fun allAmount() {
        var tList = ImageTitles.toString()
        var titles = tList!!.substring(1, tList.length - 1).split(", ")
        Log.d("Imagetitles", tList)
        Log.d("tttList", titles.toString())
        for (t in titles) {
            val storageRef = storage.reference
            val forestRef = storageRef.child("images/"+t)

            forestRef.metadata.addOnSuccessListener {
                var size = it.sizeBytes
                allSize += size
                Log.d("size",size.toString())
                Log.d("metadata", allSize.toString())

                var megaByte = allSize/1000000
                user_amount.text = " "+(megaByte.toString())+"MB"
            }.addOnFailureListener {
                Log.d("metadata", "failed")

            }
        }
        Log.d("allSize",allSize.toString())

    }


}