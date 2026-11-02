import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {
    // status siklus permainan
    public enum State {
        MENU,
        READY,
        PLAYING,
        DYING,
        GAMEOVER
    }

    // dimensi ukuran jendela permainan
    int frameWidth = 360;
    int frameHeight = 640;

    // posisi awal dan ukuran burung
    int playerStartPosX = frameWidth / 2 - 50;
    int playerStartPosY = frameHeight / 2 - 50;
    int playerWidth = 34;
    int playerHeight = 24;

    // posisi awal dan ukuran pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // status dan referensi objek permainan
    State state = State.MENU;
    View view;
    Player player;
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;
    int score = 0;

    // aset gambar dan daftar pipa aktif
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    // kecepatan pipa dan pergerakan latar belakang
    int pipeVelocityX = -3;
    int backgroundX = 0;
    int backgroundSpeed = -1;

    // efek suara permainan
    Sound jumpSound;
    Sound hitSound;
    Sound scoreSound;
    Sound dieSound;

    // inisialisasi aset dan timer permainan
    public Logic() {
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);

        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        pipes = new ArrayList<Pipe>();

        jumpSound = new Sound("assets/sfx_wing.wav");
        hitSound = new Sound("assets/sfx_hit.wav");
        scoreSound = new Sound("assets/sfx_point.wav");
        dieSound = new Sound("assets/sfx_die.wav");

        pipesCooldown = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void setView(View view) {
        this.view = view;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - (pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + openingSpace + pipeHeight, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);

        // menghapus pipa setelah lewat dari layar
        Timer cleanupTimer = new Timer(3500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pipes.remove(upperPipe);
                pipes.remove(lowerPipe);
            }
        });
        cleanupTimer.setRepeats(false);
        cleanupTimer.start();
    }

    // memicu animasi kekalahan saat burung menabrak pipa atau tanah
    public void die() {
        if (state == State.DYING || state == State.GAMEOVER) return;

        state = State.DYING;
        pipesCooldown.stop();
        hitSound.play();
        player.setVelocityY(-5);

        Timer dieTimer = new Timer(350, e -> dieSound.play());
        dieTimer.setRepeats(false);
        dieTimer.start();

        // memunculkan tampilan game over dan hentikan logic game setelah jeda singkat
        Timer stopTimer = new Timer(800, e -> {
            view.showGameOver();
            state = State.GAMEOVER;
            gameLoop.stop();
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    // memperbarui posisi elemen permainan setiap frame
    public void move() {
        // menjatuhkan burung ke bawah saat fase dying
        if (state == State.DYING) {
            player.setVelocityY(player.getVelocityY() + gravity);
            player.setPosY(player.getPosY() + player.getVelocityY());
            return;
        }

        if (state == State.GAMEOVER) return;

        // menggeser gambar latar belakang
        backgroundX += backgroundSpeed;
        if (backgroundX <= -frameWidth) {
            backgroundX = 0;
        }

        if (state == State.MENU || state == State.READY) return;

        // menerapkan gravitasi pada burung saat bermain
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(0, player.getPosY()));

        // memeriksa tabrakan burung dengan tanah
        if (player.getPosY() > 550) {
            die();
            return;
        }

        // memperbarui pipa dan mendeteksi tabrakan
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            // menambah skor saat melewati pipa atas
            if (!pipe.isPassed() && pipe.getPosY() < 0 && player.getPosX() > pipe.getPosX()) {
                pipe.setPassed(true);
                score++;
                scoreSound.play();
                if (view != null) {
                    view.updateScore(score);
                }
            }

            // mendeteksi tabrakan fisik antara burung dan pipa
            boolean insideX = player.getPosX() < pipe.getPosX() + pipe.getWidth() &&
                              player.getPosX() + player.getWidth() > pipe.getPosX();
            boolean insideY = (pipe.getImage() == upperPipeImage)
                    ? player.getPosY() < pipe.getPosY() + pipe.getHeight()
                    : player.getPosY() + player.getHeight() > pipe.getPosY();

            if (insideX && insideY) {
                die();
                break;
            }
        }
    }

    // mengatur ulang kondisi permainan ke posisi awal
    public void resetGame() {
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);

        pipes.clear();
        score = 0;
        backgroundX = 0;

        state = State.READY;
        gameLoop.start();

        if (view != null) {
            view.resetUI();
            view.repaint();
        }
    }

    public int getScore() {
        return score;
    }

    public int getBackgroundX() {
        return backgroundX;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (view != null) {
            view.repaint();
            Toolkit.getDefaultToolkit().sync();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // menangani input tombol dari pengguna
    @Override
    public void keyPressed(KeyEvent e) {
        // space untuk memulai terbang jika state game adalah ready
        if (state == State.READY && (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP)) {
            state = State.PLAYING;
            pipesCooldown.start();
            player.setVelocityY(-10);
            jumpSound.play();
            if (view != null) {
                view.hideInstruction();
            }
            return;
        }

        // space untuk melompat jika state game adalah playing
        if (state == State.PLAYING && (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP)) {
            player.setVelocityY(-10);
            jumpSound.play();
            return;
        }

        // R untuk mengulang permainan jika state game adalah game over
        if (state == State.GAMEOVER && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
