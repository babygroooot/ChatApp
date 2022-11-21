package com.example.chatapp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioEncoder
import android.media.MediaRecorder.AudioSource
import android.media.MediaRecorder.OutputFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import java.io.IOException

object VoiceManager {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mFilePath: String? = null

    private const val BITS_PER_SAMPLE = 16
    private const val CHANNEL_NUMBER = 1
    private const val COMPRESSION_AMOUNT = 8


    fun startVoice(context: Context) {
        val sampleRate = 8000
        val uncompressedBitRate = sampleRate * BITS_PER_SAMPLE * CHANNEL_NUMBER
        val encodedBitRate = uncompressedBitRate / COMPRESSION_AMOUNT
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else{
            MediaRecorder()
        }

        mediaRecorder?.run {
            setAudioSource(AudioSource.MIC)
            setOutputFormat(OutputFormat.MPEG_4)
            setAudioEncoder(AudioEncoder.AAC)
            setAudioChannels(CHANNEL_NUMBER)
            setAudioSamplingRate(sampleRate)
            setAudioEncodingBitRate(encodedBitRate)
            setOutputFile(mFilePath)
        }

        try {
            mediaPlayer?.prepare()
        } catch (e: IOException){
            e.printStackTrace()
        }

        mediaRecorder?.start()

    }

    fun stopRecording(callback: (Uri) -> Unit){
        if (mediaRecorder != null){
            mediaRecorder?.run {
                stop()
                release()
                callback(Uri.parse(mFilePath))
                mediaRecorder = null
            }
        }
    }

    fun cancelRecording(){
        if (mediaRecorder != null){
            mediaRecorder?.run {
                stop()
                release()
                mediaRecorder = null
            }
        }
    }

    fun startMediaPlayer(filePath:String, imageView: ImageView, seekBar: SeekBar, isSender:Boolean){
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
        }
        mediaPlayer?.run {
            setDataSource(filePath)
            prepareAsync()
            setOnPreparedListener {
                seekBar.max = it.duration
                mediaPlayer = it
                start()

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    try {
                        seekBar.progress = currentPosition
                        handler.postDelayed({},1000)
                    }catch (e: java.lang.Exception){
                        Thread.sleep(100)
                        seekBar.progress = 0
                    }
                },0)

            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        mediaPlayer?.setOnCompletionListener {
            stopMediaPlayer()
        }

    }

    fun stopMediaPlayer(){
        if (mediaPlayer != null){
            mediaPlayer?.run {
                stop()
                release()
            }
            mediaPlayer = null
        }
    }

}