
# 인프런 - 백엔드 프레임워크 만들기 (개정전)

## 강의 내용
https://www.inflearn.com/course/%ED%94%84%EB%A0%88%EC%9E%84%EC%9B%8C%ED%81%AC-%EB%A7%8C%EB%93%A4%EA%B8%B0

## 참고 깃허브
https://github.com/zeroshift01/code5

# 인프런 백엔드 프레임워크 만들기
## 챕터 2

```java
class WelcomeNotRunnableTest {
  @Test
  public void test() throws Exception {
    String name = "abcd";
    HttpServletRequest request = null;
    
    //request.setParameter("name", name);

    Welcome welcome = new Welcome();
    welcome.execute(request);

    Object msg = request.getAttribute("msg");

    assertEquals("WELCOME = " + name, msg);
  }
}
```

챕터 1에서 만든 `Welcome` 객체를 테스트하는 테스트 코드를 만든다고 가정해보자.
위에 테스트 코드는 실제 동작하지 않는다.

그 이유를 보기 전에 만들었던 `Welcome` 의 비즈니스 로직을 참고해보자.

```java
public String execute(HttpServletRequest req) {
  String name = req.getParameter("name");
  String msg = "WELCOME = " + name;
  req.setAttribute("msg", msg);
  return "/welcome.jsp";
}
```

위와 같이 `Welcome` 은 WAS인 톰캣에서 전달 받은 값을 `welcom.jsp` 에 전달해서 출력해주는 단순한 로직을 갖고 있다.

하지만 우리는 WAS가 없이 테스트 코드를 진행해야 된다.
테스트 코드를 저런 식으로 짜도 돌아갈까?

다시 테스트 코드를 보자.

```java
class WelcomeNotRunnableTest {
  @Test
  public void test() throws Exception {
    String name = "abcd";
    HttpServletRequest request = null;
    
    //request.setParameter("name", name);

    Welcome welcome = new Welcome();
    welcome.execute(request);

    Object msg = request.getAttribute("msg");

    assertEquals("WELCOME = " + name, msg);
  }
}
```

정답을 알겠는가?
정답은 돌아가지 않는다. 그 이유는 당연하게도 `HttpServletRequest` 는 인터페이스고, 만약 테스트를 위해서 이를 구현한다해도 단순히 테스트일 뿐이지만 상당한 시간 소모가 들어간다.

그렇다면 만약에 우리가 삽질을 통해서 이 클래스의 구현체를 만들었다고 가정해보자.
하지만 `HttpServletRequest` 내부에는 `setParameter()` 메서드가 정의되어있지 않다.

그 이유는 사용자의 요청을 온전히 보관하기 위해서 사용되기 때문에 `setter` 자체를 구현하지 않았기 때문이다. 따라서, 위와 같은 코드를 만들었을 때 비즈니스 로직에 전달할 수 있는 방법이 존재하지 않는다.


## 챕터 3
### DTO를 사용하는 SQL 질의에 대한 문제점

```java
private static List<EmployeeDto> select(Transaction transaction, EmployeeDto employeeDto) throws SQLException {
    String name = employeeDto.getName();
    String query = "SELECT EMP_NO, EMP_NAME, HP_N, DEPT_N FROM EMP";

    if (!"".equals(name)) {
        query += " WHERE EMP_NAME = '" + name + "'";
    }

    System.out.println(query);

    Statement statement = transaction.statement();
    ResultSet resultSet = statement.executeQuery(query);

    ArrayList<EmployeeDto> employeeDtos = new ArrayList<>();

    while (resultSet.next()) {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(resultSet.getString("EMP_NO"));
        dto.setName(resultSet.getString("EMP_NAME"));
        dto.setPhoneNum(resultSet.getString("HP_N"));
        dto.setDepartmentNo(resultSet.getString("DEPT_N"));

        employeeDtos.add(dto);
    }
    return employeeDtos;
}
```

1. 자바코드 내에 SQL을 사용하므로 SQL이 변경될 때마다 변경해줘야한다.
2. 만약 파라미터가 증가할 경우 dto의 `getter/setter`  가 계속해서 증가한다.

이 뿐만 아니라 이 코드의 결합도가 높다보니 유지보수할 영역이 많아진다.

