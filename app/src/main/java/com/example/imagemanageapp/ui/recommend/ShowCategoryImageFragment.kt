
package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_recommend.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.view.*


data class CategoryImage(
    val token : String? = null,
    val title : String? = null
)

class ShowCategoryImageFragment: Fragment(){
    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_showcategory_image_list, container, false)
        val aAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        val check: CheckBox = root.findViewById((R.id.itemCheckBox))
        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)
        read()


        root.selectBtn.setOnClickListener {
            checkBox(check)
            read()

        }

        return root
    }

    private fun read(){
        val titleLIst = arguments?.getString("titleList")
        var titles = titleLIst!!.substring(1,titleLIst.length-1).split(", ")
        Log.d("tList",titles.toString())
        for(t in titles){
            Log.d("t",t)
            db.collection("meta")
                .whereEqualTo("title",t)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        Log.d("two","two")
                        val token = document.get("token").toString()
                        Log.d("token",token.toString())
                        categoryImageData.add(CategoryImage(token,t))
                        Log.d("dataClass",categoryImageData.toString())
                    }
                    upload()

                }


                .addOnFailureListener { exception ->
                    Log.d("read failed:", "Error getting documents: ", exception)
                }
        }

    }

    //checkBox가 선택되어있다면 사진 지우기
    private fun checkBox(check:CheckBox){
      if(check.isChecked()){
          //사진 지우는 코드
          //선택된 그리드뷰의 position받아서 title검색해서 해당 db지우기


      }
    }


    private fun upload(){
        val cGrid = grid
        val cAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        cGrid.adapter = cAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu,inflater:MenuInflater) {
      inflater!!.inflate(R.menu.recommend_delete_toolbarmenu, menu)
        //main에 있는 action_settings 보이지 않게
        menu!!.findItem(R.id.action_settings).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    // 상단 오른쪽 점 세개 클릭
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.delete -> {
                val cGrid = grid
                val cAdapter = CategoryDeleteAdapter(this.activity,categoryImageData)
                cGrid.adapter = cAdapter

            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}



