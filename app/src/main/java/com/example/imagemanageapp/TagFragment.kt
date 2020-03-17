package com.example.imagemanageapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

/**
 * A simple [Fragment] subclass.
 */
class TagFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Get a reference to the AutoCompleteTextView in the layout
        val v = inflater!!.inflate(R.layout.fragment_tag, container, false)
        val textView = v.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val countries: Array<out String> = resources.getStringArray(R.array.tag_array)
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, countries).also { adapter ->
            textView.setAdapter(adapter)
        }
        return v
    }
}
