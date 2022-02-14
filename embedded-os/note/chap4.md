# 부팅
+ 부팅이란? 
  1. 시스템에 전원이 인가된 후 모든 초기화 작업을 마치고 펌웨어가 대기 상태가 된 것을 의미.
  2. ARM 코어가 리셋 익셉션 핸들러를 모두 처리한 다음에 본격적으로 C언어 코드로 넘어가기 직전까지를 의미.

여기서는 2번의 정의를 갖고 진행한다.

## 메모리 설계

에뮬레이터로 사용하는 QEMU는 아주 단순한 메모리 구조로 되어있다.
QEMU의 기본 메모리 값은 128MB로 임베디드 시스템에서는 많은 용량이라고 볼 수 있다.

실행 파일은 아래와 같이 메모리를 세 가지 영역으로 나누어 사용한다.

1. **text 영역** : 코드가 있는 공간 (코드이므로 임의로 변경하면 안된다.)
2. **data 영역** : 초기화한 전역 변수가 있는 공간 (전역 변수를 선언할 때 초기 값을 할당해서 선언하면 여기에 할당된다.)
3. **BBS 영역** : 초기화하지 않은 전역 변수가 있는 공간. (빌드를 통해서 생성된 바이너리에서는 초기화가 안되어있어서 심벌과 크기만 들어가있다.)

이제 고민할 내용은 이 영역들을 어떻게 배치할 것이냐이다.
하드디스크와 램과 비교해서 생각하듯이 빠르지만 용량이 작은 메모리와 속도는 느리지만 용량이 큰 메모리가 존재한다면 text 영역을 빠른 메모리 영역에 배치하는게 맞고, data 중에서도 일부 속도에 민감한 데이터들은 링커에 정보를 주어서 빠른 메모리에 배치해야한다. 나머지 data 영역과 BBS 영역은 속도는 느려도 용량이 큰 메모리에 배치하는게 좋다.

하지만, QEMU는 그런 구분이 없으므로 순서대로 배치해보자 한다.

순서는 고민하지 않는다 결정했으니 그렇다면 이제 메모리의 영역별 크기를 정할 차례이다.

1. text 영역 : 1MB (익셉션 벡터 테이블을 text 영역에 포함시킬 것이므로 시작 주소 : 0x00000000, 끝나는 주소 : 0x000FFFFF)
2. data 및 BBS 영역 : 각 동작 모드별 1MB씩 할당
  +  USR, SYS(2MB): 0x00100000 ~ 0x002FFFFF
  +  SVC(1MB): 0x00300000 ~ 0x003FFFFF
  +  IRQ(1MB): 0x00400000 ~ 0x004FFFFF
  +  FIQ(1MB): 0x00500000 ~ 0x005FFFFF
  +  ABT(1MB): 0x00600000 ~ 0x006FFFFF
  +  UND(1MB): 0x00700000 ~ 0x007FFFFF

여기서 USR, SYS 모드는 메모리 공간과 레지스터를 모두 공유하므로 하나로 보고, 2MB를 할당하였음.
추가로, RTOS 위에서 동작할 태스크(Task) 스택 영역을 생각해야하는데 각 태스크 마다 1MB를 할당하며 총 64개의 태스크를 OS가 관리할 수 있게 64MB로 할당.
그리고 전역 변수 영역 1MB와 나머지 공간은 동적 할당 영역으로 사용한다.

따라서, 1MB(text) + 7MB(각 모드별 스택 영역) + 64MB(태스크 스택 영역) + 1MB(전역 변수 영역) + 55MB(동적 할당 영역)로 총 128MB를 할당한다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153862938-3e0d8604-0d76-4874-932a-246fbca8fa70.png" alt="메모리 할당 영역" />
</p>

## 익셉션 벡터 테이블 만들기
현재까지 코드 (리셋 벡터)를 익셉션 벡터 테이블 영역에 작성했으나 이것이 익셉션 벡터 테이블이라 보긴 힘들다. 
실제로 익셉션 핸들러를 작성해보고자 한다.

하지만, 지금까지의 내용을 보고 이해가 안되는 부분이 많았고, 저자분께서도 친절하게 부록을 읽고 오라고하셨다.
부록을 읽고 난 뒤에 느낀 점은 인터럽트에 대해서 알고 있다했는데 잘못 알고 있는 부분도 많았고, 실제 프로그램이 어떻게 동작하는지 대략적으로 알고 있었으나 자세히 몰랐는데 그것이 해결된 거 같다.

챕터 4를 다시 정리하기 전에 부록 내용을 정리하고자 한다.

