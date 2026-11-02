import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class Sound {
    private AudioFormat format;
    private byte[] audioData;

    public Sound(String assetPath) {
        try {
            URL soundUrl = getClass().getResource(assetPath);
            if (soundUrl == null) {
                System.err.println("Audio file not found: " + assetPath);
                return;
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl)) {
                format = audioStream.getFormat();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                audioData = baos.toByteArray();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (audioData == null || format == null) return;

        new Thread(() -> {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
                AudioInputStream stream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

                Clip activeClip = AudioSystem.getClip();
                activeClip.open(stream);

                activeClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        activeClip.close();
                    }
                });

                activeClip.start();
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
