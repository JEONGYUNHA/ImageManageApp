package com.example.imagemanageapp


class SearchActivity : SearchFragmentActivity() {
    override fun createFragment() = MainFragment.newInstance()
}