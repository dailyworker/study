package prototype;

public class GithubIssue {
    private int id;

    private String title;

    private GithubRepository githubRepository;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GithubIssue(GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GithubRepository getGithubRepository() {
        return githubRepository;
    }

    public void setGithubRepository(GithubRepository githubRepository) {
        this.githubRepository = githubRepository;
    }
}