# ARM 아키텍처 기초 상식
## 익셉션 벡터 테이블
전원이 켜지면 ARM은 익셉션 벡터 테이블의 리셋 벡터를 읽는다.
이 값도 사용자가 조정이 가능하긴하나 대부분 기본 값인 0x00000000에 익셉션 벡터 테이블을 배치한다.
전 챕터 내용 정리본에도 적어놨지만 이 값은 메모리의 시작 주소이다. 따라서, ARM은 전원이 켜지면 단순하게 메모리의 시작 주소부터 읽는다 할 수 있다.

익셉션 벡터 테이블은 익셉션의 상황에 따라서 정리된 오프셋의 주소를 기록해둔 테이블이다.
ARM은 익셉션 벡터 테이블에 정의된 상황이 발생하면, PC를 익셉션 벡터 테이블에 정의된 오프셋 주소로 강제 변환한다.
그리고, 익셉션 벡터 테이블에 있는 명령을 바로 실행한다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153870321-8f8d0e10-9b0a-47cb-a8a2-2289d877d250.png" alt="익셉션 벡터 테이블" />
</p>

위의 그림을 보면 각 오프셋이 4바이트씩 할당되어 있는 것을 확인할 수 있다. 
각 익셉션을 처리하기 위해서는 각 익셉션 오프셋에 해당하는 곳에 브랜치 명령어를 쓰고 익셉션을 처리하는 코드로 점프한다. 
이 코드를 **익셉션 핸들러**라 한다.

마치, 스프링의 익셉션 핸들러와 같이 예상되는 익셉션 상황이 발생하면 그 처리를 다른 곳에 한다고 생각하면 될 것 같다.

ARM에서 익셉션이 발생했을 때 일어나는 일을 정리하면 다음과 같다.
1. 익셉션 발생 시 ARM 코어는 프로그램 카운터를 익셉션 벡터 테이블의 익셉션 벡터 오프셋으로 변경한다.
2. 익셉션 벡터 오프셋에 브랜치 명령을 쓰고 브랜치 명령에 따라 다시 점프해서 익셉션 핸들러로 진입한다.
3. 익셉션 핸들러가 처리되면 다시 복귀한다.

위의 상황을 보면 아래와 같은 의문점이 들 것이다.
> 그렇다면 핸들러가 처리되서 복귀하는 것은 어떻게 처리가 될까? 

이는 먼저 ARM에서 발생할 수 있는 익셉션의 종류와 인터럽트, Abort를 설명하면서 다시 설명해보도록 하겠다.

## 익셉션의 종류
펌웨어에서의 **익셉션(Exception)** 이란 주변장치(peripheral) 등에서 발생한 신호를 처리하기 위해 순차적으로 진행하던 프로그램의 수행을 끊는 것을 의미한다.
어떻게 보면, 이 개념 자체가 자바 개발자인 내가 보기에는 자바 어플리케이션에서의 익셉션과 상등하다고 생각이 든다.

아무튼, 익셉션은 위와 같은데 위에서 물어본 질문에 대답할 차례가 된 것 같다.
프로그램은 PC를 통해서 순차적으로 명령어를 읽으면서 나아간다. 그러나 익셉션이 발생하면 익셉션 처리를 위해서 익셉션 핸들러로 점프가 된다하였다.
즉, 익셉션이 발생하면 모든 레지스터의 유효성이 깨진다. (프로그램의 흐름(context)가 달라지므로)
  + 여기서 레지스터의 유효성이 깨진다는 의미는 R0부터 R14까지의 레지스터 값은 실행 중인 코드에서 유효성을 유지해야하는데 이게 깨진다는 의미

그렇다면 익셉션을 처리하고 깨진 유효성을 어떻게 다시 살릴 수가 있을까?
ARM에서는 익셉션이 발생하면 R14(Link Register, LR)에 복귀 주소를 자동으로 저장한다.

예시를 잠깐 보자.
USR 모드에서 프로그램이 수행되고 있다가 익셉션이 발생해서 IRQ 모드로 바뀐다면 ARM은 자동으로 `R14_irq`에 다음에 실행할 명령어 위치(PC+4)를 저장한다.
개발자는 IRQ 모드의 익셉션 핸들러의 마지막에서 `R14_irq`에 저장된 값을 통해서 원래 흐름으로 복귀할 수 있다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153873175-73111808-607b-4abd-88d8-5dc18e7f4b26.png" alt="익셉션별 복귀 주소" />
</p>

위의 예시가 하나도 이해가 안될 수 있다. 우리는 지금 모드가 무엇인지도 건너뛰고 상황을 보고 있는 것이다.
나중에 다 나오니 익셉션으로 깨진 프로그램 흐름을 어떻게 복구하는지에 대한 맥락만 파악하면 될 것 같다.

