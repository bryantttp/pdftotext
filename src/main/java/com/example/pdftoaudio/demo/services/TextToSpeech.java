package com.example.pdftoaudio.demo.services;

import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import javax.sound.sampled.AudioFileFormat;

public class TextToSpeech {
    private static final String VOICE_NAME = "kevin16";

    public void speakToFile(String text, String outputFile) {
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice(VOICE_NAME);

        if (voice == null) {
            System.err.println("Voice not found: " + VOICE_NAME);
            return;
        }

        voice.allocate();

        try {
            // Create a single file audio player with WAV format
            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            SingleFileAudioPlayer audioPlayer = new SingleFileAudioPlayer(outputFile.replace(".wav", ""), fileType);
            voice.setAudioPlayer(audioPlayer);

            // Convert text to speech and store it in the output file
            voice.speak(text);

            voice.deallocate();
            audioPlayer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
