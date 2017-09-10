package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.example.a90678.wechat_group_send_17_07_02_17_35.constants.AccessConstants;

import java.io.IOException;

/**
 * Created by 90678 on 2017/8/1.
 */

public class DeEncodecCommon {

    public static MediaFormat getFormat () {
        MediaFormat mMediaFormat = MediaFormat.createVideoFormat(AccessConstants.FORMAT,
                AccessConstants.PHONE_WIDTH, AccessConstants.PHONE_HEIGHT);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, AccessConstants.BITRATE);
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, AccessConstants.FPS);
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        return mMediaFormat;
    }

    public static MediaCodec getMediaCodec () {
        MediaCodec encoder = null;
        try {
            encoder = MediaCodec.createEncoderByType(AccessConstants.FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.configure(DeEncodecCommon.getFormat(), null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        return encoder;
    }
}
