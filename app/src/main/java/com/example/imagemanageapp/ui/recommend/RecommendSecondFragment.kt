
package com.example.imagemanageapp.ui.recommend

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.*
import android.widget.GridView
import android.widget.ImageView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recommend_secondfragment.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.grid


class RecommendSecondFragment: Fragment() {
    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()
    private var cGrid: GridView? = grid
    private var img: ImageView? = null
    lateinit var model: MyViewModel
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
        val root = inflater.inflate(R.layout.fragment_recommend_secondfragment, container, false)

        //viewModel 싱글톤으로 구현
        model = ViewModelProvider(activity as ViewModelStoreOwner)[MyViewModel::class.java]

        img = root.findViewById((R.id.img))


        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)

        getTitleList()

        model.checkDone.observe(activity as LifecycleOwner, Observer<Int> {
            // it로 넘어오는 param은 LiveData의 value
            //text_test.text = it
            Log.d("observe update","")

        })




        return root
    }
/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val checkObserver = Observer<Int>{ newCheck ->
            Log.d("observe update","")
        }
        model.checkDone.observe(activity as LifecycleOwner, Observer<Int> {
            // it로 넘어오는 param은 LiveData의 value
            //text_test.text = it
            Log.d("observe update","")
        })

      //  model.checkDone.observe(activity as LifecycleOwner,checkObserver)

    }  */
/*
    override fun onStart() {
        super.onStart()

/*
        model.checkDone.observe(this, Observer {
            Log.d("observer Done","")
        })
*/
    }
*/

    /* override fun onResume() {
        super.onResume()
        var num = arguments?.getInt("num")
        Log.d("fragment num4",num.toString())
    }  */


    private fun getTitleList() {

        db.collection("remove")
            .whereEqualTo("shaken", true)
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


            var deleteNum = model.getNums()
            if (deleteNum == 1) {
                Log.d("Fragment num1", deleteNum.toString())
                checkItem(categoryImageData[position].title)
                changeColor(position,categoryImageData[position].title)
              //  cGrid!![position!!].img!!.setColorFilter(color, mode)

            } else if (deleteNum == 0) {
                Log.d("Fragment num2", deleteNum.toString())
                cGrid!![position!!].img!!.setColorFilter(null)
            } else if (deleteNum == null) {
                Log.d("fragment num3", "null")
            }

            // Log.d("num11",num.toString)

            /*
            if(arguments?.getString("num") == null){
                Log.d("num",null)
                cGrid!![position!!].img!!.setColorFilter(null)

            }else if(arguments?.getString("num")!!.toInt() == 1){
                Log.d("num",arguments?.getString("num"))
                cGrid!![position!!].img!!.setColorFilter(color,mode)
            }else if(arguments?.getString("num")!!.toInt() == 0){
                Log.d("num",arguments?.getString("num"))
                cGrid!![position!!].img!!.setColorFilter(null)
            }  */
        }


    }

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






