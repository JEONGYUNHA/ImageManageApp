package com.example.imagemanageapp.ui.album

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_album.*


class AlbumListAdapter : BaseAdapter {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val ctx: Context?
    private val transaction: FragmentTransaction?
    private val krTags: ArrayList<String>
    private val enTags: ArrayList<String>
    private var mView : View? = null
    private var mGrid : GridView? = null

    constructor(_ctx: Context?, _transaction: FragmentTransaction?, _krTags: ArrayList<String>, _enTags: ArrayList<String>) {
        ctx = _ctx
        transaction = _transaction
        krTags = _krTags
        enTags = _enTags
    }

    override fun getCount(): Int {
        return krTags.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        mView = convertView
        val inflater = LayoutInflater.from(ctx)
        mView = inflater.inflate(R.layout.album_row, parent, false)

        mGrid = mView!!.findViewById(R.id.gridView)
        val mText: TextView = mView!!.findViewById(R.id.textView)

        mText.text = krTags[position]
        readImages(krTags[position], enTags[position])

        val fragment = AlbumImageFragment()
        val bundle = Bundle(2)
        bundle.putString("krTag", krTags[position])
        bundle.putString("enTag", enTags[position])
        fragment.arguments = bundle

        mView!!.findViewById<Button>(R.id.moreBtn).setOnClickListener {
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

    return mView!!
    }

    private fun readImages(krTag : String, enTag : String) {
        var tokens = arrayListOf<String>()
        db.collection("auto")
            .whereEqualTo(enTag, true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    db.collection("meta").document(docTitle).get().addOnSuccessListener {
                        val token = it.get("token").toString()
                        tokens.add(token)
                        Log.d("albumToken", tokens.toString())
                        if(tokens.size == 5)
                            setAdapter(tokens)
                    }
                }
            }
    }
    private fun setAdapter(tokens : ArrayList<String>) {
        val mAdapter = AlbumGridAdapter(ctx, transaction, tokens)
        /*mGrid!!.numColumns = tokens.size
        Log.d("numColumns", mGrid!!.numColumns.toString())*/
        mGrid!!.adapter = mAdapter
    }
}
