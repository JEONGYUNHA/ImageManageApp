
package com.example.imagemanageapp.ui.recommend

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.*
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.data.view.*
import kotlinx.android.synthetic.main.fragment_recommend_secondfragment.*


class RecommendFristFragment: Fragment() {
    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()
    private var cGrid: GridView? = grid
    private var img: ImageView? = null
    lateinit var model1: MyViewModel1
    var tList = ArrayList<String>()
    private var checkedImages = mutableListOf<String>()


    // 중복 띄우기 방지, 한 번 읽으면 true로 변경
    private var readSucess = false

    //fragment 시작하면 불리는 함수
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recommend_firstfragment, container, false)

        //viewModel 싱글톤으로 구현
        model1 = ViewModelProvider(activity as ViewModelStoreOwner)[MyViewModel1::class.java]

        img = root.findViewById((R.id.img))


        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)

        getTitleList()


        //옵저버 변화감지(확인버튼 누르면 action)
        model1.checkDone.observe(viewLifecycleOwner,Observer<Int> {
            //    if (checkedImages.contains("jpg")){
            Toast.makeText(this.activity, "선택한 사진을 삭제했어요!", Toast.LENGTH_SHORT).show()
            val titleLIst = checkedImages.toString()
            var titles = titleLIst!!.substring(1, titleLIst.length - 1).split(", ")
            for (t in titles) {
                //id나중에 수정
                val doc = String.format("%s-%s", "hankki1998", t)
                Log.d("docList", doc)

                db.collection("meta")
                    .document(doc)
                    .update("deleted", true)
                db.collection("auto")
                    .document(doc)
                    .update("deleted", true)
            }



        })



        return root
    }


    private fun getTitleList() {

        db.collection("remove")
            .whereEqualTo("similar", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("title").toString()
                    if (!readSucess) {
                        tList.add(name)
                        Log.d("!!!List", tList.toString())
                    }

                }
                if (!readSucess) {
                    read(tList.toString())
                }
                upload()

                readSucess = true

            }
            .addOnFailureListener {
            }


    }

    private fun read(tList: String) {
        val titleLIst = tList
        var titles = titleLIst!!.substring(1, titleLIst.length - 1).split(", ")
        Log.d("tttList", titles.toString())
        for (t in titles) {
            Log.d("t", t)
            db.collection("meta")
                .whereEqualTo("deleted", false)
                .whereEqualTo("title", t)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val token = document.get("token").toString()
                        Log.d("token", token.toString())
                        categoryImageData.add(CategoryImage(token, t))
                        Log.d("dataClass", categoryImageData.toString())
                    }
                    upload()

                }


                .addOnFailureListener { exception ->
                    Log.d("read failed:", "Error getting documents: ", exception)
                }
        }

    }

    private fun upload() {
        val color = Color.GRAY
        val mode = PorterDuff.Mode.SCREEN
        cGrid = grid
        val cAdapter = CategoryImageAdapter(this.activity, categoryImageData)
        cGrid!!.adapter = cAdapter
        cGrid!!.setOnItemClickListener { parent, view, position, id ->


            var deleteNum = model1.getNums()
            
            if (deleteNum == 1) {
                Log.d("Fragment num1", deleteNum.toString())
                checkItem(categoryImageData[position].title)
                changeColor(position,categoryImageData[position].title)
                //  cGrid!![position!!].img!!.setColorFilter(color, mode)

            } else if (deleteNum == 0) {
                Log.d("Fragment num2", deleteNum.toString())
                cGrid!![position!!].img!!.setColorFilter(null)

                var ctx = cAdapter.giveCtx()
                // 클릭 시 사진 확대
                val intent = Intent(ctx, RecommendSingleImageActivity::class.java)
                intent.putExtra("token", categoryImageData[position].token)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx!!.startActivity(intent)

            } else if (deleteNum == null) {
                Log.d("fragment num3", "null")
            }

        }


    }

    //gridView 누르면 색상변화
    private fun changeColor(position:Int?,title: String?){
        val color=Color.DKGRAY
        val mode = PorterDuff.Mode.DARKEN
        if(checkedImages.contains(title)) {
            cGrid!![position!!].img!!.setColorFilter(color,mode)
        }else{
            cGrid!![position!!].img!!.setColorFilter(null)
        }
    }


    //사진클릭시 리스트에 넘어감
    //다시 클릭시 선택취소
    private fun checkItem(title: String?){
        if(!checkedImages.contains(title)) {
            checkedImages.add(title!!)
        }else{
            checkedImages.remove(title!!)
        }
        Log.d("checkedImagesRemove", checkedImages.toString())

    }

}






