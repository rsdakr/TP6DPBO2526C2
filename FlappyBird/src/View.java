import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JPanel {
    int width = 360;
    int height = 640;

    private Logic logic;
    private Image backgroundImage;
    private Font customFont;

    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JLabel instructionLabel;
    private JLabel gameOverLabel;
    private JLabel restartLabel;
    private JButton startButton;
    private JButton exitButton;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);

        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("assets/flappy-font.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 24);
        }

        initUI();

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(logic);
    }

    private void initUI() {
        // judul menu utama
        titleLabel = new JLabel("Flappy bird", SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(36f));
        titleLabel.setForeground(new Color(245, 166, 35));
        titleLabel.setBounds(0, height / 2 - 140, width, 50);
        add(titleLabel);

        // tombol mulai pada menu utama
        startButton = new JButton("start");
        startButton.setFont(customFont.deriveFont(18f));
        startButton.setBackground(new Color(83, 184, 39));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setFocusable(false);
        startButton.setBounds(width / 2 - 65, height / 2 + 20, 130, 42);
        startButton.addActionListener(e -> {
            logic.setState(Logic.State.READY);
            titleLabel.setVisible(false);
            startButton.setVisible(false);
            exitButton.setVisible(false);
            instructionLabel.setVisible(true);
            scoreLabel.setVisible(true);
            requestFocusInWindow();
            repaint();
        });
        add(startButton);

        // tombol keluar pada menu utama
        exitButton = new JButton("exit");
        exitButton.setFont(customFont.deriveFont(18f));
        exitButton.setBackground(new Color(228, 75, 54));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.setFocusable(false);
        exitButton.setBounds(width / 2 - 65, height / 2 + 75, 130, 42);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        // teks instruksi sebelum permainan dimulai
        instructionLabel = new JLabel("press [SPACE] to start", SwingConstants.CENTER);
        instructionLabel.setFont(customFont.deriveFont(18f));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setBounds(0, height / 2 + 10, width, 30);
        instructionLabel.setVisible(false);
        add(instructionLabel);

        // label skor permainan
        scoreLabel = new JLabel("0", SwingConstants.CENTER);
        scoreLabel.setFont(customFont.deriveFont(44f));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(0, 40, width, 50);
        scoreLabel.setVisible(false);
        add(scoreLabel);

        // teks game over saat pemain kalah
        gameOverLabel = new JLabel("game over", SwingConstants.CENTER);
        gameOverLabel.setFont(customFont.deriveFont(32f));
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setBounds(0, height / 2 - 40, width, 40);
        gameOverLabel.setVisible(false);
        add(gameOverLabel);

        // teks instruksi mengulang permainan
        restartLabel = new JLabel("press [R] to restart", SwingConstants.CENTER);
        restartLabel.setFont(customFont.deriveFont(18f));
        restartLabel.setForeground(Color.WHITE);
        restartLabel.setBounds(0, height / 2 + 10, width, 30);
        restartLabel.setVisible(false);
        add(restartLabel);
    }

    // memperbarui angka pada label skor
    public void updateScore(int score) {
        scoreLabel.setText(String.valueOf(score));
    }

    // menyembunyikan instruksi saat burung mulai terbang
    public void hideInstruction() {
        instructionLabel.setVisible(false);
    }

    // memunculkan teks game over dan petunjuk restart
    public void showGameOver() {
        gameOverLabel.setVisible(true);
        restartLabel.setVisible(true);
    }

    // mengatur ulang tampilan saat game di-restart
    public void resetUI() {
        scoreLabel.setText("0");
        scoreLabel.setVisible(true);
        instructionLabel.setVisible(true);
        gameOverLabel.setVisible(false);
        restartLabel.setVisible(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // menggambar elemen visual permainan
    public void draw(Graphics g) {
        int backgroundX = logic.getBackgroundX();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, backgroundX, 0, logic.frameWidth, logic.frameHeight, null);
            g.drawImage(backgroundImage, backgroundX + logic.frameWidth, 0, logic.frameWidth, logic.frameHeight, null);
        }

        // menggambar seluruh pipa aktif
        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null) {
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        // menggambar burung dengan orientasi rotasi
        Player player = logic.getPlayer();
        if (player != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            double rotationAngle = Math.toRadians(player.getVelocityY() * 3.5);
            double maxRotation = Math.toRadians(90);
            rotationAngle = Math.max(-Math.toRadians(30), Math.min(rotationAngle, maxRotation));

            int centerX = player.getPosX() + player.getWidth() / 2;
            int centerY = player.getPosY() + player.getHeight() / 2;

            g2d.translate(centerX, centerY);
            g2d.rotate(rotationAngle);
            g2d.translate(-centerX, -centerY);

            g2d.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
            g2d.dispose();
        }
    }
}
