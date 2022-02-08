package BackEND;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class DrawRankCard {

    private static JDA jda = App.getJDA();


    private static final String panel = "DiscordRanks/Panel/mainPanel.png";
    private static final String InfoPanel = "Panel/InfoRank.png";
    private static final String crystal = "DiscordRanks/crystal/crystal.png";
    /*private static String small_rank = "src\\main\\resources\\DiscordRanks\\small-rank\\";*/

    private static final double scoreboardCord_X = 363.0;
    private static final double scoreboardCord_Y = 151.0;
    private static final double scoreboardWidth = 517.0;
    private static final double scoreboardHeight = 45.0;
    private static final double scoreboardRounding = 45.0;
    private static int[] ranks = new int[30];
    private static final String[] nameRanks = {"Новобранец","Рядовой","Ефрейтор","Капрал","Мастер-капрал","Сержант",
            "Штаб-сержант","Мастер-сержант","Первый сержант","Сержант-майор",
            "Уорэнт-офицер 1","Уорэнт-офицер 2","Уорэнт-офицер 3","Уорэнт-офицер 4","Уорэнт-офицер 5",
            "Младший лейтенант","Лейтенант","Старший лейтенант","Капитан","Майор","Подполковник","Полковник",
            "Бригадир","Генерал-майор","Генерал-лейтенант","Генерал","Маршал","Фельдмаршал",
            "Командор","Генералиссимус","Легенда"};

    static { // Загрузка конфигов. В конфиге сколько нужно для получение ранга.
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File("lvl-need-rank.cfg"))));
            /* BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    "src\\main\\java\\BackEND\\DiscordRanks\\lvl-need-rank.cfg")));*/
            for (int i = 0; i < 30; i++) {
                String str = reader.readLine();
                str = str.substring(str.indexOf(":")+1);
                str = str.trim();
                ranks[i] = Integer.parseInt(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
    }

    public static int[] getRanks() {
        return ranks;
    }

    public static String[] getNameRanks() {
        return nameRanks;
    }

    public static byte[] getRankInfo(String avatar, String info, OnlineStatus onlineStatus) throws IOException {

        /*BufferedImage panel = ImageIO.read(DrawRankCard.panel);*/
        BufferedImage panel = ImageIO.read(getImgFile(DrawRankCard.panel));
        BufferedImage ava = new BufferedImage(125,125,BufferedImage.TYPE_INT_ARGB);
        if (avatar == null) {
            ava = ImageIO.read(getImgFile("DiscordRanks/default-avatar/default-avatar.png"));
        } else {
            ava = ImageIO.read(new URL(avatar));
        }
        ava = getFinishedImage(ava,onlineStatus,125);
        String[] stats = info.split("⸎⸕"); // 0 = Место в топе
                                                  // 1 = Уровень игрока
                                                  // 2 = Опыт игрока
                                                  // 3 = Нужно опыта для повышения
                                                  // 4 = Никнейм
        drawScoreboard(panel,stats[2],stats[3]);

        Graphics2D g2DPanel = panel.createGraphics();

        g2DPanel.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2DPanel.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2DPanel.drawImage(ava,195,15,null); // Draw Avatar

        /*drawOnlineStatus(g2DPanel,onlineStatus);*/ // Cтарая отрисовка онлайн-статуса

        drawLVL(g2DPanel,stats[1], stats[0]);

        drawNickName(g2DPanel,stats[4]);

        drawRank(g2DPanel,stats[1]);

        g2DPanel.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(panel,"png",byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getOtherRank(String id) throws IOException, InterruptedException, isBotException {
        byte[] image = null;
        boolean stop = false;
        for (int i = 0; i < jda.awaitReady().getGuilds().size();i++) {
            if (stop) {
                break;
            }
            for (int j = 0; j < jda.awaitReady().getGuilds().get(i).getMembers().size(); j++) {
                if (stop) {
                    break;
                }
                if (id.equals(jda.awaitReady().getGuilds().get(i).getMembers().get(j).getId())) {
                    Member member = jda.awaitReady().getGuilds().get(i).getMembers().get(j);
                    if (member.getUser().isBot()) {
                        throw new isBotException();
                    }
                    image = getRankInfo(member.getUser().getAvatarUrl(),
                            RankDiscordChecker.getRank(member.getId(),member.getEffectiveName()),
                            member.getOnlineStatus());
                    stop = true;
                    break;
                }
            }
        }
        return image;
    }

    public static byte[] getInfoPanel() throws IOException {
        BufferedImage infoPanel = ImageIO.read(getImgFile(InfoPanel));
        Graphics2D graphics2D = infoPanel.createGraphics();
        int x = 20;
        int y = 85;
        Font fontForLVL = new Font("BrutaGlbExtended-SemiBold",Font.PLAIN,15);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(fontForLVL);
        graphics2D.drawString("Нужен ур. - 0", x+90,y-13);
        for (int i = 29; i > -1; i--) {
            if (y > 1000) {
                y = 10;
                x = x + 400;
            }
            graphics2D.drawString("Нужен ур. - " + ranks[i], x+90,y+63);
            y = y+75;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(infoPanel,"png",byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getMyCrystal(String id,String nick) throws IOException {
        String crystal = RankDiscordChecker.getMyCrystal(id,nick);
        BufferedImage image = new BufferedImage(1080,220,BufferedImage.TYPE_INT_ARGB);
        BufferedImage image_crystal = ImageIO.read(getImgFile(DrawRankCard.crystal));
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(image_crystal,0,0,null);


        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(new Font("BrutaGlbExtended-ThinIt",Font.BOLD,50));
        graphics2D.drawString("CRYSTALS:",300,100);
        graphics2D.setColor(Color.CYAN);
        graphics2D.setFont(new Font("BrutaGlbExtended-SemiBold",Font.PLAIN,40));
        graphics2D.drawString(crystal,650,100);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(image,"png",byteArrayOutputStream);


        return byteArrayOutputStream.toByteArray();
    }

    /*private static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) { // Обрезаем и делаем круг и картинки
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(0.0,0.0, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }*/

    private static BufferedImage getFinishedImage(BufferedImage image, OnlineStatus status, int size) {
        image = reSizeImage(image,size);
        image = trimCircle(image,size);
        image = drawOnlineStatus(image,status,size);



        return image;
    }
    private static BufferedImage reSizeImage(BufferedImage image, int size) {

        BufferedImage reSizeImage = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = reSizeImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(image,0,0,size,size,null);


        return reSizeImage;
    }
    private static BufferedImage trimCircle(BufferedImage image, int size) {

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
    private static BufferedImage drawOnlineStatus(BufferedImage image,OnlineStatus status,int size) { // Отрисовка онлайн-статуса.
        BufferedImage online = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = online.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.fill(new Ellipse2D.Double(85.0,89.0,37,37)); // 122 130 40 40
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
            graphics2D.drawImage(statusOnline,90,95,null); // 128 138
        } catch (IOException e) {
            e.printStackTrace();
        }
        return online;
    }

    private static void drawScoreboard(BufferedImage image,String xp,String need_xp) { // Отрисовка скорборда и сколько нужно опыта [xp/need_xp]
        int iXp = Integer.parseInt(xp);
        int iNeed_Xp = Integer.parseInt(need_xp);

        int onePercent = iNeed_Xp/100;
        int percent = iXp/onePercent;

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.white);
        graphics2D.fill(new RoundRectangle2D.Double(scoreboardCord_X,scoreboardCord_Y,scoreboardWidth,scoreboardHeight,
                scoreboardRounding,scoreboardRounding));


        double width = scoreboardWidth/100*percent;
        if (width < 45.0) {
            width = 45.0;
        }
        if (scoreboardWidth <= width) {
            width = scoreboardWidth;
        }
        graphics2D.setColor(new Color(12, 230, 156));

        graphics2D.fill(new RoundRectangle2D.Double(scoreboardCord_X-2,scoreboardCord_Y,width,
                scoreboardHeight,scoreboardRounding,scoreboardRounding));

        graphics2D.setFont(new Font("Brush Script MT",Font.BOLD,21));
        graphics2D.setColor(Color.white);

        String dXP =  xp + " / " + need_xp + " XP";

        graphics2D.drawString(dXP,746+cordXP(dXP),141);


    }

    private static int cordXP(String xp) {
        int len = xp.length();
        if (len == 16) {
            return 0;
        }
        if (len < 16) {
            len = 16-len;
            len = len*10;
            return len;
        } else if (len > 16) {
            len = len - 16;
            len = len*10;
            return -len;
        }
        return 0;
    }

   /* private static void drawOnlineStatus(Graphics2D graphics2D,OnlineStatus onlineStatus) { // Отрисовка онлайн старого статуса
        String status = "Online Status: ";
        graphics2D.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,20));
        graphics2D.drawString(status,140,170);



        switch (onlineStatus) {
            case ONLINE:
                graphics2D.setColor(new Color(133, 238, 143));
                status = "Online";
                break;
            case OFFLINE:
                graphics2D.setColor(Color.GRAY);
                status = "Offline";
                break;
            case INVISIBLE:
                graphics2D.setColor(Color.GRAY);
                status = "Invisible";
                break;
            case DO_NOT_DISTURB:
                graphics2D.setColor(Color.red);
                status = "Do not disturb";
                break;
            case IDLE:
                graphics2D.setColor(new Color(238, 147, 0));
                status = "Idle";
                break;
        }
        if (onlineStatus == OnlineStatus.DO_NOT_DISTURB) {
            graphics2D.drawString(status,190,190);
        } else {
            graphics2D.drawString(status, 280, 170);
        }// 280 170
    }*/

    private static void drawLVL(Graphics2D graphics2D, String LVL, String TOP) { // Отрисовка уровня и места в топе
        graphics2D.setFont(new Font("BrutaGlbExtended-Thin",Font.BOLD,24));
        graphics2D.setColor(Color.white);
        graphics2D.drawString("ур.",363,135);
        graphics2D.drawString("top",535,135);

        if (LVL.length() < 4) {
            graphics2D.setFont(new Font("BrutaGlbExtended-Bold",Font.PLAIN,55));
            graphics2D.drawString(LVL,399,140);
        } else {
            graphics2D.setFont(new Font("BrutaGlbExtended-Bold",Font.PLAIN,37));
            graphics2D.drawString(LVL,401,137);
        }
        TOP = TOP.replace("#","");
        graphics2D.setFont(new Font("BrutaGlbExtended-Bold",Font.PLAIN,55));
        graphics2D.drawString("#",581,140);
        if (TOP.length() < 4) {
            graphics2D.drawString(TOP,633,140);
        } else {
            graphics2D.setFont(new Font("BrutaGlbExtended-Bold",Font.PLAIN,30));
            graphics2D.drawString(TOP,627,137);
        }
    }

    private static void drawNickName(Graphics2D graphics2D, String nick) {
        graphics2D.setFont(new Font("BrutaGlbExtended-Thin",Font.BOLD,30));
        if (nick.length() > 25) {
            nick = nick.substring(0,25);
            nick +="...";
        }
        graphics2D.drawString(nick,340,37);

    }

    private static void drawRank(Graphics2D graphics2D, String lvl) { // Отрисовка ранга
        try {
            int width = 276;
            int height = 266;
            int x = -50;
            int y = -40;
            /*File file = new File("DiscordRanks\\rank\\"+getFileRank(lvl));*/
            String f = getFileRank(lvl);
            if (f.equals("10.png") || f.equals("15.png") || f.equals("19.png") ||
                    f.equals("25.png") || f.equals("27.png")) {
                width = 226;         // Уменьшаем и сдвигаем вправо и вниз
                height = 218;
                y += 20;
                x += 20;
            }
            if (f.equals("13.png") || f.equals("14.png") || f.equals("20.png")) {
                y -= 5;            // Поднимаем вверх
            }
            if (f.equals("21.png") || f.equals("22.png") || f.equals("26.png")) {
                width = 226;  // Уменьшаем и сдвигаем вправо(не много меньше) и вниз
                height = 218;
                y += 20;
                x += 15;
            }
            if (f.equals("24.png")) {
                y += 7; // Поднимаем вверх
            }
            if (f.equals("28.png")) {
                width = 200;  // Уменьшаем и сдвигаем вправо(не много больше) и вниз
                height = 193;
                y += 20;
                x += 40;
            }
            if (f.equals("29.png") || f.equals("30.png") || f.equals("31.png")) {
                width = 200;  // Уменьшаем и сдвигаем вправо(не много больше) и вниз (не много больше)
                height = 193;
                y += 40;
                x += 40;
            }
            BufferedImage rank = ImageIO.read(getImgFile("DiscordRanks/rank/"+f));
            rank = reSize(rank,width,height);
            graphics2D.drawImage(rank,x,y,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage reSize(BufferedImage bufferedImage,int width,int height){

        BufferedImage image = new BufferedImage(width,height,bufferedImage.getType());

        Graphics2D graphics2D = image.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(bufferedImage,0,0,width,height,null);
        graphics2D.dispose();

        return image;
    }

    private static BufferedInputStream getImgFile(String path) {
        return (BufferedInputStream) Objects.requireNonNull(DrawRankCard.class.getClassLoader().getResourceAsStream(path));
    }

    public static String getNameRank(String lvl) {
        String id = getFileRank(lvl);
        return nameRanks[Integer.parseInt(id.substring(0,id.indexOf(".png")))-1];
    }

    public static String getFileRank(String lvl) {

        int iLVL = Integer.parseInt(lvl);

        String file = "";

        if (iLVL >= ranks[0]) {
            file += "31.png";
        } else if (iLVL >= ranks[1]) {
            file += "30.png";
        } else if (iLVL >= ranks[2]) {
            file += "29.png";
        } else if (iLVL >= ranks[3]) {
            file += "28.png";
        } else if (iLVL >= ranks[4]) {
            file += "27.png";
        } else if (iLVL >= ranks[5]) {
            file += "26.png";
        } else if (iLVL >= ranks[6]) {
            file += "25.png";
        } else if (iLVL >= ranks[7]) {
            file += "24.png";
        } else  if (iLVL >= ranks[8]) {
            file += "23.png";
        } else if (iLVL >= ranks[9]) {
            file += "22.png";
        } else if (iLVL >= ranks[10]) {
            file += "21.png";
        } else if (iLVL >= ranks[11]) {
            file += "20.png";
        } else if (iLVL >= ranks[12]) {
            file += "19.png";
        } else if (iLVL >= ranks[13]) {
            file += "18.png";
        } else if (iLVL >= ranks[14]) {
            file += "17.png";
        } else if (iLVL >= ranks[15]) {
            file += "16.png";
        } else if (iLVL >= ranks[16]) {
            file += "15.png";
        } else if (iLVL >= ranks[17]) {
            file += "14.png";
        } else if (iLVL >= ranks[18]) {
            file += "13.png";
        } else if (iLVL >= ranks[19]) {
            file += "12.png";
        } else if (iLVL >= ranks[20]) {
            file += "11.png";
        } else if (iLVL >= ranks[21]) {
            file += "10.png";
        } else if (iLVL >= ranks[22]) {
            file += "9.png";
        } else if (iLVL >= ranks[23]) {
            file += "8.png";
        } else if (iLVL >= ranks[24]) {
            file += "7.png";
        } else if (iLVL >= ranks[25]) {
            file += "6.png";
        } else if (iLVL >= ranks[26]) {
            file += "5.png";
        } else if (iLVL >= ranks[27]) {
            file += "4.png";
        } else if (iLVL >= ranks[28]) {
            file += "3.png";
        } else if (iLVL >= ranks[29]) {
            file += "2.png";
        } else {
            file += "1.png";
        }

        return file;
    }


    public static class isBotException extends Exception {

    }
}
