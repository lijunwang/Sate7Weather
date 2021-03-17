package com.wlj.sate7weather

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.wlj.sate7weather.fragments.NoticeListFragment

class NoticeActivity : AppCompatActivity() {
    private lateinit var noticeListFragment:NoticeListFragment
    private val noticeListTag = "noticeListFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        noticeListFragment = NoticeListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container,noticeListFragment,noticeListTag).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}