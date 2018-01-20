package ritik.nexcha;

/**
 * Created by SuperUser on 27-05-2017.
 */

public class GridItem {
    private String image, genre, chat_uid, episode, uview;
    private String title, author, views, description;


    public GridItem() {
        super();
    }

    public GridItem(String chat_uid, String title, String image, String genre, String author, String views, String description, String episode, String uview) {
        this.image = image;
        this.title = title;
        this.genre = genre;
        this.chat_uid = chat_uid;
        this.author = author;
        this.views = views;
        this.description = description;
        this.episode = episode;
        this.uview = uview;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getChat_uid() {
        return chat_uid;
    }

    public void setChat_uid(String chat_uid) {
        this.chat_uid = chat_uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUview() {
        return uview;
    }

    public void setUview(String uview) {
        this.uview = uview;
    }
}
