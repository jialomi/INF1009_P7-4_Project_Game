package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.AudioOutput;

public class CellAudioHandler {
    private static final String SFX_PATH = "audio/sfx/";
    private static final String MUSIC_PATH = "audio/music/";
    private static final String MENU_BGM = MUSIC_PATH + "menu_bgm.mp3";

    private final AudioOutput audio;

    public CellAudioHandler(AudioOutput audio) {
        if (audio == null) {
            throw new IllegalArgumentException("AudioOutput cannot be null");
        }
        this.audio = audio;
    }

    public void playEatCellSquelch() {
        audio.playSound(SFX_PATH + "squelch.mp3");
    }

    public void playTCellDamage() {
        audio.playSound(SFX_PATH + "damage.mp3");
    }

    public void playRadioactiveAlert() {
        audio.playSound(SFX_PATH + "crash.mp3");
    }

    public void setOrganBGM(String organName) {
        audio.playMusic(MUSIC_PATH + organName.toLowerCase() + "_bgm.mp3");
    }

    public void setMenuBGM() {
        audio.playMusic(MENU_BGM);
    }

    public void setEvolvedStageBGM(int stage) {
        audio.playMusic(MUSIC_PATH + "stage_" + stage + "_bgm.mp3");
    }
}
