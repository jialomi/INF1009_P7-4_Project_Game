package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.AudioOutput;
import io.github.some_example_name.engine.io.IOManager;

public class CellAudioHandler {

    private AudioOutput getAudio() {
        return IOManager.getInstance().getAudio();
    }

    public void playEatCellSquelch() {
        getAudio().playSound("squelch.mp3");
    }

    public void playTCellDamage() {
        getAudio().playSound("damage.mp3"); // Using existing damage/crash sound
    }

    public void playRadioactiveAlert() {
        getAudio().playSound("alert.mp3");
    }

    public void setOrganBGM(String organName) {
        getAudio().playMusic(organName.toLowerCase() + "_bgm.mp3");
    }

    public void setEvolvedStageBGM(int stage) {
        getAudio().playMusic("stage_" + stage + "_bgm.mp3");
    }
}