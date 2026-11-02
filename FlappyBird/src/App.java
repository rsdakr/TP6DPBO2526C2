import javax.swing.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Logic logic = new Logic();
        View view = new View(logic);
        logic.setView(view);

        view.requestFocus();

        frame.add(view);
        frame.pack();
        frame.setVisible(true);
    }
}
