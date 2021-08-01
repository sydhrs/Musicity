package com.huhx0015.hxaudio.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import com.huhx0015.hxaudio.utils.HXLog;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/** -----------------------------------------------------------------------------------------------
 *  [HXSoundEngine] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXSoundEngine class is a wrapper class for the SoundPool object, used for
 *  simplifying the outputting of sound effects for the application.
 *  -----------------------------------------------------------------------------------------------
 */

class HXSoundEngine {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private AudioManager soundManager; // AudioManager variable for sound effects.
    private volatile ConcurrentHashMap<Integer, Integer> soundEffectMap; // Used for storing the loaded sound effects.
    private volatile Vector<Integer> soundFxList; // Used for storing the referenced sound effects.
    private volatile SoundPool soundPool; // SoundPool variable for sound effects.
    private int engineID; // Used to determine the ID value of this instance.
    private volatile int soundEventCount = 0; // Used to count the number of sound events that have occurred.

    // CONSTANT VARIABLES:
    private static final int MAX_SIMULTANEOUS_SOUNDS = 8; // Can output eight sound effects simultaneously. Adjust this value accordingly.
    private static final int MAX_SOUND_EVENTS = 4; // Maximum number of sound events before the SoundPool object is reset. Adjust this value based on sound sample sizes. Android 2.3 (GINGERBREAD) only.
    private static final int SOUND_PRIORITY_LEVEL = 1; // Used for setting the sound priority level.
    private static final float SOUND_VOLUME_LEVEL = 1.0f; // Used for setting the left and right volume levels.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSoundEngine.class.getSimpleName();

    /** CONSTRUCTOR METHOD _____________________________________________________________________ **/

    // HXSoundEngine(): Constructor method for this class.
    HXSoundEngine(int id) {
        this.engineID = id;
    }

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // initSoundPool(): Initializes the SoundPool object. Depending on the Android version of the
    // device, the SoundPool object is created using the appropriate methods.
    private synchronized void initSoundPool() {

        // API 21+: Android 5.0 and above.
        if (Build.VERSION.SDK_INT > 20) {
            HXLog.d(LOG_TAG, "INITIALIZING (" + engineID + "): initSoundPool(): Using Lollipop (API 21+) SoundPool initialization.");
            soundPool = buildSoundPool();
        }

        // API 9 - 20: Android 2.3 - 4.4
        else {
            HXLog.d(LOG_TAG, "INITIALIZING (" + engineID + "): initSoundPool(): Using GB/HC/ICS/JB/KK (API 9 - 20) SoundPool initialization.");
            soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
    }

    // buildSoundPool(): Builds the SoundPool object. This implementation is only used on devices
    // running Android 5.0 and later.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private synchronized SoundPool buildSoundPool() {

        // Initializes the AudioAttributes.Builder object.
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // Sets the audio type to USAGE_GAME.
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                .build();

        // Initializes the SoundPool.Builder object.
        SoundPool soundBuilder = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(MAX_SIMULTANEOUS_SOUNDS) // Sets the maximum number of audio streams.
                .build();

        HXLog.d(LOG_TAG, "INITIALIZING (" + engineID + "): buildSoundPool(): SoundPool construction complete.");

        return soundBuilder; // Returns the newly created SoundPool object.
    }

