import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Main implements NativeKeyListener {
    TrayIcon trayIcon;
    Robot robot;
    Timer timer;
    int x = 100;
    int y = 100;

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == 41) {
            run();
        }
    }

    public Main() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/icon/icon.png"))).getImage());
        if (SystemTray.isSupported()) {
            trayIcon.setToolTip("YuXiang Switch");

            PopupMenu popupMenu = new PopupMenu();

            MenuItem runItem = new MenuItem("运行(`)");
            runItem.addActionListener(e -> run());
            popupMenu.add(runItem);

            MenuItem setItem = new MenuItem("设置");
            setItem.addActionListener(e -> {
                trayIcon.displayMessage("YuXiang Switch", "请将鼠标指针移动到需要点击的位置。", TrayIcon.MessageType.INFO);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Point point = MouseInfo.getPointerInfo().getLocation();
                        x = (int) point.getX();
                        y = (int) point.getY();
                        trayIcon.displayMessage("YuXiang Switch", "捕获完成，捕获到的坐标为(" + x + "," + y + ")。", TrayIcon.MessageType.INFO);
                        timer.cancel();
                    }
                }, 4000);
            });
            popupMenu.add(setItem);

            MenuItem exitItem = new MenuItem("退出");
            exitItem.addActionListener(e -> System.exit(0));
            popupMenu.add(exitItem);

            trayIcon.setPopupMenu(popupMenu);
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "您当前的操作系统不支持系统托盘，无法启动本程序。", "YuXiang Switch", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void run() {
        Point nowPoint = MouseInfo.getPointerInfo().getLocation();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove((int) nowPoint.getX(), (int) nowPoint.getY());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            JOptionPane.showMessageDialog(null, "系统按键注册失败，无法启动本程序。", "YuXiang Switch", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        GlobalScreen.addNativeKeyListener(new Main());
    }
}
