/*
package BackEND;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class DrawCard {

    private static final String FILE_RANKS = "ImageRanks\\";

    private static final int Rwidth = 152;
    private static final int Rheight = 152;

    private static Font standard_font_rank = new Font("Bauhaus 93",Font.PLAIN,33);
    private static Font small_font_rank = new Font("Bauhaus 93",Font.PLAIN,19);
    private static Font div_font_rank = new Font("Consolas",Font.PLAIN,19);

    public static void main(String[] args) throws IOException {

        byte[] buffer = getPanel("Bronze III Division IV","Silver III Division IV","Gold III Division IV",
                "Diamond III Division IV","Diamond III Division IV",
                "Champion III Division IV", "Grand-Champion III Division IV",
                "Supersonic Legend Division", "487",
                "489","794","878","178","987",
                "749","877","Diamond","Epic","Katya_Wyverno");
        */
/*BufferedImage panel = ImageIO.read(new File("src\\main\\java\\ImageRanks\\Panel\\mainPanel.png"));
        Graphics2D graphics2D = panel.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.*//*


    }

    public static byte[] getPanel(String oneS,String doubles,String standard,String tournament, // Создаем картинку и возращаем
                                  String hoops, String rumble, String dropShot,                 // Массив байтов
                                  String snowDay, String mmrOneS, String mmrDoubles, String mmrStandard,
                                  String mmrTournament, String mmrHoops, String mmrRumble,String mmrDropShot,
                                  String mmrSnowDay, String SeasonReward, String platform,String nickname) throws IOException {
        File fileOneS = new File(getFileRank(oneS));
        File fileDoubles = new File(getFileRank(doubles));
        File fileStandard = new File(getFileRank(standard));
        File fileTournament = new File(getFileRank(tournament));
        File fileHoops = new File(getFileRank(hoops));
        File fileRumble = new File(getFileRank(rumble));
        File fileDropShot = new File(getFileRank(dropShot));
        File fileSnowDay = new File(getFileRank(snowDay));
        File fileReward = new File(getFileSeasonReward(SeasonReward));

        BufferedImage panel = ImageIO.read(new File("ImageRanks\\Panel\\mainPanel.png"));
        BufferedImage iOneS = ImageIO.read(fileOneS);
        BufferedImage iDoubles = ImageIO.read(fileDoubles);
        BufferedImage iStandard = ImageIO.read(fileStandard);
        BufferedImage iTournament = ImageIO.read(fileTournament);
        BufferedImage iHoops = ImageIO.read(fileHoops);
        BufferedImage iRumble = ImageIO.read(fileRumble);
        BufferedImage iDropShot = ImageIO.read(fileDropShot);
        BufferedImage iSnowDay = ImageIO.read(fileSnowDay);
        BufferedImage iReward = ImageIO.read(fileReward);


        iOneS = reSize(iOneS);
        iDoubles = reSize(iDoubles);
        iStandard = reSize(iStandard);
        iTournament = reSize(iTournament);
        iHoops = reSize(iHoops);
        iRumble = reSize(iRumble);
        iDropShot = reSize(iDropShot);
        iSnowDay = reSize(iSnowDay);
        iReward = reSize(iReward);




        Graphics2D gPanel2D = panel.createGraphics();
        gPanel2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        platform = firstUpperCase(platform);

        if (nickname.length() > 15) {
            nickname = nickname.substring(0,15);
        }
        String up = platform + ": " + nickname;






        gPanel2D.setFont(new Font("Bauhaus 93",Font.PLAIN,71));

        gPanel2D.drawString(up,87,95); // Platform: nickname
        gPanel2D.setFont(new Font("Bauhaus 93",Font.PLAIN,33));
        drawRank(gPanel2D,oneS,5,400); // Отрисовка названий рангов
        drawRank(gPanel2D,doubles,227,400);
        drawRank(gPanel2D,standard,453,400);
        drawRank(gPanel2D,tournament,669,400);
        drawRank(gPanel2D,hoops,5,614);
        drawRank(gPanel2D,rumble,227,614);
        drawRank(gPanel2D,dropShot,453,614);
        drawRank(gPanel2D,snowDay,669,614);


        gPanel2D.setFont(new Font("Bauhaus 93",Font.PLAIN,33));
        gPanel2D.drawString(mmrOneS,41,452); // Отрисовка очков ММР
        gPanel2D.drawString(mmrDoubles,263,452);
        gPanel2D.drawString(mmrStandard,485,452);
        gPanel2D.drawString(mmrTournament,700,452);
        gPanel2D.drawString(mmrHoops,41,664);
        gPanel2D.drawString(mmrRumble,263,664);
        gPanel2D.drawString(mmrDropShot,485,664);
        gPanel2D.drawString(mmrSnowDay,700,664);


        gPanel2D.drawImage(iOneS,-3,245,null); // Отрисовка картинок рангов
        gPanel2D.drawImage(iDoubles,222,245,null);
        gPanel2D.drawImage(iStandard,441,245,null);
        gPanel2D.drawImage(iTournament,655,245,null);
        gPanel2D.drawImage(iHoops,-3,460,null);
        gPanel2D.drawImage(iRumble,222,460,null);
        gPanel2D.drawImage(iDropShot,441,460,null);
        gPanel2D.drawImage(iSnowDay,655,460,null);
        gPanel2D.drawImage(iReward,-2,106,null);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        */
