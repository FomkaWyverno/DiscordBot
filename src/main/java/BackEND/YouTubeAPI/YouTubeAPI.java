package BackEND.YouTubeAPI;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class YouTubeAPI {
    protected static final String API_KEY;
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_URL_SEARCH = "https://youtube.googleapis.com/youtube/v3/search";
    private static final String API_URL_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems";
    private static final String CONTENT_DETAILS = "part=contentDetails";
    private static final String PLAYLIST_ID = "playlistId=";
    private static final String PAGE_TOKEN = "pageToken=";
    private static final String SNIPPET = "part=snippet";
    private static final String SEARCH = "q=";
    private static final String MAX_RESULTS = "maxResults=10";
    private static final String SPACE = "%20";


    static {
        BufferedReader reader;
        String api = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("YouTubeAPI.txt")));
            api = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert api != null;
        API_KEY = "key="+api;
    }

    public static String getYoutubeUrl(String video) throws IOException {
        video = video.replaceAll(" ",SPACE); // Заменяем все пробелы на код URL пробела
        String url = API_URL_SEARCH+"?"+SNIPPET+"&"+SEARCH+video+"&"+MAX_RESULTS+"&"+API_KEY; // Создаем ссылку запрос у API YouTube
        ObjectMapper objectMapper = new ObjectMapper(); // Создаем Парсер JSON
        YouTubeVideoJSON youTubeJSON = objectMapper.readValue(new URL(url), YouTubeVideoJSON.class); // Парсим обьект из JSON
        List<YouTubeVideoJSON.Videos> list = youTubeJSON.items; // Берем лист с видео
        for (YouTubeVideoJSON.Videos videos : list) { // Циклим видео пока не найдем не прямой эфир
            String liveStream = videos
                    .snippet
                    .liveBroadcastContent;
            if (liveStream.equals("none")) {
                return YOUTUBE_URL + videos.id.videoId;
            }
        }
        return null;
    }

    public static ArrayList<String> getYouTubeMusicsPlaylist(String playlist) throws IOException {
        playlist = playlist.substring(playlist.indexOf("list=")+5); // Извлекаем PlaylistID у ссылки
        if (playlist.contains("&")) { // Если после аргумента лист есть ещё один аргумент то удаляем следующий аргумент.
            playlist = playlist.substring(0,playlist.indexOf("&"));
        }
        ObjectMapper objectMapper = new ObjectMapper(); // Создаем парсера JSON
        YouTubePlaylistJSON playlistJSON = objectMapper.readValue(new URL( // Делаем первый запрос на видео из Плейлиста
                API_URL_PLAYLIST+"?"+CONTENT_DETAILS+"&"+PLAYLIST_ID+playlist+"&" // И парсим их в обьект
                        +MAX_RESULTS+"&"+API_KEY
        ),YouTubePlaylistJSON.class);
        ArrayList<String> list = new ArrayList<>(); // Создаем лист
        do {
            List<YouTubePlaylistJSON.Video> items = playlistJSON.getItems(); // Излекаем лист с видео.
            for (YouTubePlaylistJSON.Video item : items) {
                String YouTubeURL = YOUTUBE_URL
                        + item.getContentDetails().getVideoId(); // Берем макет ссылки и добавляем аргумент VideoID
                list.add(YouTubeURL);
            }
            String nextPage = playlistJSON.getNextPageToken(); // Берем следующий токен страницы
            if (nextPage != null) {
                playlistJSON = objectMapper.readValue(new URL( // Идем на следующую страницу и присваиваем новый обьект ту же переменную.
                        API_URL_PLAYLIST+"?"+CONTENT_DETAILS+"&"+PLAYLIST_ID+playlist+
                                "&"+PAGE_TOKEN+nextPage+"&"+MAX_RESULTS+"&"+API_KEY
                ),YouTubePlaylistJSON.class);
            } else { // Если страницы закончились то закрываем цикл.
                break;
            }
        } while (true);
        return list; // Отдаем лист с видео
    }
}