위 내용을 보다 자세히 정리하자면 아래와 같다.
1. ARM 모드일 때는 익셉션에 따라 PC+4 혹은 PC+8을 R14_x(x는 각 익셉션 동작 모드)에 저장
2. CPSR(Current Program Status Register, CPSR)을 익셉션별 동작 모드에 연결된 SPSR_x(Saved PSR)에 저장
3. CPSR의 동작 모드 비트와 I, T 비트의 값을 각 익셉션과 동작 모드에 맞게 변경
4. SCTLR(System Control Register)의 EE 비트 값에 따라 E 비트를 설정
5. SCTLR의 TE 비트 값에 따라 T 비트를 설정
6. PC의 값을 익셉션 벡터 위치로 강제 변경

위의 용어는 다시 한번 말하지만 몰라도 된다. 밑에서 계속 설명할 예정이기 때문이다.
전반적인 흐름만 저렇다는 것을 알아두면 될 것 같다.

## 인터럽트
**인터럽트**는 이름 그대로 **프로그램의 흐름을 누군가 가로채는 것**을 말한다.
ARM에서는 인터럽트와 익셉션이 차이없이 동작하므로, ARM에서 인터럽트가 발생했을 때 이를 처리하는 인터럽트 핸들러가 익셉션 핸들러와 같은 개념이라 생각해도 된다.
인터럽트와 익셉션을 의미지적으로 구분하면 인터럽트는 **외부 요인에 의해서 발생하는 것**이다.

예를 들면 버튼을 누르거나 키보드를 누르거나 그러한 행위를 통해서 인터럽트가 발생한다고 생각하면 될 것 같다.

인터럽트는 **하드웨어가 인터럽트를 감지해서 ARM에 인터럽트 신호가 입력되는 순간부터 인터럽트 핸들러가 수행되기 직전까지의 필연적인 지연이 발생**된다.
ARM은 두 종류의 인터럽트를 제공한다.

1. IRQ(Interrupt Request)
2. FIQ(Fast Interrupt Request)

인터럽트가 발생하면 익셉션 처리에 해당하는 동작을 수행하고 IRQ 익셉션 혹은 FIQ 익셉션 벡터로 PC가 변경된다.
아래에서는 인터럽트에 대한 것들 중 다섯 가지 개념에 대해 설명하고자 한다.

1. IRQ(Interrupt Request)
2. FIQ(Fast Interrupt Request)
3. NMFI(Non-maskable Fast Interrupt)
4. LIL(Low Interrupt Latency)
5. IC(Interrupt Controller)

+ IRQ : IRQ는 FIQ보다 우선순위가 낮으므로 IRQ와 FIQ가 동시 발생하면 ARM은 FIQ에 대한 처리 요청을 먼저 보낸다. (CPSR의 I 비트를 1로 설정하면 비활성화 된다.)
+ FIQ : 동작 특성은 IRQ랑 동일하다. 그러나 FIQ는 뱅크드 레지스터인 R8~R12까지의 레지스터를 갖고 있기때문에 처리 속도가 빠르다. (컨텍스트 스위칭 오버헤드를 줄이므로)
+ NMFI : NMFI를 켜면 FIQ를 비활성 할 수 없다. NMFI를 켜면 CPSR의 F비트를 0으로 클리어되며, 1이 되는 순간은 FIQ 익셉션이나 리셋 익셉션이 발생된 경우이다.
+ LIL : 인터럽트 지연을 줄이기위한 ARM의 기본 설정 기능이다. SCTLR의 FI비트로 동작유무를 확인할 수 있다.
  + LIL은 어떻게 지연을 줄일까? -> 인터럽트가 발생하면 현재 수행 중인 명령어를 끝나지 않았더라도 취소하고 인터럽트를 먼저 처리하기 때문이다.(취소된 명령은 인터럽트 처리 후에 다시 수행된다.)
+ IC : 인터럽트 처리를 전담하는 일종의 주변 장치
  + 위에 나온거처럼 ARM에서는 IRQ, FIQ만 발생함을 알 수 있으나 어디서 발생한지는 모르기에 인터럽트 컨트롤러에게 물어봐야한다.
  + 인터럽트 컨트롤러의 기능 
    + 인터럽트가 발생했을 때 해당 인터럽트의 종류를 레지스터에 기록
    + 인터럽트가 발생했을 때 ARM의 IRQ 혹은 FIQ에 맞춰 인터럽트 신호를 준다.
    + 특정 인터럽트를 골라서 마스킹을 할 수 있다. (마스킹 된 인터럽트는 비활성화 된다. (토글이므로 다시 킬 수도 있음))
    + 여러 인터럽트 간에 우선순위를 설정할 수 있다.

