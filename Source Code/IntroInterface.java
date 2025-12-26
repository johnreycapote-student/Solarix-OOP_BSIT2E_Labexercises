import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * IntroInterface - simple splash / intro screen.
 * Shows logo, system title, and motto with slow fade-in,
 * then opens the main LoginFrame.
 */
public class IntroInterface extends JFrame {

    private JLabel logoLabel;
    private JLabel titleLabel;
    private JLabel mottoLabel;

    public IntroInterface() {
        setTitle("Renewable Energy Hardware - Loading");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setResizable(false);
        setLocationRelativeTo(null);

        initComponents();
        startAnimation();
    }

    private void initComponents() {
        Color primaryOrange = new Color(255, 140, 0);
        Color background = primaryOrange; // Use orange background for the intro

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);
        root.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Center panel for logo + text
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Load logo image from Images/logo.png (relative to project root)
        // and scale it to a reasonable size for the intro screen.
        ImageIcon logoIcon = new ImageIcon("Images/logo.png");
        Image img = logoIcon.getImage();
        Image scaled = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaled);

        logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        titleLabel = new JLabel("Renewable Energy Hardware System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 0, 0, 0)); // transparent at start
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Motto
        mottoLabel = new JLabel("Powering a sustainable future, one system at a time.", SwingConstants.CENTER);
        mottoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        mottoLabel.setForeground(new Color(0, 0, 0, 0)); // transparent at start
        mottoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(logoLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(15));
        center.add(mottoLabel);
        center.add(Box.createVerticalGlue());

        // Simple bottom footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        JLabel loadingLabel = new JLabel("Loading, please wait...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setForeground(new Color(100, 100, 100));
        footer.add(loadingLabel);

        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /**
     * Simple fade-in animation:
     *  - First fade-in logo
     *  - Then fade-in title
     *  - Then fade-in motto
     *  After all finished, open LoginFrame.
     */
    private void startAnimation() {
        final int steps = 20;
        final int delay = 80; // ms between steps

        Timer logoTimer = new Timer(delay, null);
        Timer titleTimer = new Timer(delay, null);
        Timer mottoTimer = new Timer(delay, null);

        logoTimer.addActionListener(new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                // We don't fade the PNG logo itself (Swing doesn't support icon alpha easily here),
                // but we keep this timer as a small delay before starting the title fade-in.
                if (step >= steps) {
                    logoTimer.stop();
                    titleTimer.start();
                }
            }
        });

        titleTimer.addActionListener(new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                int alpha = Math.min(255, (int) (255 * (step / (float) steps)));
                titleLabel.setForeground(new Color(0, 0, 0, alpha));
                if (step >= steps) {
                    titleTimer.stop();
                    mottoTimer.start();
                }
            }
        });

        mottoTimer.addActionListener(new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                int alpha = Math.min(255, (int) (255 * (step / (float) steps)));
                mottoLabel.setForeground(new Color(0, 0, 0, alpha));
                if (step >= steps) {
                    mottoTimer.stop();
                    // Short pause then open LoginFrame
                    Timer openLoginTimer = new Timer(600, evt -> {
                        dispose();
                        LoginFrame login = new LoginFrame();
                        login.setLocationRelativeTo(null);
                        login.setVisible(true);
                    });
                    openLoginTimer.setRepeats(false);
                    openLoginTimer.start();
                }
            }
        });

        logoTimer.start();
    }
}


