
package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.GridView
import android.widget.ListView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_showcategory_checkbox.*
import kotlinx.android.synthetic.main.fragment_showcategory_checkbox.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.view.*


data class CategoryImage(
    val token : String? = null,
    val title : String? = null
)

class ShowCategoryImageFragment: Fragment(){
    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()
    private val titleLIst2 = arguments?.getString("titleList")
 //   private var dGrid : GridView? = null
 //   private var dAdapter : CategoryDeleteAdapter? = null
    private var cGrid : GridView? = grid
    private var cAdapter : CategoryImageAdapter? = null

    private var checkedImages = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_showcategory_image_list, container, false)

      //  dGrid = grid
     //   dAdapter = CategoryDeleteAdapter(this.activity,categoryImageData)


        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)
        read()

        root.selectBtn.setOnClickListener {


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
    private fun checkedImage(){
      if(itemCheckBox.isChecked()){
          Log.d("ischecked",itemCheckBox.lineCount.toString())
      }else{
          Log.d("ischecked","null")
      }
    }


    private fun upload(){
        cGrid = grid
        cAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        cGrid!!.adapter = cAdapter
      /*  cGrid.setOnItemClickListener { parent, view, position, id ->
            checkedImage(categoryImageData[position].title)

        } */
    }

    override fun onCreateOptionsMenu(menu: Menu,inflater:MenuInflater) {
      inflater!!.inflate(R.menu.recommend_delete_toolbarmenu, menu)
        //main에 있는 action_settings 보이지 않게
        menu!!.findItem(R.id.action_settings).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)

    }
    var checknum = 0
    // 상단 오른쪽 점 세개 클릭
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.delete -> {
            cGrid!!.setOnItemClickListener { parent, view, position, id ->

                checkItem(categoryImageData[position].title)
              //  checkremove(categoryImageData[position].title)


            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun checkItem(title: String?){
      //  checknum++

   /*    if(checknum%2 == 1) {
            checkedImages.add(title!!)
           // val listcheck = checkedImages.distinct()
            Log.d("checkdImages",checkedImages.toString())
        }else{
            checkedImages.remove(title!!)
            Log.d("checkdImages",checkedImages.toString())
        }
*/

        checkedImages.add(title!!)
        Log.d("checkImage",checkedImages.toString())
     /*   Log.d("checkdImages",checkedImages.toString())
        while (checkedImages.listIterator().hasNext()){
            if(checkedImages.contains(title)){
                checkedImages.remove(title!!)
                Log.d("checkdImagesRemove",checkedImages.toString())
            }
        }
*/
    }

    private fun checkremove(title: String?){

       for(a in 0..checkedImages.size-1){
            if(checkedImages.get(a).equals(title)){
                val b=a+1
                for(b in 0..checkedImages.size-1) {
                    if (checkedImages.get(b).equals(title)) {
                        checkedImages.remove(title!!)
                        Log.d("checkdImagesRemove", checkedImages.toString())
                    }
                }
            }
        }

    }

}




