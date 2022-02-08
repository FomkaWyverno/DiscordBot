package BackEND;

import net.dv8tion.jda.api.OnlineStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class DrawStatsChecker {

    public static void main(String[] args) {
        getStats("299872345047302144","\uD83D\uDC8EFomka_Wyverno\uD83D\uDC8E | Костя",OnlineStatus.DO_NOT_DISTURB,"https://cdn.discordapp.com/avatars/299872345047302144/ef8dfb5198fa41051ba19c9829bdd6b2.png?size=128");
    }


    public static byte[] getStats(String id, String nick,OnlineStatus status, String url_avatar) throws NullPointerException {
        long time = StatsChecker.getTimeUser(id);
        int messages = StatsChecker.getMessagesUser(id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage panel = new BufferedImage(900,200,BufferedImage.TYPE_INT_ARGB) /*ImageIO.read(mainPanel)*/;

            Graphics2D graphics2D = panel.createGraphics();

            int size =176;
            BufferedImage avatar;
            if (!url_avatar.isEmpty()) {
                avatar = getFinishedImage(ImageIO.read(new URL(url_avatar)),status,size);
            } else {
                avatar = getFinishedImage(ImageIO.read(getImgFile("DiscordRanks/default-avatar/default-avatar.png")), status, size);
            }


            graphics2D.drawImage(avatar,30,10,null);

            if (nick.length() > 27) {
                nick = nick.substring(0,27) + "...";
            }


            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2D.setFont(new Font("BrutaGlbExtended-ThinIt",Font.PLAIN,40));

            graphics2D.drawString(nick,190,50);

            drawVoiceStats(graphics2D,time);
            drawMessagesStats(graphics2D,messages);


            graphics2D.dispose();
            ImageIO.write(panel,"png",byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return byteArrayOutputStream.toByteArray();
    }

    private static BufferedImage getFinishedImage(BufferedImage image, OnlineStatus status, int size) { // Получить готовое изображение.
        image = reSizeImage(image,size);
        image = trimCircle(image,size);
        image = drawOnlineStatus(image,status,size);



        
        return image;
    }
    private static BufferedImage reSizeImage(BufferedImage image, int size) { // Изменить размер

        BufferedImage reSizeImage = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = reSizeImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(image,0,0,size,size,null);


        return reSizeImage;
    }
    private static BufferedImage trimCircle(BufferedImage image, int size) { // Сделать аватарку круглой

        BufferedImage trim = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = trim.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setColor(Color.white);
        graphics2D.fill(new Ellipse2D.Double(0.0,0.0,size,size));
        graphics2D.setComposite(AlphaComposite.SrcAtop);
        graphics2D.drawImage(image,0,0,null);


        return trim;
    }
    private static BufferedImage drawOnlineStatus(BufferedImage image,OnlineStatus status,int size) { // Отрисовка онлайн статуса.
        BufferedImage online = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = online.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.fill(new Ellipse2D.Double(122,130.0,40,40));
        graphics2D.setComposite(AlphaComposite.SrcOut);
        graphics2D.drawImage(image,0,0,null);
        graphics2D.dispose();
        graphics2D = online.createGraphics();  // 128 138
        BufferedInputStream onlineStatus = null;
        switch (status) {
            case ONLINE:
                onlineStatus = getImgFile("Stats/Online-Status/online.png");
                break;
            case OFFLINE:
                onlineStatus = getImgFile("Stats/Online-Status/offline.png");
                break;
            case IDLE:
                onlineStatus = getImgFile("Stats/Online-Status/idle.png");
                break;
            case DO_NOT_DISTURB:
                onlineStatus = getImgFile("Stats/Online-Status/dnd.png");
                break;
        }
        try {
            assert onlineStatus != null;
            BufferedImage statusOnline = ImageIO.read(onlineStatus);
            graphics2D.drawImage(statusOnline,128,137,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return online;
    }

    private static void drawVoiceStats(Graphics2D graphics2D,long time){ // Отрисовка микрофона + отрисовка сколько просидел в голосовом чате.
        try {
            BufferedImage micr = ImageIO.read(getImgFile("Stats/icons/microphone.png"));
            graphics2D.drawImage(micr,215,50,null);
            long seconds = time / 1000; // time == Время в milliseconds
            long minutes = seconds / 60;
            long hours = minutes / 60;

            seconds = seconds - (60*minutes);
            minutes = minutes - (60*hours);


            graphics2D.setFont(new Font("Arial",Font.PLAIN,30));
            graphics2D.drawString("Hours: "+hours+" Minutes : "+minutes+" Seconds : "+seconds,285,99);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void drawMessagesStats(Graphics2D graphics2D, int messages) { // Отрисовка изображение сообщения + количество сообщений
        try {
            BufferedImage message = ImageIO.read(getImgFile("Stats/icons/messages.png"));
            graphics2D.drawImage(message,215,120,null);
            graphics2D.setFont(new Font("Arial",Font.PLAIN,30));
            graphics2D.drawString("Messages: " + messages,285,162);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static BufferedInputStream getImgFile(String path) { // Выдаем поток на изображение (нужно только для ImageIO.read())
        return (BufferedInputStream) Objects.requireNonNull(DrawStatsChecker.class.getClassLoader().getResourceAsStream(path));
    }
}
