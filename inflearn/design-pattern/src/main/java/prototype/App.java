package prototype;

public class App {
    public static void main(String[] args) {
        GithubRepository repository = new GithubRepository();
        repository.setUser("whiteship");
        repository.setName("live-study");

        GithubIssue githubIssue = new GithubIssue(repository);
        githubIssue.setId(1);
        githubIssue.setTitle("1주차 과제 : JVM은 무엇이며 자바 코드는 어떻게 실행하는 것인가.");

        String url = githubIssue.getUrl();
        System.out.println(url);

        // 아래와 같이 처리되는 게 아니라 위의 객체를 클론해서 사용하고자함.
//        GithubIssue githubIssue2 = new GithubIssue(repository);
        //TODO : clone != githubIssue
        //TODO : clone.eqauls(githubIssue) => true
        //GithubIssue clone = githubIssue.clone();
//        githubIssue.setId(2);
//        githubIssue.setTitle("2주차 과제");
    }
}