> 즉, 유지보수성이 떨어진다.

또한, 아래와 같이 정적쿼리를 사용하는 update문을 보자.

```java
private static void update(Transaction transaction,
        List<EmployeeDto> maybeEmployees,
        EmployeeDto employeeDto) throws SQLException {

    transaction.setAutoCommitFalse();

    try {
        for(EmployeeDto dto : maybeEmployees) {
            String id = dto.setId();
            String phoneNum = employeeDto.getPhoneNum();

            String query = "UPDATE EMP SET HP_N = ? WHERE EMP_NO = ?";
            PreparedStatement preparedStatement = transaction.preparedStatement(query);
            preparedStatement.setString(1, phoneNum);
            preparedStatement.setString(2, id);

            System.out.println(query);

            int updateCnt = preparedStatement.executeUpdate();
            if(updateCnt != 1) {
                throw new SQLException("업데이트에 실패하였습니다.");
            }
        }
        transaction.commit();
    } catch (Exception e) {
        transaction.rollback();
    }
}

```


이 경우에는 `System.out.print(query);` 를 사용하지만 실제 질의되는 쿼리가 어떤건지 알 수가 없으며, `setString()` 의 시작이 1이므로 배열 시작(0)과 혼동할 소지가 있다.

그렇다면, DTO를 사용하지 않는 대안이 있을까?

대표적인 대안이 컬렉션 객체를 사용하는 방법일 것이다.

### Collection Object SQL 질의 문제점

```java
private static List<String[]> selectForCollection(Transaction transaction) throws SQLException {

    CustomRequest customRequest = CustomRequestContext.get();
    String name = customRequest.getString("EMP_NAME");

    String query = "SELECT EMP_NO, EMP_NAME, HP_N, DEPT_N FROM EMP";

    if (!"".equals(name)) {
        query += " WHERE EMP_NAME = '" + name + "'";
    }

    System.out.println(query);

    Statement statement = transaction.statement();
    ResultSet resultSet = statement.executeQuery(query);

    ArrayList<String[]> table = new ArrayList<>();

    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    String[] column = new String[columnCount];

    for (int i = 0; i < column.length; i++) {
        column[i] = metaData.getColumnName(i + 1);
    }

    table.add(column);


    while (resultSet.next()) {
        String[] recode = new String[columnCount];

        for(int i = 0; i < column.length; i++) {
            recode[i] = resultSet.getString(column[i]);
        }
        table.add(recode);
    }
    return table;
}

```

위의 동적 SQL을 사용해서 컬렉션으로 심어둔 코드이다.

일단, 단점으로는 For문의 성능 자체가 느리며,  `List<String[]>` 형태로 리턴하기 때문에 이 결과를 처리할 때 불편함 및 NPE 발생 가능성이 존재한다.

## 챕터 3
```sql
DROP TABLE EMP ;

CREATE TABLE EMP (
EMP_N PRIMARY KEY
, EMP_NAME
, HP_N
, DEPT_N
);


INSERT INTO EMP VALUES ('N01','ABC','','D01');
INSERT INTO EMP VALUES ('N02','ZZZ','','D01');
INSERT INTO EMP VALUES ('N03','ABC','','D02');

DROP TABLE FW_SQL;

CREATE TABLE FW_SQL (
KEY PRIMARY KEY
, SQL
);

INSERT INTO FW_SQL VALUES ('SQL_SELECT_BY_USERNAME
', 'SELECT EMP_N, EMP_NM, HP_N, DEPT_N FROM EMP WHERE EMP_NM = [EMP_NM] ORDER BY EMP_N');

INSERT INTO FW_SQL VALUES ('SQL_UPDATE_EMP
', 'UPDATE EMP SET HP_N = [HP_N] WHERE EMP_N =[EMP_N]' );
```


## 챕터 4
### 주입, 의존, 제어