인터럽트가 발생하면 일반적으로 아래와 같은 흐름이 흘러간 뒤에 인터럽트 서비스 루틴으로 진입한다.
여기서 말하는 인터럽트 서비스 루틴은 **인터럽트 핸들러에서 인터럽트의 종류를 판별한 다음 해당 인터럽트만 전담하는 코드**를 뜻한다.

1. 인터럽트 컨트롤러에서 인터럽트 소스가 어떤 것인지 판별
2. 인터럽트 소스에 따라 실행해야할 인터럽트 서비스 루틴 선택
3. 해당 인터럽트 소스를 비활성화하고 인터럽트 서비스 루틴으로 진입


## Abort
**Abort**의 정의는 문제가 일어났는지 보고하지 않고 프로그램의 동작이 더 이상 진행되지 않도록 하는 것이다.
ARM에서는 인터럽트와 함께 abort를 익셉션의 한 종류로 정의되어 있다. 인터럽트가 데이터 처리를 위해 정상적인 프로그램의 흐름을 끊는 익셉션이면, abort는 비정상적인 동작으로 인하여 프로그램의 흐름을 끊는 익셉션이다.
다음의 세 가지 경우에 **Abort**가 발생한다.

1. MPU(Memory Protection Unit)로 보호되는 메모리 영역에 접근 권한 없이 접근하는 경우
2. AMBA(SoC의 주변장치 연결 및 관리를 위한 공개표준) 메모리 버스가 에러를 응답했을 경우
3. ECC(데이터의 전송, 교환 과정에서 데이터의 오류를 제어하는 코드) 로직에서 에러가 발생했을 경우

주로 메모리와 관련된 것들인데 ARM에서 메모리를 정보를 읽는 경우는 두 가지이다.

1. 명령어를 읽는 경우
2. 데이터를 읽는 경우 
3. 메모리나 다른 것들이 모두 정상이나 명령어가 ARM이 모르는 경우

1의 경우에 abort 발생 시 **prefetch abort** 익셉션이 발생하고, 2의 경우에는 **data abort** 익셉션이 발생하며, 3의 경우에는 **undefined instruction** 익셉션이 발생한다.
3의 경우에는 익셉션 핸들러를 통해서 처리를 할 수도 있다.

## 동작 모드와 뱅크드 레지스터
32비트 ARM 프로세서를 기준으로 동작모드를 설명해보고자 한다.

+ User 모드 (USR) : 일반적으로 사용하는 모드로 아래의 두가지 상태로 동작한다.
  1. ARM (32비트 명령어 집합)
  2. Thumb (16비트 명령어 집합)
+ Fast Interrupt 모드(FIQ) : FIQ 익셉션 발생 시 전환되는 모드 (ARM 상태일 떄만 동작)
+ Interrupt 모드(IRQ) : IRQ 익셉션이 발생 시 전환되는 모드 (ARM, Thumb 상태 둘 다 동작)
+ Supervisor 모드(SVC) : 운영체제 등에서 시스템 코드를 수행하기 위한 보호 모드
  + 보통 시스템 콜을 호출하면 SVC 익셉션을 발생시켜 SVC 모드로 전환 후에 커널 동작 수행
+ Abort 모드(ABT) : Data abort나 Prefetch abort가 발생 시 전환되는 모드
+ System 모드(SYS) : 운영체제 등에서 사용자 프로세스가 임시로 커널 모드를 획득하는 경우 사용
+ Undefined 모드(UND) : Undefined instruction 발생 시 진입되는 모드

각 동작 모드에 따라서 각기 다른 레지스터를 사용하기도 하고, 공유해서 사용하기도 한다.
동작 모드별로 사용할 수 있는 레지스터의 최대 개수는 범용 레지스터 16개와 상태 레지스터 2개이다.
위의 동작 모드가 7개 이므로 범용 레지스터는 16 * 7 = 112개가 필요할 것같으나 ARM은 범용레지스터 31개만 갖고 있다.
따라서, 일부 레지스터는 여러 동작 모드가 공유해서 사용해야된다. 상태 레지스터도 마찬가지이다. (6개이므로)


<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153880626-a39e4112-0c5f-4d8f-ad55-85c45446be2b.png" alt="범용 레지스터 및 뱅크드 레지스터" />
</p>

위는 ARM의 범용 레지스터와 뱅크드 레지스터를 나타낸 표이다.
그러면 각각 레지스터는 어떤 역할을 할까? 

