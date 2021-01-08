    package com.example.cooldog

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private var index = 0
    private val mediaPlayer = MediaPlayer()
    private val homeDataList = ArrayList<Data>()
    /**
     * 数据
     */
    private fun initHomeData() {
        repeat(10)
        {
            homeDataList.add(Data("小尖尖", "韩红/薛之谦·天外来物", R.drawable.music_01, "music_01.mp3"))
            homeDataList.add(Data("彩卷", "薛之谦·天外来物", R.drawable.music_02, "music_02.mp3"))
            homeDataList.add(Data("绅士", "薛之谦2015年6月5日发行的个人EP", R.drawable.music_03, "music_03.mp3"))
            homeDataList.add(Data("天后", "薛之谦《蒙面唱将猜猜猜第二季》", R.drawable.music_04, "music_04.mp3"))
            homeDataList.add(Data("天外来物", "薛之谦·天外来物", R.drawable.music_05, "music_05.mp3"))
            homeDataList.add(Data("下雨了", "薛之谦·绅士《我是杜拉拉》电视剧插曲", R.drawable.music_06, "music_06.mp3"))
            homeDataList.add(Data("演员", "薛之谦·绅士", R.drawable.music_07, "music_07.mp3"))
            homeDataList.add(Data("野心", "薛之谦·天外来物 电影《缉魂》推广曲", R.drawable.music_08, "music_08.mp3"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * 沉浸式状态栏
         */
        val window = this.window
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        initHomeData()
        RecyclerViewTest.layoutManager = LinearLayoutManager(this)
        val adapter = NewsAdapter(homeDataList)
        RecyclerViewTest.adapter = adapter



        /**
         * 随机播放
         */
        randomBroadcast.setOnClickListener {
            val list = homeDataList[randomNumber()]
            set(list.musicImage, list.musicName, list.singer, list.musicUri)
        }
        /**
         * 顺序播放
         */
        orderPlay.setOnClickListener {
            val list = homeDataList[0]
            set(list.musicImage, list.musicName, list.singer, list.musicUri)
        }
        /**
         *         播放全部
         */
        playAll.setOnClickListener {
            val list = homeDataList[0]
            set(list.musicImage, list.musicName, list.singer, list.musicUri)
        }
        /**
         * 暂停播放按钮
         */
        musicStart.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                musicStart.setImageResource(R.drawable.ic_bo)
            } else {
                mediaPlayer.start()
                musicStart.setImageResource(R.drawable.ic_zan)
            }
        }
        /**
         * 上一曲
         */
        musicTop.setOnClickListener {
            //如果index小于0就返回homeDataList的最后一首歌得到坐标
            if (index <= 0) {
                index = homeDataList.size
            }
            index--
            val top = homeDataList[index]
            set(top.musicImage, top.musicName, top.singer, top.musicUri)
        }
        /**
         * 下一曲
         */
        musicButtom.setOnClickListener {
            nextMusic()
        }
    }

    /**
     * 适配器
     */
    inner class NewsAdapter(private val newsList: List<Data>) :
            RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.item_name)
            val singer: TextView = view.findViewById(R.id.item_singer)
            val indexs:TextView = view.findViewById(R.id.textIndex)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.itemView.setOnClickListener {
                /**
                 * 保存当前点击的数组坐标
                 */
                index = viewHolder.adapterPosition
                val list = newsList[index]
//              Toast.makeText(parent.context, "您点击了!!!" + list.musicName, Toast.LENGTH_SHORT).show()
                set(list.musicImage, list.musicName, list.singer, list.musicUri)
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = homeDataList[position]
            holder.name.text = data.musicName
            holder.singer.text = data.singer
            holder.indexs.text = (position+1).toString()
        }

        override fun getItemCount() = newsList.size
    }

    /**
     * 音乐图片转起来
     */
    private fun turn(musicTime:Int){
        val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = 10000
        rotateAnimation.repeatCount = musicTime/10000
        rotateAnimation.repeatMode = 1
        rotateAnimation.interpolator = LinearInterpolator()
        musicImage.animation = rotateAnimation
        rotateAnimation.start()
    }

    /**
     * 更改播放音乐的图片歌曲名字和歌手 调用播放音乐方法
     */
    private fun set(img: Int, name: String, singer: String, uri: String) {
        musicImage.setImageResource(img)
        muaiscName.text = name
        musicSinger.text = singer
        initPlayer(uri)

    }

    /**
     * 播放音乐方法 接收歌曲
     */
    private fun initPlayer(musicUri: String) {
        musicStart.setImageResource(R.drawable.ic_zan)
        mediaPlayer.reset()
        val fd = assets.openFd(musicUri)
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
        mediaPlayer.start()
        turn(mediaPlayer.duration)
    }

    /**
     * 停止并释放音乐资源
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    /**
     * 生成随机歌曲坐标
     */
    private fun randomNumber():Int{
        return (Math.random()*homeDataList.size).toInt()
    }

    /**
     * 下一曲
     */
    private fun nextMusic() {
        index++
        musicStart.setImageResource(R.drawable.ic_zan)
        //如果index大于homeDataList的长度就返回第一首歌的坐标
        if (index >= homeDataList.size) {
            index = 0
        }
        val button = homeDataList[index]
        set(button.musicImage, button.musicName, button.singer, button.musicUri)
    }

}
