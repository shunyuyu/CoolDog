package com.example.cooldog

import android.media.MediaPlayer
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 首页数据实体类
 * musicName：音乐名字
 * singer：歌手
 * musicImage 音乐图片
 * musicUri 音乐链接
 */
@Parcelize
class Data(
    val musicName: String,
    val singer: String,
    val musicImage : Int,
    val musicUri : String
):Parcelable