+ R0 ~ R12 : 범용 레지스터로 부르며, 펌웨어가 데이터를 일반적으로 처리할 때 사용하는 레지스터
+ R13 : 스택 포인터(Stack Pointer, SP) 레지스터이며, 대부분의 소프트웨어는 스택 기반으로 동작하기에 이를 통해서 스택 위치를 추적한다.
+ R14 : 링크 레지스터(Link Register, LR) : 소프트웨어는 함수들의 호출로도 이뤄지는데 A() 함수 내부에 B() 함수가 있으며, B함수가 끝나고 A함수를 실행할 때 리턴 어드레스를 저장하는 레지스터이다.
  + ARM은 BL, BLX와 같은 분기 명령어를 통해서 서브 루틴으로 점프하는데 이때 하드웨어가 자동으로 LR에 리턴 어드레스를 넣어준다.
+ R15 : 프로그램 카운터(Program Counter, PC) : 메모리에서 명령어를 읽어서 실행하고 그 다음 명령어를 읽을 때 다음 명령어의 메모리 주소를 저장하는 레지스터

FIQ 모드의 R8 ~ R12까지는 FIQ 모드에서만 쓸 수 있게 배정되어 있는데, 각 동작 모드에서 공유하지 않고 전용으로 사용하는 레지스터를 뱅크드 레지스터라 부른다.
또한, 각 동작 모드별로 SP와 LR을 통해서 독립된 스택 영역을 유지하고 다른 모드로 동작해야하기 때문에 SL와 LR 그리고 SPSR을 뱅크드 레지스터로 갖는다.

## 프로그램 상태 레지스터(Program Status Register, PSR)
프로세서의 상태 외에도 프로그램이 동작하면서 생기는 많은 상태가 존재하는데 이를 관리하는 레지스터이다.
현재 상태를 저장하는 프로그램 상태 레지스터는 CPSR(Current PSR)라 부르며, 상태를 저장하는 레지스터는 SPSR(Saved PSR)라 부른다.

지금까지 많은 내용을 알게되었는데, 익셉션 복구에 관련된 상세흐름을 다시 한번 봐보자.

1. ARM 모드일 때는 익셉션에 따라 PC+4 혹은 PC+8을 R14_x(x는 각 익셉션 동작 모드)에 저장
2. CPSR(Current Program Status Register, CPSR)을 익셉션별 동작 모드에 연결된 SPSR_x(Saved PSR)에 저장
3. CPSR의 동작 모드 비트와 I, T 비트의 값을 각 익셉션과 동작 모드에 맞게 변경
4. SCTLR(System Control Register)의 EE 비트 값에 따라 E 비트를 설정
5. SCTLR의 TE 비트 값에 따라 T 비트를 설정
6. PC의 값을 익셉션 벡터 위치로 강제 변경

여기서 이제 대부분의 값들을 이해할 수 있게 될 것이다. 근데 I, T 비트나 EE비트, T 비트는 무엇일까? 
이를 확인하기 위해서는 PSR의 구조를 봐야한다. 

참고로, CPSR, SPSR 모두 구조는 동일하다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153882599-d4e86aa3-5a47-42ad-8b0e-c039233b2697.png" alt="PSR 구성" />
</p>