**주입** : A에 B를 주입한다. (A에서 B의 기능을 이용할 수 있도록 준비한다.)
**의존** : A가 B에 의존한다. (A가 B의 기능을 이용한다.)
**제어** : A가 B를 제어한다. (B를 생성하고, B의 기능을 이용할 수 있도록 준비하고, 실행 결과를 마무리한다. -> 객체의 생명주기를 담당한다.

+ 예제코드

```java
protected Connection createConnection() throw SQLException {
	try {
		String className = "org.sqlite.JDBC";
		Class.forName(className);
	} catch (ClassNotFound ex) {
		ex.printStackTrace();
		return null;
	}	
	String url = "jdbc:sqlite:~/dailyworker/db/framework.db"
	Connection conn = DriverManager.getConnection(url);
	
	return conn;
}
```

`try {...}`  이 블록 안에 로드될 클래스를 리플렉션으로 `DriverManager` 에 등록하는 것을 **주입**으로 볼 수 있다.

**의존**은 이제 `return conn` 을 통해서 실제 `DriverManager` 를 사용하는 측이 이 `Connection` 객체에 의존한다고 볼 수 있다.

프레임워크의 공통 모듈을 만든다는 것 의존하기 쉽게 개발자에게 전달해주는 것이다.

그렇다면 **제어**는 무엇일까? 주입 -> 의존 후에 사용된 객체가 소멸될때까지의 과정을 한번에 처리하는 것이 제어(개발자가 명시적으로 처리할 수 있도록)이다.

### 의존성 주입(DI, Dependency Injection)

**의존성 주입** : 어떤 기능을 사용할 지 알려준다.
즉, 객체지향의 지연바인딩을 통해 정보가 주입되는 것이 의존성 주입이다.
따라서, 실행 시점에 객체가 결정되고, 객체의 메서드들이 사용될 수 있게 준비해주는 것이다.
+ 기능과 데이터가 실행 시점에 결정될 때 사용되는 바인딩 정보
+ 의존성 주입의 기준은 서비스의 키 (e.g localhost:8080/login -> login)

### 제어의 역전 (IoC, Inversion of Control)

위에서 본 내용을 토대로 제어는 개발자가 코드의 생명주기(생성 -> 사용 -> 종료)를 작성하는 것을 뜻한다. 그렇다면 제어의 역전은 무엇일까? -> **프로그램이 이를 처리**한다.

즉, 객체의 생명주기를 개발자가 아닌 프로그램이 알아서 해준다고 생각하면된다.
e.g) 톰캣의 서블릿 컨테이너 (서블릿 기능을 따로 구현을 안해도 컨테이너가 알아서 처리)

### 비즈니스 로직

**비즈니스 로직** : 공통기능 + 요구사항
-> 이를 재사용하려면??? **(DI와 IoC로 제공)**
DI는 정보는 서비스 키로 전달하며, IoC는 `MasterController.execute()` 로 처리할 예정

```sql
DROP TABLE FW_CONTROLLER;

DROP TABLE FW_VIEW;

CREATE TABLE FW_SQL (
	KEY PRIMARY KEY,
	SQL
);

CREATE TABLE FW_CONTROLLER (
	KEY PRIMARY KEY,
	CLASS_NAME,
	METHOD_NAME
);

CREATE TABLE FW_VIEW (
	KEY PRIMARY KEY,
	JSP
);

INSERT INTO FW_CONTROLLER VALUES ('select', 'io.dailyworker.framework.domain.Employee', 'select');

INSERT INTO FW_CONTROLLER VALUES ('update', 'io.dailyworker.framework.domain.Employee', 'update');

INSERT INTO FW_VIEW VALUES ('employeeView', '/WEB-INF/classes/io/dailyworker/framework/view/employeeView.jsp');

INSERT INTO FW_CONTROLLER VALUES ('login', 
'io.dailyworker.framework.domin.Login', 'login');

INSERT INTO FW_VIEW VALUES ('loginView', '/WEB-INF/classes/io/dailyworker/framework/view/loginView.jsp');

DELETE FROM FW_SQL WHERE KEY IN ('MASTERCONTROLLER_ERD_01', 'MASTERCONTROLLER_ERD_02');

INSERT INTO FW_SQL VALUES (
'MASTERCONTROLLER_ERD_01',
'SELECT KEY, CLASS_NAME, METHOD_NAME FROM FW_CONTROLLER WHERE KEY = [KEY]'
);

INSERT INTO FW_SQL VALUES (
	'MASTERCONTROLLER_ERD_02',
	'SELECT KEY, JSP FROM FW_VIEW WHERE KEY = [KEY]'
);



```

