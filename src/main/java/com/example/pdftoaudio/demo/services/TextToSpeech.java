package com.example.pdftoaudio.demo.services;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

public class TextToSpeech {
    private static final String VOICE_NAME = "kevin16";

    public File speakToFile(String text, String outputFile) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice(VOICE_NAME);

        if (voice == null) {
            System.err.println("Voice not found: " + VOICE_NAME);
            return null;
        }

        voice.allocate();

        try {
            // Create a single file audio player with WAV format
            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            SingleFileAudioPlayer audioPlayer = new SingleFileAudioPlayer(outputFile.replace(".wav", ""), fileType);
            voice.setAudioPlayer(audioPlayer);

            // Convert text to speech and store it in the output file
            voice.speak(text);
            File audioFile = new File(outputFile);

            voice.deallocate();
            audioPlayer.close();
            return audioFile;
        } catch (Exception e) {
            return null;
        }
    }
}
