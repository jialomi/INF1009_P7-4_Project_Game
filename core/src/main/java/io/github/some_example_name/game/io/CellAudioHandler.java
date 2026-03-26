package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.AudioOutput;

public class CellAudioHandler {
    private final AudioOutput audio;

    public CellAudioHandler(AudioOutput audio) {
        if (audio == null) {
            throw new IllegalArgumentException("AudioOutput cannot be null");
        }
        this.audio = audio;
    }

    public void playEatCellSquelch() {
        audio.playSound("squelch.mp3");
    }

    public void playTCellDamage() {
        audio.playSound("damage.mp3"); // using existing damage/crash sound
    }

    public void playRadioactiveAlert() {
        audio.playSound("crash.mp3");
    }

    public void setOrganBGM(String organName) {
        audio.playMusic(organName.toLowerCase() + "_bgm.mp3");
    }

    public void setEvolvedStageBGM(int stage) {
        audio.playMusic("stage_" + stage + "_bgm.mp3");
    }
}