/*ImageIO.write(panel,"png",new File("image.png"));*//*

        ImageIO.write(panel,"jpg",byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private static BufferedImage reSize(BufferedImage bufferedImage){

        BufferedImage image = new BufferedImage(Rwidth,Rheight,bufferedImage.getType());

        Graphics2D graphics2D = image.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(bufferedImage,0,0,Rwidth,Rheight,null);
        graphics2D.dispose();

        return image;
    }

    private static String getFileRank(String rank) { // Геттер путя к файла
        String name_rank = rank.substring(0,rank.indexOf(" ")).toLowerCase();
        String level_rank = rank.substring(rank.indexOf(" ")+1,rank.indexOf("Division")-1);
        switch (name_rank) {
            case "bronze":
                return FILE_RANKS+"Bronze\\Bronze"+getFileLevelRank(level_rank);
            case "silver":
                return FILE_RANKS+"Silver\\Silver"+getFileLevelRank(level_rank);
            case "gold":
                return FILE_RANKS+"Gold\\Gold"+getFileLevelRank(level_rank);
            case "platinum":
                return FILE_RANKS+"Platinum\\Platinum"+getFileLevelRank(level_rank);
            case "diamond":
                return FILE_RANKS+"Diamond\\Diamond"+getFileLevelRank(level_rank);
            case "champion":
                return FILE_RANKS+"Champion\\Champion"+getFileLevelRank(level_rank);
            case "grand-champion":
                return FILE_RANKS+"Grand Champion\\GrandChampion"+getFileLevelRank(level_rank);
            default:
                return FILE_RANKS+"Supersonic Legend\\SupersonicLegend.png";

        }
    }

    private static String getFileLevelRank(String level) { // Геттер окончание файлы (уровень ранга)
        switch (level) {
            case "I":
                return "1.png";
            case "II":
                return "2.png";
            default:
                return "3.png";
        }
    }

    private static String getFileSeasonReward(String reward) {
        switch (reward) {
            case "Unranked":
                return FILE_RANKS+"SeasonsReward\\unranked.png";
            case "Bronze":
                return FILE_RANKS+"SeasonsReward\\bronze.png";
            case "Silver":
                return FILE_RANKS+"SeasonsReward\\silver.png";
            case "Gold":
                return FILE_RANKS+"SeasonsReward\\gold.png";
            case "Platinum":
                return FILE_RANKS+"SeasonsReward\\platinum.png";
            case "Diamond":
                return FILE_RANKS+"SeasonsReward\\diamond.png";
            case "Champion":
                return FILE_RANKS+"SeasonsReward\\champion.png";
            case "Grand-Champion":
                return FILE_RANKS+"SeasonsReward\\grandchampion.png";
            default:
                return FILE_RANKS+"SeasonsReward\\supersoniclegend.png";
        }
    }

    private static String getNameRank(String rank) {

        rank = rank.substring(0,rank.indexOf("Division")-1);

        return rank;
    }

    private static String getDivision(String rank) {

        rank = rank.substring(rank.indexOf("Division")+8);

        return rank;
    }

    private static String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return ""; //или return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static void drawRank(Graphics2D graphics2D,String rank, int x, int y) {

        String dRank = getNameRank(rank);

        int length = dRank.length();
        */
/*System.out.println(dRank +": " +  dRank.length());*//*


        if (length < 10) {
            x +=12;
        }
        if (length > 11) {
            x -= 5;
        }
        boolean font = false;

        if (length > 16) {
            graphics2D.setFont(small_font_rank);
            font = true;
        }

        if (x > 500) {
            x -= 20;
        }

        if (x > 200 && x < 300 && length < 11) {
            x+= 20;
        }

        if (x > 400 && x < 600 && length < 11) {
            x+= 20;
        }

        String division =  getDivision(rank);

        graphics2D.drawString(dRank,x,y);


        if (division.length() > 0) {
            font = true;
            graphics2D.setFont(div_font_rank);
            y += 20;
            x += 5;
            graphics2D.drawString("Division:" + division,x,y);
        }

        if (font) {
            graphics2D.setFont(standard_font_rank);
        }
    }

}
*/
