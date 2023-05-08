package com.appg.mungnyang.oguogu.contentresolver

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.Attendees.query
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentResolverCompat.query
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appg.mungnyang.oguogu.contentresolver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG = "로그"
    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val REQ_READ = 99

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(isPermitted()){
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQ_READ)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startProcess() {
        val adapter = MusicAdapter()
        adapter.musicList.addAll(getMusicList())

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMusicList(): List<Music> {
        // 컨텐트 리졸버로 음원 목록 가져오기
        // 1.데이터 테이블 주소
        val musicListUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        // 2.가져올 데이터 컬럼 정의
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        // 3. 컨텐트 리졸버에 해당 데이터 요청

        val cursor = contentResolver.query(musicListUri, proj, null, null, null)

        // 4. 커서로 전달받은 데이터를 꺼내서 저장
        val musicList = mutableListOf<Music>()
        while(cursor?.moveToNext() ?: false){
            val id = cursor!!.getString(0)
            val title = cursor!!.getString(1)
            val artist = cursor!!.getString(2)
            val albumId = cursor!!.getString(3)
            val duration = cursor!!.getLong(4)

            val music = Music(id, title, artist, albumId, duration)
            musicList.add(music)

        }
        return musicList
    }

    fun isPermitted() : Boolean{  //책에는 checkPermission
        Log.d(TAG, "isPermitted 호출")
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQ_READ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startProcess()
            }else{
                Toast.makeText(this, "권한 요청 승인해야지만 앱을 실행할 수 있습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }


    }
}