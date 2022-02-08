package BackEND;

import net.dv8tion.jda.api.JDA;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RankDiscordChecker extends Thread {

    private static final String dataBase = "ranks.txt";

    private static JDA jda = App.getJDA();

    private static final String lvl = "⸐⸑";
    private static final String xp = "⸎⸕";
    private static final String nick = "⸔⸕";
    private static final String crystal = "✔\uD83D\uDDF8";
    private static final String rank = "\uD83C\uDFC6\uD83C\uDFC5";

    private static final int[] LVLranks = inversionSorting(DrawRankCard.getRanks());
    private static final String[] NAMERanks = DrawRankCard.getNameRanks();
    private static final int[] crystalBonusForUpLvl = {30,70,150,300,400,720,900,1200,1500,1990,
                                                       2400,2800,3600,4200,4700,5200,5900,6700,
                                                       7500,8100,9000,10000,11500,12900,14000,17200,
                                                       20000,25000,31900,40000,70000};

    private static final int xp_for_voice = 173;
    private static final int xp_for_chat = 6;

    private static boolean add_xp_voice = true;

    private static ArrayList<AccountRank> ratingList = new ArrayList<>();

    static {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dataBase))));
            while (true) {
                String string = reader.readLine();
                if (string == null) {
                    reader.close();
                    break;
                }
                String id = string.substring(0,string.indexOf(RankDiscordChecker.lvl)).trim();
                String lvl = string.substring(string.indexOf(RankDiscordChecker.lvl)+2,string.indexOf(RankDiscordChecker.xp)).trim();
                String xp = string.substring(string.indexOf(RankDiscordChecker.xp)+2,string.indexOf(crystal)).trim();
                String cry = string.substring(string.indexOf(crystal)+3,string.indexOf(RankDiscordChecker.rank)).trim();
                String rank = string.substring(string.indexOf(RankDiscordChecker.rank)+4,string.indexOf(RankDiscordChecker.nick)).trim();
                String nick = string.substring(string.indexOf(RankDiscordChecker.nick)+2);
                ratingList.add(new AccountRank(nick,id,rank,Integer.parseInt(lvl),Integer.parseInt(xp),Integer.parseInt(cry)));

            }
            sortRating(ratingList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }

    public RankDiscordChecker() {
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (add_xp_voice) {
                    int category_size = jda.awaitReady().getCategories().size();

                    for (int i = 0; i < category_size; i++) {
                        if (jda.awaitReady().getCategories().get(i).toString().contains("AFK")) {
                            break;
                        }
                        int chat_size = jda.awaitReady().getCategories().get(i).getVoiceChannels().size();
                        for (int j = 0; j < chat_size; j++) {
                            int voice_size = jda.awaitReady().
                                    getCategories().get(i)
                                    .getVoiceChannels().get(j)
                                    .getMembers().size();
                            for (int e = 0; e < voice_size; e++) {
                                String id = jda.awaitReady()
                                        .getCategories().get(i)
                                        .getVoiceChannels().get(j)
                                        .getMembers().get(e).getId();
                                String name = jda.awaitReady()
                                              .getCategories().get(i)
                                              .getVoiceChannels().get(j)
                                              .getMembers().get(e).getEffectiveName();
                                boolean bot = jda.awaitReady()
                                        .getCategories().get(i)
                                        .getVoiceChannels().get(j)
                                        .getMembers().get(e).getUser().isBot();
                                int isHasAccount = isHasAccount(id);
                                if (isHasAccount != -1 && !bot) { // -1 == false || Если не -1 то аккаунт уже есть в списке.
                                    AccountRank accountRank = ratingList.get(isHasAccount);
                                    accountRank.addXp(xp_for_voice,name);



                                } else if (!bot) {
                                    String nick = jda.awaitReady()
                                            .getCategories().get(i)
                                            .getVoiceChannels().get(j)
                                            .getMembers().get(e).getEffectiveName();
                                    addNewAccount(new AccountRank(nick,id,NAMERanks[0], 0,0,0));
                                }
                            }
                        }
                    }
                }
                writeList();
                Thread.sleep(600000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void setAdd_xp_voice(boolean b) {
        add_xp_voice = b;
    }

    public static int getSizeRating() {
        return ratingList.size();
    }

    private static int isHasAccount(String id) {
        for (int i = 0; i < ratingList.size(); i++) {
            String check_id = ratingList.get(i).getId();
            if (id.equals(check_id)) {
                return i;
            }
        }
        return -1; // Обозначает что это false
    }

    private static int[] inversionSorting(int[] array) {
        int[] fArray = new int[31];
        fArray[0] = 0;

        int[] nArray = new int[30];

        for (int i = 0; i < nArray.length; i++) {
            nArray[i] = array[i];
        }

        Arrays.sort(nArray);

        for (int i = 1; i < fArray.length; i++) {
            fArray[i] = nArray[i-1];
        }
        return fArray;
    }

    private static void addNewAccount(AccountRank accountRank) {
        ratingList.add(accountRank);
    }

    public static void writeList() { // Запись в дату базу.
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dataBase))));
            for (int i = 0; i < ratingList.size(); i++) {
                String id = ratingList.get(i).getId();
                String lvl = ratingList.get(i).getLvl();
                String xp = ratingList.get(i).getXp();
                String cry = String.valueOf(ratingList.get(i).getCrystal());
                String rank = ratingList.get(i).getRank();
                String nick = ratingList.get(i).getNick();
                if (id.length() < 30) {
                    id = addSpace(id,30);
                }
                if (lvl.length() < 20) {
                    lvl = addSpace(lvl,20);
                }
                if (xp.length() < 20) {
                    xp = addSpace(xp,20);
                }
                if (cry.length() < 10) {
                    cry = addSpace(cry,10);
                }
                writer.write(id+
                        RankDiscordChecker.lvl+lvl+
                        RankDiscordChecker.xp+xp+
                        crystal+cry+
                        RankDiscordChecker.rank+rank+
                        RankDiscordChecker.nick+nick+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sortRating(ArrayList<AccountRank> list) { // Сортировка листа рейтинга
        for (int i = 0; i < list.size(); i++) {
            for (int j = list.size()-1; j > 0; j--) {
                AccountRank rank1 = list.get(j);
                AccountRank rank2 = list.get(j-1);

                if (rank1.isBest(rank2)) {
                    list.set(j-1,rank1);
                    list.set(j,rank2);
                }
            }
        }
    }

    public static String addSpace(String add,int need) { // Добавление пробелов для сохранение файла
        for (int i = add.length(); i < need; i++) {
            add += " ";
        }
        return add;
    }

    private static int getUpRank(int lvl) {
        if (lvl >= LVLranks[30]) {
            return 30;
        }  else if (lvl >= LVLranks[29]) {
            return 29;
        } else if (lvl >= LVLranks[28]) {
            return 28;
        } else if (lvl >= LVLranks[27]) {
            return 27;
        } else if (lvl >= LVLranks[26]) {
            return 26;
        } else if (lvl >= LVLranks[25]) {
            return 25;
        } else if (lvl >= LVLranks[24]) {
            return 24;
        } else if (lvl >= LVLranks[23]) {
            return 23;
        } else if (lvl >= LVLranks[22]) {
            return 22;
        } else if (lvl >= LVLranks[21]) {
            return 21;
        } else if (lvl >= LVLranks[20]) {
            return 20;
        } else if (lvl >= LVLranks[19]) {
            return 19;
        } else if (lvl >= LVLranks[18]) {
            return 18;
        } else if (lvl >= LVLranks[17]) {
            return 17;
        } else if (lvl >= LVLranks[16]) {
            return 16;
        } else if (lvl >= LVLranks[15]) {
            return 15;
        } else if (lvl >= LVLranks[14]) {
            return 14;
        } else if (lvl >= LVLranks[13]) {
            return 13;
        } else if (lvl >= LVLranks[12]) {
            return 12;
        } else if (lvl >= LVLranks[11]) {
            return 11;
        } else if (lvl >= LVLranks[10]) {
            return 10;
        } else if (lvl >= LVLranks[9]) {
            return 9;
        } else if (lvl >= LVLranks[8]) {
            return 8;
        } else if (lvl >= LVLranks[7]) {
            return 7;
        } else if (lvl >= LVLranks[6]) {
            return 6;
        } else if (lvl >= LVLranks[5]) {
            return 5;
        } else if (lvl >= LVLranks[4]) {
            return 4;
        } else if (lvl >= LVLranks[3]) {
            return 3;
        } else if (lvl >= LVLranks[2]) {
            return 2;
        } else if (lvl >= LVLranks[1]) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void giveAllBonus() {
        for (int i = 0; i < ratingList.size(); i++) {
            AccountRank member = ratingList.get(i);
            int id_rank = getUpRank(member.getIntegerLvl());
            for (int j = 0; j <= id_rank; j++) {
                member.addCrystal(crystalBonusForUpLvl[j]);
            }
        }
    }

    public static void addXp(String id,String nick) {
        for (int i = 0; i < ratingList.size(); i++) {
            AccountRank accountRank = ratingList.get(i);
            if (accountRank.getId().equals(id)) {
                accountRank.addXp(xp_for_chat,nick);
                return;
            }
        }
        AccountRank accountRank = new AccountRank(nick,id,NAMERanks[0], 0,0,0);
        addNewAccount(accountRank);
    }

    public static void removeXp(String id) {
        for (int i = 0; i < ratingList.size(); i++) {
            AccountRank accountRank = ratingList.get(i);
            if (accountRank.getId().equals(id)) {
                accountRank.removeXp(xp_for_chat);
                return;
            }
        }
    }

    public static String getRank(String id, String nick) {

        String info = null;

        int id_list = isHasAccount(id);

        if (id_list != -1) {

            AccountRank accountRank = ratingList.get(id_list);

            info ="#" + (id_list+1) + " " + accountRank.toString();

        } else {

            AccountRank accountRank = new AccountRank(nick,id,NAMERanks[0], 0,0,0);

            addNewAccount(accountRank);

            info = "#" + (ratingList.size()+1) + " " + accountRank.toString();
        }

        return info;
    }

    public static String getMyCrystal(String id, String nick) {
        String crystal;

        int id_list = isHasAccount(id);

        if (id_list != -1) {
            AccountRank accountRank = ratingList.get(id_list);

            crystal = String.valueOf(accountRank.getCrystal());
        } else {
            AccountRank accountRank = new AccountRank(nick,id,NAMERanks[0],0,0,0);
            addNewAccount(accountRank);
            crystal = "0";
        }
        return crystal;
    }

    public static String[] getLeadersList() { // Выдает лист лидеров, нужен для отображения команды !leaders
        synchronized (ratingList) {
            String[] leadersList = new String[ratingList.size()];
            for (int i = 0; i < leadersList.length; i++) {
                leadersList[i] = "~place~" + i + "~nameRank~" + DrawRankCard.getNameRank(ratingList.get(i).getLvl()) + "~nickname~" + ratingList.get(i).getInfoForLeadersBoards();
            }
            return leadersList;
        }
    }

    private static class AccountRank { // Подкласс обьектов рангов.
        String nick;
        String rank;
        String id;

        int lvl;
        int xp;
        int need_up;


        int crystal;


        private static final int modifyXP = 107;

        private AccountRank(String nick,String id, String rank, int lvl, int xp, int crystal) { // Конструктор аккаунтов
            this.nick = nick;
            this.id = id;
            this.lvl = lvl;
            this.xp = xp;
            this.crystal = crystal;
            this.rank = rank;
            need_up = getNeedXP(lvl);
        }

        private static int getNeedXP(int lvl) { // Геттер сколько нужно опыта до следующего уровня.
            if (lvl == 0) {
                return 1000;
            }
            return 1000+(modifyXP*lvl);
        }

        private String getLvl() { // Геттер ЛВЛ
            return String.valueOf(lvl);
        }

        private int getIntegerLvl() {
            return lvl;
        }

        private String getXp() { // Гетер Опыта
            return String.valueOf(xp);
        }

        private String getRank() {
            return this.rank;
        }

        private int getCrystal() {
            return crystal;
        }

        private void addXp(int xp, String name) { // Добавление определенного количества опыта
            if (!name.equals(nick)) {
                nick = name;
            }
            this.xp = this.xp + xp;
            sortRating(RankDiscordChecker.ratingList);
            this.canLVLup();
        }

        private void removeXp(int xp) {
            if (this.xp != 0) {
                this.xp = this.xp - xp;
            }
        }

        private boolean canLVLup() { // Может ли повысится уровень?
            if (xp >= need_up) {
                appLVL();
                return true;
            }
            return false;
        }

        private void appLVL(){ // Увеличение уровня
            lvl++;
            rankUP();
            xp = xp - need_up;
            need_up = getNeedXP(lvl);
        }

        private void rankUP(){ // Установка нового ранга если это возможно.
            int id_rank = getUpRank(lvl);
            if (!rank.equals(NAMERanks[id_rank])) {
                rank = NAMERanks[id_rank];
                crystal += crystalBonusForUpLvl[id_rank];
                try {
                    jda.awaitReady().getGuilds().get(0).getCategories().get(1).getTextChannels().get(4)
                            .sendMessage("Поздравляем с повышением <@"+id+
                                    "> теперь ваше новое звание: " + rank+  "\n"+
                                    "Так же вы получаете бонус за повышение в размере " + crystalBonusForUpLvl[id_rank]+" кристалов\n" +
                                    "Что-бы проверить свой баланс напиши \"!crystal\"")
                            .addFile(new File(Objects.requireNonNull(RankDiscordChecker.class
                                    .getClassLoader()
                                    .getResource("DiscordRanks/rank/"+DrawRankCard.getFileRank(String.valueOf(lvl)))).toURI()
                                    /*"src\\main\\java\\DiscordRanks\\rank\\"+DrawRankCard.getFileRank(String.valueOf(lvl))*/))
                                    .queue();
                } catch (InterruptedException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        }

        private void addCrystal(int crystals) {
            this.crystal += crystals;
        }

        private int getAllXP() { // Геттер всего накопленного опыта.
            int allXP = 0;
            if (!(lvl > 0)) {
                return xp;
            }
            for (int i = 0; i < lvl; i++) {
                allXP += getNeedXP(i);
            }

            return allXP+xp;
        }

        private String getNick() { // Геттер ника
            return nick;
        }

        private String getId() { // Геттер id
            return id;
        }

        private boolean isBest(AccountRank accountRank) { // Какой аккаунт имеет больше опыта и уровень.

            if (this.lvl > accountRank.lvl) {
                return true;
            } else if (this.lvl == accountRank.lvl && this.xp > accountRank.xp) {
                return true;
            } else { return false; }
        }

        private String getInfoForLeadersBoards(){
            return getNick() + "~level~" + getLvl() + "~experience~" + getAllXP();
        }

        @Override
        public String toString() {
            return RankDiscordChecker.xp + lvl + RankDiscordChecker.xp + xp + RankDiscordChecker.xp + need_up + RankDiscordChecker.xp + nick;
        }
    }
}