/*
package BackEND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Check_Rank {

    private static final String URL = "https://rlstats.net/profile/"; // Образец ссылки от куда берем информацию о ММР

    private static volatile int _try = 0;

    private static byte seasonReward = -1;

    private static String platform(String platform, String nick) throws isNotNickOrPlatformException { // Возращает аргументы в конец ссылки

        platform = platform.toLowerCase();

        switch (platform) {
            case "epic":
                return "Epic/" + nick; // Добавляем аргументы Эпика
            case "xbox":
                return "Xbox/" + nick; // Добавляем аргументы ХБокса
            case "ps4":
                return "PS4/" + nick; // Добавляем аргументы ПС4
            case "steam":
                if (nick.contains("https:")) {
                    nick = nick.substring(nick.lastIndexOf("/")+1);
                }
                return "Steam/" + nick;
            default:
                throw new isNotNickOrPlatformException(); // Если нет подобной платформы то пробрасываем исключение.
        }
    }


    public static String[] mmr(String platform, String nick) throws isNotNickOrPlatformException, IOException, infinityCycleException { // Возращает массив строк с ммр
        URL url = new URL(URL + platform(platform, nick)); // Создаем ссылку на проверку ММР
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())); // Открываем поток чтения сайта и открываем поток ссылки.
        String[] points = new String[8]; // Создаем пустой массив строк


        while (reader.ready()) {
            String txt = reader.readLine();
            if (txt.contains("/images/ranks/s15rl")) {
                txt = txt.substring(txt.indexOf("rl")+2,txt.indexOf("."));
                seasonReward = Byte.parseByte(txt);
            }
            if (txt.contains("[new Date")) { // Ищем нужную строку в коде сайта.
                points = takeMMR(txt); // Заполняем массив строками ММР
            }
        }
        reader.close();
        while (true) {
            _try++;
            if (_try > 10) { // Если 10 попыток сделано то пробрасываем исключение, и о бесконечном цикле.
                _try = 0;
                throw new infinityCycleException();
            }
            boolean restart = false;
            if (isEmpty(points)) {  // проверяем на пустоту строки
                restart = true; // Если есть хотя бы одна строка пустая ставить restart = true
            }
            if (!restart) { // Если restart == false то останавливаем цикл.
                break;
            } else {
                points = mmr(platform, nick); // Иначе пытаемся ещё раз прочитать сайт
            }
        }
        byte _tryS = 0;
        while (true) {
            if (_tryS == 5) {
                throw new infinityCycleException();
            }
            if (seasonReward != -1) { break; }
            seasonReward = onlyCheckSeasonReward(platform,nick);
            _tryS++;
        }

        return points; // Возращаем массив со строками ММР
    }

    private static byte onlyCheckSeasonReward(String platform, String nick) throws isNotNickOrPlatformException, IOException {
        URL url = new URL(URL + platform(platform, nick));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        while (true) {
            String txt = reader.readLine();
            if (txt.contains("/images/ranks/s15rl")) {
                txt = txt.substring(txt.indexOf("rl")+2,txt.indexOf("."));
                return Byte.parseByte(txt);
            }
        }
    }

    private static boolean isEmpty(String s) { // Пустая ли строка?
        if (s == null) {
            return true;
        }
        return false;
    }

    private static boolean isEmpty(String[] s) { // Пустые строки? Если есть хотя-бы одна строка пустая возращает true
        boolean[] points = new boolean[s.length];
        for (int i = 0; i < points.length; i++) { // Заполняем буллены true or false где true означает что строка пустая.
            points[i] = isEmpty(s[i]);
        }
        for (boolean p : points) {
            if (p) {
                return true;
            }
        }
        return false;
    }

    private static String[] takeMMR(String s) { // Обрезаем строку и оставляем только очки ММР
        s = s.substring(s.indexOf(",") + 2, s.indexOf("]") - 1);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(s.split(", ")));
        list.remove(3);

        String[] strings = list.toArray(new String[0]);

        return strings;
    }

    public static String name_rank(String mmr, type_match match) {
        String rank = null;

        switch (match) {
            case oneS:
                rank = rank1v1(mmr);
                break;
            case doubles:
                rank = rank2v2(mmr);
                break;
            case standart:
                rank = rank3v3(mmr);
                break;
            case hoops:
                rank = rankHoops(mmr);
                break;
            case rumble:
                rank = rankRumble(mmr);
                break;
            case dropShot:
                rank = rankDropShot(mmr);
                break;
            case snowDay:
                rank = rankSnowDay(mmr);
                break;
            case tournament:
                rank = rankTournament(mmr);
        }

        return rank;
    }

    public static String getSeasonReward() {

        String reward = null;

        switch(seasonReward) {
            case 0:
                reward = "Unranked";
                seasonReward = -1;
                break;

            case 1:
                reward = "Bronze";
                seasonReward = -1;
                break;
            case 2:
                reward = "Silver";
                seasonReward = -1;
                break;
            case 3:
                reward = "Gold";
                seasonReward = -1;
                break;
            case 4:
                reward = "Platinum";
                seasonReward = -1;
                break;
            case 5:
                reward = "Diamond";
                seasonReward = -1;
                break;
            case 6:
                reward = "Champion";
                seasonReward = -1;
                break;
            case 7:
                reward = "Grand-Champion";
                seasonReward = -1;
                break;
            case 8:
                reward = "Supersonic-Legend";
                seasonReward = -1;
                break;
        }

        return reward;
    }

    private static String rank1v1(String mmr) { // Ранг в 1vs1
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmr1S.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmr1S[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmr1S[i][j].length; e++) {

                    if (points > mmr1S[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rank2v2(String mmr) { // Rank 2vs2
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmr2S.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmr2S[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmr2S[i][j].length; e++) {

                    if (points > mmr2S[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rank3v3(String mmr) { // Rank 3vs3
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmr3S.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmr3S[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmr3S[i][j].length; e++) {

                    if (points > mmr3S[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rankHoops(String mmr) { // Rank Hoops
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmrHoops.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmrHoops[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmrHoops[i][j].length; e++) {

                    if (points > mmrHoops[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rankRumble(String mmr) { // rank Rumble
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmrRumble.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmrRumble[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmrRumble[i][j].length; e++) {

                    if (points > mmrRumble[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rankDropShot(String mmr) { // RankDropShot
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmrDropShot.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmrDropShot[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmrDropShot[i][j].length; e++) {

                    if (points > mmrDropShot[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rankSnowDay(String mmr) { // Rank SnowDay
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmrSnowDay.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmrSnowDay[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmrSnowDay[i][j].length; e++) {

                    if (points > mmrSnowDay[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    private static String rankTournament(String mmr) { // Rank Tournament
        int points = Integer.parseInt(mmr);
        String rank = null;
        boolean b = false;
        for (int i = 0; i < mmrTournament.length; i++) {
            if (b) {break;}
            for (int j = 0; j < mmrTournament[i].length; j++) {
                if (b) {break;}
                for (int e = 0; e < mmrTournament[i][j].length; e++) {

                    if (points > mmrTournament[i][j][e]) {
                        rank = RANKS[i][j][e];
                    } else {
                        b = true;
                    }

                }
            }
        }
        return rank;
    }

    static class isNotNickOrPlatformException extends Exception { // Исключение если ник или платформа не существует.

    }

    static class infinityCycleException extends Exception { // Исключение если возникает бесконечный цикл.

    }

    public enum type_match {
        oneS,
        doubles,
        standart,
        tournament,
        hoops,
        rumble,
        snowDay,
        dropShot
    }

    private static final String[][][] RANKS = {
            {      {"Bronze I Division I",
                    "Bronze I Division II",
                    "Bronze I Division III",
                    "Bronze I Division IV",
            },     {"Bronze II Division I",
                    "Bronze II Division II",
                    "Bronze II Division III",
                    "Bronze II Division IV",
            },     {"Bronze III Division I",
                    "Bronze III Division II",
                    "Bronze III Division III",
                    "Bronze III Division IV",
            },},
            {       {"Silver I Division I",
                    "Silver I Division II",
                    "Silver I Division III",
                    "Silver I Division IV",
            },     {"Silver II Division I",
                    "Silver II Division II",
                    "Silver II Division III",
                    "Silver II Division IV",
            },     {"Silver III Division I",
                    "Silver III Division II",
                    "Silver III Division III",
                    "Silver III Division IV",
            },},
            {       {"Gold I Division I",
                    "Gold I Division II",
                    "Gold I Division III",
                    "Gold I Division IV",
            },     {"Gold II Division I",
                    "Gold II Division II",
                    "Gold II Division III",
                    "Gold II Division IV",
            },     {"Gold III Division I",
                    "Gold III Division II",
                    "Gold III Division III",
                    "Gold III Division IV",
            },},
            {       {"Platinum I Division I",
                    "Platinum I Division II",
                    "Platinum I Division III",
                    "Platinum I Division IV",
            },      {"Platinum II Division I",
                    "Platinum II Division II",
                    "Platinum II Division III",
                    "Platinum II Division IV",
            },      {"Platinum III Division I",
                    "Platinum III Division II",
                    "Platinum III Division III",
                    "Platinum III Division IV",
            },},
            {       {"Diamond I Division I",
                    "Diamond I Division II",
                    "Diamond I Division III",
                    "Diamond I Division IV",
            },      {"Diamond II Division I",
                    "Diamond II Division II",
                    "Diamond II Division III",
                    "Diamond II Division IV",
            },      {"Diamond III Division I",
                    "Diamond III Division II",
                    "Diamond III Division III",
                    "Diamond III Division IV",
            },},
            {       {"Champion I Division I",
                    "Champion I Division II",
                    "Champion I Division III",
                    "Champion I Division IV",
            },      {"Champion II Division I",
                    "Champion II Division II",
                    "Champion II Division III",
                    "Champion II Division IV",
            },      {"Champion III Division I",
                    "Champion III Division II",
                    "Champion III Division III",
                    "Champion III Division IV",
            },},
            {      {"Grand-Champion I Division I",
                    "Grand-Champion I Division II",
                    "Grand-Champion I Division III",
                    "Grand-Champion I Division IV",
            },     {"Grand-Champion II Division I",
                    "Grand-Champion II Division II",
                    "Grand-Champion II Division III",
                    "Grand-Champion II Division IV",
            },     {"Grand-Champion III Division I",
                    "Grand-Champion III Division II",
                    "Grand-Champion III Division III",
                    "Grand-Champion III Division IV",
            },},
            {     {"Supersonic Legend Division",
            }, {}, {}}};


    private static final int[][][] mmr1S = {
            {
            {0, 117, 131, 146},
            {149, 160, 183, 197},
            {207, 219, 240, 257},
    }, {
            {264, 279, 298, 317},
            {323, 339, 358, 377},
            {387, 399, 418, 437},
    }, {
            {455, 459, 478, 497},
            {513, 519, 538, 557},
            {567, 579, 598, 617},
    }, {
            {633, 639, 658, 677},
            {686, 699, 718, 737},
            {746, 759, 778, 797},
    }, {
            {807, 819, 838, 857},
            {866, 879, 898, 917},
            {925, 939, 958, 977},
    }, {
            {987, 999, 1018, 1037},
            {1047, 1060, 1078, 1097},
            {1106, 1120, 1143, 1157},
    }, {
            {1167, 1180, 1202, 1217},
            {1225, 1242, 1258, 1277},
            {1288, 1300, 1318, 1337},
    }, {
            {1344}}};

    private static final int[][][] mmr2S = {
    {
            {0, 125, 155, 172},
            {187, 201, 218, 237},
            {245, 259, 278, 297},
    },{
            {309, 319, 338, 357},
            {369, 379, 398, 417},
            {430, 439, 458, 477},
    },{
            {489, 499, 518, 537},
            {550, 559, 578, 597},
            {614, 624, 648, 672},
    },{
            {695, 704, 728, 752},
            {772, 784, 808, 832},
            {853, 864, 888, 912},
    },{
            {935, 944, 968, 992},
            {1015, 1031, 1058, 1087},
            {1113, 1130, 1158, 1187},
    },{
            {1214, 1232, 1258, 1287},
            {1314, 1332, 1362, 1387},
            {1414, 1432, 1458, 1487},
    },{
            {1515, 1535, 1568, 1602},
            {1631, 1655, 1690, 1722},
            {1753, 1774, 1808, 1842},
    },{
            {1866}}};

    private static final int[][][] mmr3S = {
            {
            {0, 125, 155, 172},
            {189, 200, 218, 237},
            {246, 259, 278, 297},
    },{
            {311, 319, 338, 357},
            {369, 379, 398, 417},
            {428, 439, 458, 477},
    },{
            {489, 499, 518, 537},
            {550, 559, 578, 597},
            {615, 624, 648, 672},
    },{
            {694, 704, 728, 752},
            {773, 784, 808, 832},
            {852, 864, 888, 912},
    },{
            {935, 950, 978, 1007},
            {1035, 1050, 1078, 1107},
            {1135, 1150, 1178, 1207},
    },{
            {1235, 1252, 1282, 1307},
            {1334, 1352, 1382, 1407},
            {1433, 1451, 1478, 1507},
    },{
            {1535, 1555, 1588, 1622},
            {1648, 1675, 1708, 1742},
            {1771, 1795, 1828, 1862},
    },{
            {1885}}};

    private static final int[][][] mmrDropShot = {
    {
            {0},
            {154, 168},
            {163, 182, 200, 217},
    },{
            {222, 244, 258, 277},
            {286, 299, 318, 337},
            {344, 359, 378, 397},
    },{
            {408, 419, 438, 457},
            {463, 479, 498, 517},
            {531, 539, 558, 577},
    },{
            {595, 599, 618, 637},
            {655, 659, 678, 697},
            {710, 719, 738, 757},
    },{
            {775, 779, 798, 817},
            {832, 839, 858, 877},
            {892, 899, 918, 937},
    },{
            {945, 959, 978, 997},
            {1009, 1019, 1038, 1057},
            {1067, 1081, 1098, 1117},
    },{
            {1124, 1139, 1158, 1178},
            {1187, 1201, 1220, 1243},
            {1268, 1285, 1300},
    },{
            {1305}}};

    private static final int[][][] mmrHoops = {
    {
            {0},
            {154, 168},
            {163, 182, 200, 217},
    },{
            {222, 244, 258, 277},
            {286, 299, 318, 337},
            {344, 359, 378, 397},
    },{
            {408, 419, 438, 457},
            {463, 479, 498, 517},
            {531, 539, 558, 577},
    },{
            {595, 599, 618, 637},
            {655, 659, 678, 697},
            {710, 719, 738, 757},
    },{
            {775, 779, 798, 817},
            {832, 839, 858, 877},
            {892, 899, 918, 937},
    },{
            {945, 959, 978, 997},
            {1009, 1019, 1038, 1057},
            {1067, 1081, 1098, 1117},
    },{
            {1124, 1139, 1158, 1178},
            {1187, 1201, 1220, 1243},
            {1268, 1285, 1300},
    },{
            {1305}}};

    private static final int[][][] mmrRumble = {
    {
            {0, 119, 135},
            {156, 167, 180, 197},
            {203, 222, 239, 257},
    },{
            {270, 280, 303, 317},
            {326, 340, 358, 377},
            {386, 399, 418, 437},
    },{
            {447, 459, 478, 497},
            {511, 519, 538, 557},
            {573, 579, 598, 617},
    },{
            {633, 639, 658, 677},
            {691, 699, 718, 737},
            {751, 759, 778, 797},
    },{
            {811, 819, 838, 857},
            {871, 879, 898, 917},
            {933, 944, 968, 992},
    },{
            {1015, 1024, 1048, 1072},
            {1091, 1105, 1128, 1152},
            {1168, 1185, 1209, 1232},
    },{
            {1254, 1265, 1289, 1312},
            {1331, 1344, 1368, 1392},
            {1414, 1431, 1458, 1493},
    },{
            {1516}}};

    private static final int[][][] mmrSnowDay = {
    {
            {0,132},
            {145, 165, 183, 197},
            {215, 222, 240, 257},
    },{
            {266, 280, 298, 317},
            {325, 339, 358, 377},
            {388, 399, 418, 437},
    },{
            {455, 459, 478, 497},
            {515, 519, 538, 557},
            {568, 579, 598, 617},
    },{
            {635, 639, 658, 677},
            {690, 699, 718, 737},
            {755, 759, 778, 797},
    },{
            {815, 819, 838, 857},
            {868, 879, 898, 917},
            {928, 939, 958, 977},
    },{
            {988, 999, 1018, 1037},
            {1046, 1059, 1078, 1097},
            {1104, 1120, 1139, 1157},
    },{
            {1163, 1180, 1198, 1218},
            {1224, 1239, 1263, 1281},
            {1295, 1301, 1318, 1347},
    },{
            {1356}}};

    private static final int[][][] mmrTournament = {
    {
            {0, 125, 148, 172},
            {183, 199, 218, 237},
            {241, 259, 278, 296},
    },{
            {301, 319, 337, 356},
            {361, 377, 396, 416},
            {421, 437, 456, 476},
    },{
            {481, 497, 516, 536},
            {540, 557, 576, 596},
            {601, 624, 648, 672},
    },{
            {680, 704, 728, 751},
            {760, 784, 808, 832},
            {840, 864, 888, 912},
    },{
            {920, 949, 978, 1007},
            {1020, 1049, 1078, 1106},
            {1120, 1149, 1178, 1207},
    },{
            {1220, 1249, 1278, 1306},
            {1320, 1349, 1378, 1406},
            {1420, 1449, 1478, 1507},
    },{
            {1520, 1554, 1588, 1622},
            {1641, 1674, 1708, 1742},
            {1761, 1795, 1829, 1862},
    },{
            {1881}}};
}*/