각각 비트별로 어떤 의미를 갖는 지는 [ARM Mode와 PSR..너희들은 누구냐?](http://recipes.egloos.com/5618965) 해당 링크를 참고하고, 우리는 다시 본문으로 돌아가자.

## 익셉션 벡터 테이블 만들기 (계속)

다시 본문으로 돌아왔다. 이제 어느정도 ARM 아키텍처에 대한 기초지식이 함양되었다.
익셉션 벡터 테이블 코드를 아래와 같이 작성하였다.

```
.text
	.code 32

	.global vector_start
	.global vector_end

	vector_start:
		LDR		PC, reset_handler_addr ; 전원이 켜진 경우에 가져오는 익셉션 벡터 오프셋 주소
		LDR 	PC, undef_handler_addr ; 잘못된 명령어를 실행의 경우에 가져오는 익셉션 벡터 오프셋 주소
		LDR 	PC, svc_handler_addr ; SVC 명령으로 발생 시 가져오는 익셉션 벡터 오프셋 주소 
		LDR 	PC, pftch_abt_handler_addr ; 명령어 메모리에서 명령어를 읽다 문제가 생기면 가져오는 익셉션 벡터 오프셋 주소
		LDR 	PC, data_abt_handler_addr ; 데이터 메모리에서 데이터를 읽다 문제가 생기면 가져오는 익셉션 벡터 오프셋 주소
		B		.
		LDR 	PC, irq_handler_addr ; IRQ 인터럽트 발생 시 가져오는 익셉션 벡터 오프셋 주소
		LDR		PC, fiq_handler_addr ; FIQ 인터럽트 발생 시 가져오는 익셉션 벡터 오프셋 주소 

		reset_handler_addr:			.word rest_handler
		undef_handler_addr:			.word dummy_handler
		svc_handler_addr:				.word dummy_handler
		pftch_abt_handler_addr: .word dummy_handler
		data_abt_handler_addr: 	.word dummy_handler
		irq_handler_addr:				.word dummy_handler
		fiq_handler_addr:				.word dummy_handler

	vector_end:
	
	reset_handler:
		LDR		R0, =0x10000000
		LDR		R1, [R0]
	
	dummy_handler:
		B		.
.end
```

이제는 어느정도 이해가 되는 것을 확인할 수 있을 것이다.
익셉션 핸들러는 아직, `reset_handler` 만 만들어둔 상태이고, 나머지는 `dummy_handler` 에 매핑된 코드이다.

자 이제 익셉션 핸들러를 만들어야할 차례이다.

## 익셉션 핸들러 만들기
가장 먼저 만들어야할 익셉션은 우리가 만들었던 `reset_handler` (리셋 익셉션 핸들러) 이다.
아직 단순한 작업만 해둔 상태인데 본격적인 작업이 필요한 것이다.

그렇다면 리셋 익셉션 핸들러에서 가장 먼저 할 일은 무엇인가?
바로, **메모리 맵 설정** 작업이다.

이제 우리가 설계했던 동작 모드별 스택 주소를 각 동작 모드의 뱅크드 레지스터 SP에 설정하는 작업을 수행할 것이다.
이 스택이 모두 설정되면 C언어 `main()` 으로 진입이 가능해지는데 이렇게 되면 어셈블리어가 아닌 C언어로 임베디드 시스템을 제어할 수 있다.

## 스택 만들기
자 이제 위에 나온 내용과 같이 익셉션 모드별로 스택을 설정해보자.

+ 스택 주소를 정의하는 MemoryMap.h

```.h
#define INST_ADDR_START     0
#define USRSYS_STACK_START  0x00100000 // USR, SYS 모드 스택
#define SVC_STACK_START     0x00300000 // SVC 모드 스택
#define IRQ_STACK_START     0x00400000 // IRQ 모드 스택
#define FIQ_STACK_START     0x00500000 // FIQ 모드 스택
#define ABT_STACK_START     0x00600000 // ABT 모드 스택
#define UND_STACK_START     0x00700000 // UND 모드 스택 
#define TASK_STACK_START    0x00800000 // 태스크 영역 스택
#define GLOBAL_ADDR_START   0x04800000 // 전역변수 영역 스택
#define DALLOC_ADDR_START   0x04900000 // 동적할당 영역 스택
// 각각의 스택 사이즈를 계산
#define INST_MEM_SIZE       (USRSYS_STACK_START - INST_ADDR_START) 
#define USRSYS_STACK_SIZE   (SVC_STACK_START - USRSYS_STACK_START)
#define SVC_STACK_SIZE      (IRQ_STACK_START - SVC_STACK_START)
#define IRQ_STACK_SIZE      (FIQ_STACK_START - IRQ_STACK_START)
#define FIQ_STACK_SIZE      (ABT_STACK_START - FIQ_STACK_START)
#define ABT_STACK_SIZE      (UND_STACK_START - ABT_STACK_START)
#define UND_STACK_SIZE      (TASK_STACK_START - UND_STACK_START)
#define TASK_STACK_SIZE     (GLOBAL_ADDR_START - TASK_STACK_START)
#define DALLOC_MEM_SIZE     (55 * 1024 * 1024)
// 스택의 TOP을 처리하는 부분
#define USRSYS_STACK_TOP    (USRSYS_STACK_START + USRSYS_STACK_SIZE  - 4)
#define SVC_STACK_TOP       (SVC_STACK_START + SVC_STACK_SIZE - 4)
#define IRQ_STACK_TOP       (IRQ_STACK_START + IRQ_STACK_SIZE - 4)
#define FIQ_STACK_TOP       (FIQ_STACK_START + FIQ_STACK_SIZE - 4)
#define ABT_STACK_TOP       (ABT_STACK_START + ABT_STACK_SIZE - 4)
#define UND_STACK_TOP       (UND_STACK_START + UND_STACK_SIZE - 4)
```

+ 동작 모드 전환 값을 상수로 갖고 있는 ARMv7AR.h

```.h
#define ARM_MODE_BIT_USR 0x10
#define ARM_MODE_BIT_FIQ 0x11
#define ARM_MODE_BIT_IRQ 0x12
#define ARM_MODE_BIT_SVC 0x13
#define ARM_MODE_BIT_ABT 0x17
#define ARM_MODE_BIT_UND 0x1B
#define ARM_MODE_BIT_SYS 0x1F
#define ARM_MODE_BIT_MON 0x16
```

이제 리셋 익셉션 핸들러 부분을 고쳐야할 차례이다.
아래의 코드 블럭을 활용해서 고칠 예정이다.

```
		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #동작 모드 상수 (ARMv7AR.h에 정의한 값)
		MSR cpsr, r1
		LDR sp, =스택 TOP 주소 (MemoryMap.h에 정의한 모드별 TOP 주소 값)
```

+ 동작 모드 스택 초기화 리셋 익셉션 핸들러

```
...
  reset_handler: ; 각 모드별 스택 초기화
		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_SVC
		MSR cpsr, r1
		LDR sp, =SVC_STACK_TOP

		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_IRQ
		MSR cpsr, r1
		LDR sp, =IRQ_STACK_TOP

		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_FIQ
		MSR cpsr, r1
		LDR sp, =FIQ_STACK_TOP

		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_ABT
		MSR cpsr, r1
		LDR sp, =ABT_STACK_TOP

		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_UND
		MSR cpsr, r1
		LDR sp, =UND_STACK_TOP

		MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_SYS
		MSR cpsr, r1
		LDR sp, =USRSYS_STACK_TOP
...
```

위와 같은 파일들로 스택을 구현하였다.
그런데 스택의 TOP을 구하는 MemoryMap.h를 보면

> #define SVC_STACK_TOP       (SVC_STACK_START + SVC_STACK_SIZE - 4)

와 같이 처리되는데 이 이유는 스택은 FILO(First In Last Out)이기 때문에 TOP은 해당 스택의 영역에서 가장 높은 메모리 주소 값이 될 것이다.

따라서, 스택의 TOP을 구하는 공식은 **스택의 시작주소 + 스택의 크기 - 4** 인 것이다.
여기서 4는 32bit ARM이 4바이트를 활용한다하였는데 4가 아니라면 너무 따닥따닥 붙어있어서 -4바이트만큼 Padding을 준것이다.

자 이제 빌드를 해보자.


<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153904324-cf49cf4c-c928-472b-9a20-b241c8622082.png" alt="헤더 파일을 못 불러서 생기는 에러" />
</p>

위의 에러 내용 중 몇 개는 어셈블러에 단 주석으로 인한 에러였고, 실제 문제되는 에러는 우리가 작성한 헤더파일의 값을 못 불러오는 것이다.

생각해보니 `include` 폴더 내부에 헤더 파일을 만들어뒀으나 `MakeFile` 에 해당 위치를 기입을 안했었다.
이를 수정하자.

```
ARCH = armv7-a
MCPU = cortex-a8

CC = arm-none-eabi-gcc
AS = arm-none-eabi-as
LD = arm-none-eabi-ld
OC = arm-none-eabi-objcopy
 ...
INC_DIRS = include
  ..
build/%.o: boot/%.S
  mkdir -p $(shell dirname $@)
  $(AS) -march=$(ARCH) -mcpu=$(MCPU) -I $(INC_DIRS) -g -o $@ $<
```

위와 같이 수정 후에 실행해도 동일한 에러가 나온다.
이유가 무엇일까?

C언어에서 `#define` 은 전처리기가 담당해준다. 그러나, `arm-none-eabi-as` 는 어셈블러일 뿐이고 전처리 기능은 탑재되어있지 않다.
따라서 이를 우리가 `MakeFile` 에 상수로 선언해둔 **CC(arm-none-eabi-gcc)** 로 바꿔줘서 C의 전처리 기능을 활용해야 한다.

MakeFile의 마지막 줄을 다음과 같이 고쳐보자. 

`$(CC) -march=$(ARCH) -mcpu=$(MCPU) -I $(INC_DIRS) -g -o $@ $<` 

하지만 또 빌드가 안되고 이상한 에러가 발생하는 것을 볼 수가 있다.
이는 GCC 옵션때문이다. 오브젝트 파일을 생성하기 위해서 `-c` 옵션이 필요하다.

`$(CC) -march=$(ARCH) -mcpu=$(MCPU) -I $(INC_DIRS) -g -o -c $@ $<` 

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153905362-ffc4e4c6-cba0-4274-bca3-ff23f627f150.png" alt="빌드 성공" />
</p>

위의 그림과 같이 빌드가 성공적으로 된 것을 볼 수 있다.
다음으로 우리가 만든 스택이 제대로 올라갔는지 확인해보자.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153905615-00c77d9e-d07f-41b5-adc9-9a28f22a56dc.png" alt="빌드 성공" />
</p>

한 줄씩 실행해보니 우리가 만든 명령어를 정상적으로 받고있으며, 명령어를 보니 SVC 모드 스택을 만들고 있음을 확인할 수 있다.
`i r` 명령어를 통해서 레지스터를 확인해보면, sp쪽에 `0x3ffffc 0x3ffffc` 라는 출력이 보인다.

우리는 SVC 모드 스택을 0x00300000 ~ 0x003FFFFF까지 메모리를 할당하였으며, 4바이트를 비워두기로 했으니 스택 포인터 값이 의도한 대로 설정됐음을 확인할 수 있다.
> 0x003FFFFF - 4 = 0x003FFFFC 이므로 

또한, cpsr도 보면 마지막 바이트가 0xd3인데 이 값을 2진수로 바꾸면 11010011이고, 마지막 하위 5비트만 잘라보면 10011이다.
이걸 16진수로 바꾸면 0x13인데 이 값은 우리가 **ARMv7AR.h** 에서 선언한 `#define ARM_MODE_BIT_SVC 0x13` 과 일치한다.

즉, 우리 의도대로 스택이 만들어졌음을 확인할 수 있다.

## 메인으로 진입하기
이제 C언어 `main()` 함수로 진입하는 것을 구현해보고자 한다.

그러기 위해선 우선 **Entry.S**에 브랜치 명령어를 통해 main으로 진입할 수 있게 해주자.

```
...
	MRS r0, cpsr
		BIC r1, r0, #0x1F
		ORR r1, r1, #ARM_MODE_BIT_SYS
		MSR cpsr, r1
		LDR sp, =USRSYS_STACK_TOP

		BL	main
...
```

테스트로 진입할 `main()` 함수도 작성하자

```.c
#include "stdint.h" 

void main(void)
{
  uint32_t* dummyAddr = (uint32_t*)(1024*1024*100);
  *dummyAddr = sizeof(long);
}
```

그런데 궁금한 점이 어셈블러에서 C언어의 main() 함수로 점프하는게 아주 단순하게 `BL main` 으로 처리되는 이유가 무엇일까? 
브랜치 명령(BL)을 통해서 점프하려면 점프 대상 레이블이 같은 파일 안에 있어야한다. 다른 곳에 있다면 `.global` 로 선언해야되는데 컴파일러는 C언어 함수 이름을 링커가 자동으로 접근할 수 있는 전역 심벌로 만든다.
C에서는 전역 심벌을 나타내는 것이 `extern` 이고, 어셈블러에서는 `.global` 지시어이다.

여기서 우리는 `.vector_start` 와 `.vector_end` 를 `.global` 지시어로 만들었다. 
즉, 이 내부에서 브랜치 명령어를 통해서 C언어에서 함수 호출로 진입할 수 있는 것이다.

이제 위의 코드들을 돌려보기 위해서 다시 `MakeFile` 을 수정할 차례이다.
이번에는 변경점이 많아서 [MakeFile](https://github.com/dailyworker/study/blob/703c2eb1ddb9d2d843dc7ef9e48f00fb274ffb01/embedded-os/Makefile)을 직접 참고하는 걸 추천한다.

나도 서적에 나온 변경점만 확인하면서 짰다가 다른 변경점을 놓쳐서 헤맸었다.

여기서 중요 포인트만 집고가자면 `MAP_FILE` 을 선언하였는데 이건 링커가 만들어준다.
링커가 링킹 작업을 할 때 심벌에 할당된 메모리 주소를 map파일에 기록한다.
그리고 링커에 파라미터로 C_OBJS를 넘겨서 C언어 파일을 컴파일해서 오브젝트 파일로 생성한다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153908061-76c0fb4b-cb6c-4858-b112-a77d26a87a02.png" alt="새로운 MakeFile로 빌드" />
</p>

빌드가 성공적으로 됐고, C언어 코드가 제대로 동작하는 지 확인할 차례이다.
잠깐 우리가 작성한 C 파일을 다시 보자.

```.c
#include "stdint.h" 

void main(void)
{
  uint32_t* dummyAddr = (uint32_t*)(1024*1024*100);
  *dummyAddr = sizeof(long);
}
```

아주 단순하게 100MB의 위치에 4바이트를 기록한 함수이며, 이를 검증하기 위해서 `0x640000` 주소에 4가 저장되어있으면 된다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153908069-95f062b9-e308-43f3-95e3-970f219b4a55.png" alt="main()의 값 확인" />
</p>

`x/8wx` 명령어는 메모리 주소부터 8개를 4바이트씩 16진수로 출력하는 명령이다.
확인해보니 0x0000004로 변경되어있음을 확인할 수 있다.



