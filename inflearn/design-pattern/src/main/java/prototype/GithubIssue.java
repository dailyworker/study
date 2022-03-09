package prototype;

import java.util.Objects;

public class GithubIssue implements Cloneable {
    private int id;

    private String title;

    private GithubRepository githubRepository;

    private String url;

    public String getUrl() {
        return String.format("https://github.com/%s/%s/issues/%d",
                githubRepository.getUser(),
                githubRepository.getName(),
                this.getId());
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GithubIssue that = (GithubIssue) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(githubRepository, that.githubRepository) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, githubRepository, url);
    }
}
