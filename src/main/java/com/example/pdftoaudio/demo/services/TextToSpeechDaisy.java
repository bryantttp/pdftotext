package com.example.pdftoaudio.demo.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.transform.stream.StreamSource;

import org.daisy.braille.css.BrailleCSSParser.text_transform_def_return;
import org.daisy.common.transform.XMLTransformer.OutputType;
import org.daisy.pipeline.tts.TTSEngine;
import org.daisy.pipeline.tts.TTSRegistry;
import org.daisy.pipeline.tts.TTSService;
import org.daisy.pipeline.tts.TTSService.ServiceDisabledException;
import org.daisy.pipeline.tts.TTSService.SynthesisException;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.pipeline.tts.Voice;

public class TextToSpeechDaisy {
    private TTSService ttsService;
    private TTSEngine ttsEngine;
    private Voice defaultVoice;

    public TextToSpeechDaisy() {
        // Initialize the TTS service registry and get the first available service
        TTSRegistry registry = new TTSRegistry();
        Optional<Collection<TTSService>> optionalTTSServices = Optional.ofNullable(registry.getServices());

        if (optionalTTSServices.isPresent()) {
            Collection<TTSService> collectionOfServices = optionalTTSServices.get();
            ttsService = collectionOfServices.stream().findFirst().get();
            try {
                ttsEngine = ttsService.newEngine(null);
            } catch (ServiceDisabledException e) {
                // if the service is not available, e.g. because it was disabled by the user, or because required configuration is missing.
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // Get the first available voice
            Collection<Voice> voices = null;
            try {
                voices = ttsEngine.getAvailableVoices();
            } catch (SynthesisException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            defaultVoice = voices.stream().findFirst().get();
        } 
    }

    public static XdmNode stringToXdmNode(String xmlString) throws SaxonApiException {
        // Initialize Saxon processor
        Processor processor = new Processor(false);
        DocumentBuilder builder = processor.newDocumentBuilder();

        // Convert the XML string to an XdmNode
        StringReader reader = new StringReader(xmlString);
        return builder.build(new StreamSource(reader));
    }

    public File convertTextToSpeech(String text, String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);
        XdmNode convertedText = null;
        try {
            convertedText = stringToXdmNode(text);
        } catch (SaxonApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try (AudioInputStream audioInputStream = ttsEngine.synthesize(convertedText,defaultVoice,ttsEngine.allocateThreadResources()).audio) {
            // Write the AudioInputStream to a WAV file
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        } catch (Exception e) {
            throw new IOException("Error while converting text to speech", e);
        }

        return outputFile;
    }
}
