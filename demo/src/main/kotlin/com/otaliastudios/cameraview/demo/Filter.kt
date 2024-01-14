package com.otaliastudios.cameraview.demo

import android.content.Context
import android.graphics.Point
import android.opengl.GLES20
import android.util.Log
import android.widget.ImageView
import com.effectsar.labcv.core.effect.EffectManager
import com.effectsar.labcv.core.effect.EffectResourceHelper
import com.effectsar.labcv.core.license.EffectLicenseHelper
import com.effectsar.labcv.core.opengl.Drawable2d
import com.effectsar.labcv.core.opengl.Drawable2d.Prefab
import com.effectsar.labcv.core.opengl.GlUtil
import com.effectsar.labcv.core.util.ImageUtil
import com.effectsar.labcv.core.util.LogUtils
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants
import com.effectsar.labcv.effectsdk.EffectsSDKEffectConstants.Rotation
import com.otaliastudios.cameraview.filter.BaseFilter


class Filter(context: Context?) : BaseFilter(), EffectManager.OnEffectListener {
    private val TAG = GlUtil.TAG

    // Handles to the GL program and various components of it.
    protected var mProgramHandle = 0

    protected var mDrawable2d: Drawable2d? = null

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mEffectManager: EffectManager
    private var mImageUtil: ImageUtil

    // Simple vertex shader, used for all programs.

//    override fun createDefaultFragmentShader(): String {
//        return """precision mediump float;
//                     varying vec2 vTextureCoord;
//                     uniform sampler2D sTexture;
//                     void main() {
//                         gl_FragColor = texture2D(sTexture, vTextureCoord);
//                     }
//                     """
//    }
//
//    override fun createDefaultVertexShader(): String {
//        return "uniform mat4 uMVPMatrix;\n" +
//                "uniform mat4 uTexMatrix;\n" +
//                "attribute vec4 aPosition;\n" +
//                "attribute vec2 aTextureCoord;\n" +
//                "varying vec2 vTextureCoord;\n" +
//                "void main() {\n" +
//                "    gl_Position = uMVPMatrix * aPosition;\n" +
//                "    vTextureCoord = (uTexMatrix * vec4(aTextureCoord, 0.0, 1.0)).xy;\n" +
//                "}\n";
//    }

    init {
        Log.i("setSize", "initttt")
        mProgramHandle = GlUtil.createProgram(vertexShader, fragmentShader)
        mDrawable2d = Drawable2d(Prefab.FULL_RECTANGLE)

        // init effect manager

        // init effect manager
        mEffectManager = EffectManager(
            context, EffectResourceHelper(context), EffectLicenseHelper.getInstance(context)
        )

        mEffectManager.setOnEffectListener(this)

        mImageUtil = ImageUtil()
    }

    @Suppress("unused")
    constructor() : this(Demo.context) {

    }

    override fun getFragmentShader(): String {
        return createDefaultFragmentShader()
    }

    override fun onPreDraw(timestampUs: Long, transformMatrix: FloatArray, textureId: Int) {
        val mDrawRotation = if (mWidth > mHeight) {
            90
        } else {
            270
        }

        val transition = ImageUtil.Transition().rotate(mDrawRotation.toFloat())

        val texture2D: Int = mImageUtil.transferTextureToTexture(
            textureId,
            EffectsSDKEffectConstants.TextureFormat.Texture_Oes,
            EffectsSDKEffectConstants.TextureFormat.Texure2D,
            mWidth,
            mHeight,
            transition
        )

        var rotation = Rotation.CLOCKWISE_ROTATE_0
        when (mDrawRotation) {
            0 -> rotation = Rotation.CLOCKWISE_ROTATE_0
            90 -> rotation = Rotation.CLOCKWISE_ROTATE_90
            180 -> rotation = Rotation.CLOCKWISE_ROTATE_180
            270 -> rotation = Rotation.CLOCKWISE_ROTATE_270
        }

        var dstTexture: Int = mImageUtil.prepareTexture(mWidth, mHeight)

        mEffectManager.setCameraPosition(true)

        val ret: Boolean = mEffectManager.process(
            texture2D, dstTexture, mWidth, mHeight, rotation, System.currentTimeMillis()
        )

        if (!ret) {
            dstTexture = textureId
        }

        if (!GLES20.glIsTexture(dstTexture)) {
            LogUtils.e("output texture not a valid texture")
            return
        }


        val drawTransition = ImageUtil.Transition().crop(
                ImageView.ScaleType.CENTER_CROP,
                0,
                mHeight,
                mWidth,
                mHeight,
                mWidth,
            )
        mImageUtil.drawFrameOnScreen(
            dstTexture,
            EffectsSDKEffectConstants.TextureFormat.Texure2D,
            mWidth,
            mHeight,
            drawTransition.matrix
        )
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        Log.i("setSize", "width: " + width + " height:" + height)
        mWidth = width
        mHeight = height
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(programHandle: Int) {
        super.onCreate(programHandle)

        val ret: Int = mEffectManager.init()
        if (ret != EffectsSDKEffectConstants.EffectsSDKResultCode.BEF_RESULT_SUC) {
            LogUtils.e("mEffectManager.init() fail!! error code =\$ret")
        }


        mEffectManager.setComposeNodes(
            arrayOf<String>("style_makeup/wennuan"), arrayOf<String?>(null)
        )
        mEffectManager.updateComposerNodeIntensity("style_makeup/wennuan", "Filter_ALL", 0.8f)
        mEffectManager.updateComposerNodeIntensity("style_makeup/wennuan", "Makeup_ALL", 0.8f)

    }

    override fun onEffectInitialized() {
//        TODO("Not yet implemented")
    }

}