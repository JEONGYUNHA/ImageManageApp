
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
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_showcategory_checkbox.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image.view.*


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
    private var img : ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_showcategory_image_list, container, false)


      //  dGrid = grid
     //   dAdapter = CategoryDeleteAdapter(this.activity,categoryImageData)



        val aAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        img = root.findViewById((R.id.img))

        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)
        read()

        root.selectBtn.setOnClickListener {
            btnAction()
            refresh()

        }

        return root
    }

    //gridview 새로고침
    private fun refresh(){
        val titleLIst1 = arguments?.getString("titleList")
        val titleList = Bundle()
        titleList.putString("titleList",titleLIst1)
        //fragment to fragment 전환
        val newFragment = ShowCategoryImageFragment()
        newFragment.arguments = titleList
        val transaction = parentFragmentManager.beginTransaction()
        transaction?.replace(R.id.nav_host_fragment,newFragment)
        transaction.commit()
    }

    //ok버튼 누르면 deteled=true로 update
    private fun btnAction(){
       // Log.d("cc",checkedImages.toString())
        val titleLIst = checkedImages.toString()
        var titles = titleLIst!!.substring(1,titleLIst.length-1).split(", ")


        for(t in titles){
            //id나중에 수정
            val doc = String.format("%s-%s","hankki1998",t)
            Log.d("docList",doc)

            db.collection("meta")
                .document(doc)
                .update("deleted",true)
        }
    }


    private fun read(){
        val titleLIst = arguments?.getString("titleList")
        var titles = titleLIst!!.substring(1,titleLIst.length-1).split(", ")
        Log.d("tList",titles.toString())
        for(t in titles){
            Log.d("t",t)
            db.collection("meta")
                .whereEqualTo("deleted",false)
                .whereEqualTo("title",t)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
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
                changeColor(position,categoryImageData[position].title)
               // Log.d("getPosition",position.toString())


            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun changeColor(position:Int?,title: String?){
        val color=Color.GRAY
        val mode = PorterDuff.Mode.SCREEN
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
        Log.d("checkdImagesRemove", checkedImages.toString())

    }

}