## 챕터 5
+ 기밀성 : 작업의 과정과 결과를 감추는 것
    + e.g) 비밀키 암호화를 통해서 많이 해결한다.
+ 무결성 : 작업의 흐름이 일치하는 것
    + e.g) A->A' 로 변경되었을 때 A' 임을 증명되는 것
+ 가용성 :  허락 받은 사용자가 정해진 작업을 할 수 있는 것
    + e.g) 가용성 위배 : XSS, DDOS 등.. (정보보호 취약성)

가용성을 보장 -> 인증과 접근 제어
+ 관리적 보안 : 정책, 자원, 인원
+ e.g) 정보보호서약서
+ 물리적 보안 : 전산센터와 떨어져있는 상황, cctv 등
+ 기술적 보안
+ 인증
+ 접근제어
+ 	e.g) DRM, NAC 등..

### 인증
**인증** : 내가 누군지 시스템에 알려주는 것
+ 내가 누군지 -> 아이디/패스워드
+ 시스템에 알려주는 것 -> 브라우저 (쿠키), WAS (세션), 비즈니스로직

[강의자료1](https://user-images.githubusercontent.com/22961251/132526194-c0fb66af-98b3-41ee-900a-5648923c84b8.png)

1. 사용자가 브라우저 접근하여 로그인 시 WAS는 브라우저에 쿠키를 구워준다
   ->  HTTP가 Stateless한 프로토콜이기 때문이다.
2. WAS는 쿠키를 통해서 세션 객체를 생성하여 비즈니스 로직에 사용하는 데이터로 사용한다.

### 접근제어

**접근제어** : 내가 할 작업을 허락 받는 것
내가 -> 아이디(권한코드), 접근통제모델 (MAC,DAC,RBAC)
+ MAC (Mandatory Access Control) :  주체가 객체를 사용할 수 있다라는 것을 중앙 통제 (복잡하고, 중앙통제 컴퓨터가 항상 실행되어야한다.)
+ DAC (Discretionary Access Control) :  사용자가 로그인하면 사용자가 주도적으로 자원에 대한 접근 비트를 설정하는 것 (chmod)
+ RBAC (Role Based Access Control) : 권한을 분리하여 자원에 대한 접근을 막는 것 (사용자는 권한코드에 따라서 접근할 수 있게함)

[강의자료2](https://user-images.githubusercontent.com/22961251/132526168-042ae36d-8c51-4f46-9a4e-19f5ab106d1d.png)

1. 사용자가 브라우저에 접속을 했을 때 쿠키가 생성된다.
2. WAS는 브라우저의 쿠키를 통해서 세션을 만들 수 있다.
   -> `getSession()` 을 통해서 브라우저 영역에 접근
3. WAS는 접근하여 페이지에 세션을 저장한다.
4. WAS가 이중화 되어있을 경우 페이지에 저장된 경우에 다른 WAS에 사용이 불가능하다.
   -> 스토리지에 저장한다. (클러스터 서버) 네트워크 통신을 통해서
   [강의자료3](https://user-images.githubusercontent.com/22961251/132526207-9542be03-84ce-42d4-893a-6a3ed67bfdb7.png)

즉, 이 네트워크 통신을 위해서 세션 객체는 직렬화가 되어야한다.

```sql
CREATE TABLE BZ_ID (
ID PRIMARY KEY
, PIN
, AUTH
, FAIL_CNT
, LAST_LOGIN_DTM
);

INSERT INTO BZ_ID values (
'id_A0'
, 'abcd1111'
, 'A0'
, '0'
, ''
);

INSERT INTO BZ_ID values (
'id_U0'
, 'abcd1111'
, 'U0'
, '0'
, ''
);


DELETE FROM FW_SQL WHERE KEY IN ('LOGIND_01','LOGIND_02','LOGIND_03');


INSERT INTO FW_SQL values (
'LOGIND_01'
, 'SELECT 
ID
, PIN
, AUTH
, FAIL_CNT
, LAST_LOGIN_DTM 
FROM 
BZ_ID
WHERE ID = [ID] 
');


INSERT INTO FW_SQL values (
'LOGIND_02'
, 'UPDATE BZ_ID
SET FAIL_CNT = FAIL_CNT + 1
WHERE ID = [ID] 
');


INSERT INTO FW_SQL values (
'LOGIND_03'
,'UPDATE BZ_ID
SET FAIL_CNT = 0
, LAST_LOGIN_DTM = [LAST_LOGIN_DTM]
WHERE ID = [ID]
');

```

```sql
ALTER TABLE FW_CONTROLLER ADD SESSION_CHECK_YN;
ALTER TABLE FW_CONTROLLER ADD AUTH;

UPDATE FW_CONTROLLER SET SESSION_CHECK_YN = 'N', AUTH = '' WHERE KEY IN ('login','loginView');

UPDATE FW_CONTROLLER SET SESSION_CHECK_YN = 'Y', AUTH = '' WHERE KEY = 'select';

UPDATE FW_CONTROLLER SET SESSION_CHECK_YN = 'Y', AUTH = 'A0' WHERE KEY = 'update';

UPDATE FW_SQL SET SQL = 'SELECT
KEY
, CLASS_NAME
, METHOD_NAME
, SESSION_CHECK_YN
, AUTH
FROM FW_CONTROLLER
WHERE KEY = [KEY]'
WHERE KEY = 'MASTERCONTROLL_ERD_01';

DROP TABLE BZ_ID;

CREATE TABLE BZ_ID (
ID PRIMARY KEY
, PIN
, AUTH
, FAIL_CNT
, LAST_LOGIN_DTM
);

INSERT INTO BZ_ID values (
'id_A0'
, 'abcd1111'
, 'A0'
, '0'
, ''
);


INSERT INTO BZ_ID values (
'id_U0'
, 'abcd1111'
, 'U0'
, '0'
, ''
);


DELETE FROM FW_SQL WHERE KEY IN ('LOGIND_01','LOGIND_02','LOGIND_03');


INSERT INTO FW_SQL values (
'LOGIND_01'
, 'SELECT 
ID
, PIN
, AUTH
, FAIL_CNT
, LAST_LOGIN_DTM 
FROM 
BZ_ID
WHERE ID = [ID] 
');


INSERT INTO FW_SQL values (
'LOGIND_02'
, 'UPDATE BZ_ID
SET FAIL_CNT = FAIL_CNT + 1
WHERE ID = [ID] 
');


INSERT INTO FW_SQL values (
'LOGIND_03'
,'UPDATE BZ_ID
SET FAIL_CNT = 0
, LAST_LOGIN_DTM = [LAST_LOGIN_DTM]
WHERE ID = [ID]
');
```

## 챕터6
## 6강 SQL

```sqlite
drop TABLE FW_SRT;

CREATE TABLE FW_SRT (
    OTP PRIMARY KEY
    , MODE
    , KEY
    , IV
);

INSERT INTO FW_SRT VALUES ('S01','ARIA/ECB/ZERO','E231C123B7512A8A9027E9EE99C0C684','');
INSERT INTO FW_SRT VALUES ('S02','ARIA/CBC/PKCS7','B426E1A441F6DBFC2B2D2412D0066D20','52A9A4CC4FB1EF00A72FF87583D44E5C');
INSERT INTO FW_SRT VALUES ('S03','AES/CBC/PKCS7','BF210BE9E2ED4620B442D5AF8D000E40','CF80492ACF3166C7CC039818619E4859');
INSERT INTO FW_SRT VALUES ('SDB','ARIA/CBC/PKCS7','B426E1A441F6DBFC2B2D2412D0066D20','52A9A4CC4FB1EF00A72FF87583D44E5C');

DELETE FROM FW_SQL WHERE KEY = 'DATACRYPT_01';
INSERT INTO FW_SQL VALUES ('DATACRYPT_01','SELECT * FROM FW_SRT WHERE OTP = [OTP]');

update BZ_ID SET PIN ='424ab5a6448f7b6aca9cd65c361b672c3d853622bd29001ee15bc5c50bcfa169' WHERE ID = 'id_A0';
update BZ_ID SET PIN ='337c1456c9b72fd82583e974ac3885295373b1968210cfc0cb5418c554935f4f' WHERE ID = 'id_U0';
```