    // reinitialize(): This method re-initializes the SoundPool object for devices running
    // on Android 2.3 (GINGERBREAD) and earlier. This is to help minimize the AudioTrack out of
    // memory error, which was limited to a small 1 MB size buffer.
    synchronized void reinitialize(Context context) {

        // GINGERBREAD: The SoundPool is released and re-initialized. This is done to minimize the
        // AudioTrack out of memory (-12) error.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            HXLog.d(LOG_TAG, "RE-INITIALIZING (" + engineID + "): reinitialize(): The SoundPool object is being re-initialized.");

            release(); // Releases the SoundPool object.
            initSoundPool(); // Initializes the SoundPool object.

            // Re-generates the soundEffectMap.
            if (soundFxList != null && !soundFxList.isEmpty()) {
                for (int i = 0; i < soundFxList.size(); i++) {
                    addSoundFx(soundFxList.get(i), context);
                }
                HXLog.d(LOG_TAG, "RE-INITIALIZING (" + engineID + "): reinitialize(): Re-generated sound effect map.");
            }

            soundEventCount = 0; // Resets the sound event counter.
        }
    }

    /** SOUND METHODS __________________________________________________________________________ **/

    // prepareSoundFx(): Prepares the specified resource for sound playback.
    synchronized void prepareSoundFx(final int resource, final boolean isLoop, Context context) {

        HXLog.w(LOG_TAG, "TEST (" + engineID + "): prepareSoundFx(): Sound Resource (" + resource + ")");

        // Initializes the SoundPool object.
        if (soundPool == null) {
            initSoundPool();
        }

        // ANDROID 2.3 (GINGERBREAD): The SoundPool object is re-initialized if the sound event
        // counter has reached the MAX_SOUND_EVENT limit. This is to handle the AudioTrack 1 MB
        // buffer limit issue.
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) && (soundEventCount >= MAX_SOUND_EVENTS)) {
            HXLog.w(LOG_TAG, "WARNING (" + engineID + "): prepareSoundFx(): Sound event count (" + soundEventCount + ") has exceeded the maximum number of sound events. Re-initializing the engine.");
            reinitialize(context);
        }

        // Checks to see if the sound effect has been already added. If not it is added to the list
        // the sound effect is prepared in SoundPool.
        boolean isAdded = addSoundFx(resource, context);

        // If the soundPool object is not yet fully loaded, the listener will play the sound effect
        // after the soundPool object has fully loaded.
        if (isAdded) {
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    HXLog.d(LOG_TAG, "READY (" + engineID + "): onLoadComplete(): The SoundPool object is ready.");
                    playSoundFx(sampleId, isLoop);
                }
            });
        } else {
            playSoundFx(soundEffectMap.get(resource), isLoop);
        }

        soundEventCount++;
    }

    // playSoundFx(): Plays the specified sound effect.
    private synchronized void playSoundFx(int id, boolean isLoop) {
        if (soundEffectMap != null && !soundEffectMap.isEmpty()) {
            soundPool.play(id, SOUND_VOLUME_LEVEL, SOUND_VOLUME_LEVEL, SOUND_PRIORITY_LEVEL,
                    isLoop ? -1 : 0, 1.0f);
        }
    }

    // pauseSounds(): Pauses all sound effects playing in the background.
    void pauseSounds() {

        // Checks to see if the soundPool object has been initiated first before pausing sound
        // effect playback.
        if (soundPool != null) {
            soundPool.autoPause(); // Pauses all sound effect playback.
            HXLog.d(LOG_TAG, "SOUND (" + engineID + "): pauseSounds(): All sound playback has been paused.");
        } else {
            HXLog.e(LOG_TAG, "ERROR (" + engineID + "): pauseSounds(): Cannot pause sound playback due to SoundPool object being null.");
        }
    }

    // resumeSounds(): Resumes all sound effect playback in the background.
    void resumeSounds() {

        // Checks to see if soundPool has been initiated first before resuming sound effect playback.
        if (soundPool != null) {
            soundPool.autoResume(); // Resumes all sound effect playback.
            HXLog.d(LOG_TAG, "SOUND (" + engineID + "): Resuming sound effect playback.");
        }
    }

    /** SOUND HELPER METHODS ___________________________________________________________________ **/

    // addSoundFx(): Adds the specified sound resource to the soundEffectMap, if it has not been
    // added.
    private synchronized boolean addSoundFx(int resource, Context context) {

        if (soundEffectMap == null) {
            soundEffectMap = new ConcurrentHashMap<>();
        }

        // Checks to see if the sound effect has already been added.
        Integer soundEffect = soundEffectMap.get(resource);
        if (soundEffect == null) {

            // Initializes the SoundPool object.
            if (soundPool == null) {
                initSoundPool();
            }

            soundEffectMap.put(resource, soundPool.load(context, resource, SOUND_PRIORITY_LEVEL));

            // Stores the reference for the added sound resource into soundFxList.
            if (soundFxList == null) {
                soundFxList = new Vector<>();
            }
            soundFxList.add(resource);

            HXLog.d(LOG_TAG, "PREPARING (" + engineID + "): addSoundFx(): New sound effect has been added.");
            return true;
        } else {
            HXLog.d(LOG_TAG, "PREPARING (" + engineID + "): addSoundFx(): Sound effect already added to soundEffectMap.");
            return false;
        }
    }

    // getCurrentVolume(): Retrieves the current volume value.
    private synchronized float getCurrentVolume(Context context) {
        if (soundManager == null) {
            soundManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return soundManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // loadSoundFxList(): Loads the list of sound effects into the soundEffectMap.
    synchronized void loadSoundFxList(List<Integer> soundList, Context context) {

        // Removes any existing onLoadCompleteListeners for SoundPool.
        if (soundPool != null) {
            soundPool.setOnLoadCompleteListener(null);
        }

        // Loads each resource from the soundList into the soundEffectMap and soundFxList.
        for (int resource : soundList) {
            if (resource != 0) {
                addSoundFx(resource, context);
            }
        }
    }

    // release(): Used to free up memory resources when all audio effects are no longer needed.
    void release() {

        // Releases SoundPool resources.
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;

            if (soundEffectMap != null) {
                soundEffectMap.clear();
            }

            HXLog.d(LOG_TAG, "RELEASE (" + engineID + "): release(): SoundPool object has been released.");
        } else {
            HXLog.e(LOG_TAG, "ERROR (" + engineID + "): release(): SoundPool object is null and cannot be released.");
        }
    }
}