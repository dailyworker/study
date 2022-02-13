# 리셋 벡터
ARM 코어에 전원이 들어가면 ARM 코어가 가장 먼저하는 일은 리셋 벡터에 있는 명령을 실행한다.
-> 여기서 리셋벡터는 메모리 주소 0x00000000를 뜻한다.
즉, ARM 코어에 전원이 들어오면 0x0000000에서 32비트를 읽어서 그 명령을 바로 실행한다.

## 리셋 벡터 코드 분석
```
.text
	.code 32

	.global vector_start
	.global vector_end

	vector_start:
		MOV		R0, R1
	vector_end:
		.space 1024, 0
.end
```

1. `.text` : .end가 나올 때까지 모든 코드가 text 섹션이라는 의미이다.
	+ 컴파일러가 만든 기계어가 위치하는 섹션을 뜻한다.
2. `.code 32` : 명령어의 크기가 32비트라는 의미이다.
3. `.global ...` : C언어 지시어인 extern과 같은 일을 한다. (여기서는 `vector_start`와 `vector_end`의 주소 정보를 외부 파일에서 심벌로 읽게 설정)
5. `MOV R0, R1` : R1의 값을 R0로 넣어라는 의미이다. (ARM 레지스터)
6. `.space 1024, 0` : 해당 위치부터 1024바이트를 0으로 채우라는 의미이다.

![ARM 공유 레지스터와 뱅크드 레지스터](https://user-images.githubusercontent.com/22961251/153756658-fe4af0b7-eb14-4a88-9a28-16dfcb390a79.png)

즉, 0x00000004 부터 0x00000400까지 0으로 채워지는 명령어이다.
이를 확인하기 위해서는 어셈블러로 컴파일 후 생성된 바이너리 파일을 확인하면 된다.

## 리셋 벡터 컴파일 및 바이너리 덤프 

+ Entry.S 어셈블러 컴파일 및 바이너리 덤프 명령어

```.sh
# 아키텍처는 armv7 / cpu는 코어텍스A8을 타겟으로 컴파일
arm-none-eabi-as -march=armv7-a -mcpu=cortex-a8 -o Entry.o ./Entry.S

# 바이너리 추출
arm-none-eabi-objcopy -O binary Entry.o Entry.bin

# 바이너리 덤프
hexdump Entry.bin
```

![Entry.S를 어셈블러로 컴파일 후 바이너리로 덤프](https://user-images.githubusercontent.com/22961251/153756763-5e6334c4-a30e-4ffe-a4fb-737c184af251.png)

마지막 주소가 0x00000404 인 이유는 ARM에서는 4바이트 단위로 메모리 주소를 관리하기 때문이고, 따라서 바로 앞 주소가 0x00000400이므로 정상동작임을 확인할 수 있다. 

# 실행 파일 제작

QEMU를 통해서 부팅하려면 입력으로 지정한 펌웨어 바이너리 파일이 ELF 파일 형식이여한다.
위에서 컴파일 시에 생성된 `Entry.o` 파일도 ELF 파일이다. (여기서 C를 한 사람은 눈치채겠지만 이는 오브젝트 파일이다.)

따라서, 위의 ELF 파일 중에서 바이너리만 추출하기 위해서 `arm-none-eabi-objcopy` 를 사용한 것이다.

이 ELF를 만들기 위해서는 링커가 필요한데, 링커가 동작하기 위해서는 정보를 링커에게 전달하는 **링커 스크립트**가 필요하다.

## 링커 스크립트

```
ENTRY(vector_start)
SECTIONS
{
	. = 0x0;


	.text :
	{
		*(vector_start)
		*(.text .rodata)
	}
	.data :
	{
		*(.data)
	}
	.bss :
	{
		*(.bss)
	}
}
```

1. `ENTRY` : 시작 위치의 심벌 지정
2. `SECTIONS` : 아래의 블록이 섹션 배치 설정 정보를 갖고 있다 알려줌.
3. `. = 0x0` : 첫 번째 섹션이 메모리 주소 0x00000000에 위치함을 의미한다.
4. `.text { ... }` : 위에서 리셋 벡터를 만들 때의 .text 와 비슷한데 링커 스크립트 내부에서는 .text 블록 내부에서 text 섹션의 배치 순서를 지정한다. (여기서는 리셋 벡터를 배치하기 위해서 `vector_start` 를 배치 후에 `.text` 와 `data`, `bbs` 섹션을 지정)

```.sh
:<<'END'
 -n : 링커에 섹션의 정렬을 자동으로 맞추지 말라는 옵션 (링커 스크립트를 통해서 섹션을 정렬하기 때문이다.)
 -T : 링커 스크립트의 파일명을 넘겨준다.
 첫번째 명령어가 실행되면 .axf파일이 생성된다. 이를 2번째 명령어에서 디스어셈블해서 내부를 파악할 수 있다. 
END

arm-none-eabi-ld -n -T ./navilos.ld -nostdlib -o navilos.axf boot/Entry.o
arm-none-eabi-objdump -D navilos.axf
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153757341-9adf84fd-8f30-40b1-9463-a2011679a899.png" alt="실행 파일 결과 확인" />
</p>

## 링커 스크립트로 만든 실행 파일을 QEMU에서 실행

우리가 알고 있는 실행 파일은 흔히 `.exe` 와 같은 형식일 것이다. 실제로 `.axf` 파일을 실행하려하면 에러가 발생한다. **이는 리눅스 커널에서 동작하지 않는 섹션 배치로 제작된 실행 파일이라 그렇다.** 

이를 실행하기 위해서 QEMU를 활용하면 된다.

```.sh
# 선행 설치 (sudo apt install gdb-arm-none-eabi가 18.04 이후로 동작안함)
sudo apt install gdb-multiarch

qemu-system-arm -M realview-pb-a8 -kernel navilos.axf -S -gdb tcp::1234,ipv4

gdb
```

위의 명령어는 실행파일을 QEMU에서 돌린 후 호스트와 gdb를 통해서 1234로 통신한다는 뜻이다.

두번째 줄 명령어를 수행하면 아래와 같이 QEMU 창이 같이 뜰 것이다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153757509-18c76db8-7d07-430d-985c-b494e57a4b1e.png" alt="QEMU에 실행파일을 로드 후 실행"/>
</p>

그러면 새 터미널 창을 열어서 gdb로 통신하면 아래와 같이 보인다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153757534-61670ea6-3b17-42a3-a747-9bb1d97d20f9.png" alt="QEMU GDB 통신"/>
</p>

여기서 이 결과를 보면 0xE1A00001인데 이건, 실행 파일을 덤프떠서 봤을 때 나온 값이다.
즉, QEMU에 실행파일이 제대로 로드되었음을 알 수 있다.

# 빌드 자동화

위와 같은 복잡한 과정을 계속 개발 과정속에서 반복하면 불편할 수 밖에 없다.
따라서, 빌드 자동화 도구가 필요한 시점인데 이때 `Makefile` 을 사용해서 처리하고자한다.

초기 `Makefile` 에 대한 자세한 주석은 [Makefile](https://github.com/dailyworker/study/blob/931ab99de79c7eecd1edea317c20584b6f37a97d/embedded-os/Makefile)을 참고하면된다.

만들어진 `Makefile` 통해서 `make all` 을 했을 시 결과는 아래와 같다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153758949-343aa242-86d0-44e7-b523-aa3401852a04.png" alt="Make all 결과"/>
</p>


위의 주석에서 `.PHONY` 에는 all 뿐만 아니라, debug 명령도 지원하는데 `make debug` 를 수행하면 아래와 같이 QEMU에 실행 파일을 올려서 gdb로 디버깅을 할 수 있다.

<p align="center">
  <img src="https://user-images.githubusercontent.com/22961251/153759220-4ee73cc3-e383-4b5b-b32f-06c662c22ce4.png" alt="Make all 결과"/>
</p>

# 하드웨어 정보 읽어오기

하드웨어와 상호작용한다는 것은 하드웨어에서 정보를 읽고, 하드웨어에 정보를 쓰는 것을 말한다. 이는 **레지스터가 하는 역할인데 레지스터는 하드웨어가 소프트웨어와 상호작용하는 인터페이스이다.**

+ 하드웨어의 값을 읽기 위해 변경된 리셋 벡터 

```
.text
	.code 32

	.global vector_start
	.global vector_end

	vector_start:
		LDR R0, =0x10000000
		LDR R1, [R0]
	vector_end:
		.space 1024, 0
.end
```

이전 소스와 비교하면 `vector_start` 내부의 값이 변경된 것을 알 수가 있다.
`LDR R0, =0x10000000` 는 R0 레지스터에 0x10000000 값을 대입하는 명령어이다.
`LDR R1, [RO]` 는 C언어의 포인터처럼 RO에 입력된 정보를 R1에 대입하는 것이다.

그렇다면 `0x10000000` 에는 어떤 값이 들어가있을까? 
이는 학부시절 임베디드 프로그래밍에서도 배웠던 내용인데 ARM 보드 벤더사나 ARM 아키텍처를 만드는 ARM에서 데이터시트를 제공해준다.

우리는 RealViewPB를 사용하기때문에 해당 값을 데이터시트에 확인하면 **ID Register**라 나온다. 이 레지스터는 하드웨어를 식별할 수 있는 정보를 가진 레지스터이며, 구조는 다음 이미지와 같다. 

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153759988-b8097c2b-efd9-4986-992d-8ff129a72de6.png" alt="SYS_ID 레지스터의 구조"/>
</p>

SYS_ID의 해당 항목들은 아래의 뜻을 가진다.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153760331-b4b7942d-f658-4549-b099-71379aed3b44.png" alt="SYS_ID 레지스터의 설명"/>
</p>

우리는 여기서 상수로 할당된 HBI, ARCH (기본값이 지정되어있으므로)을 확인해서 우리가 만든 실행파일로 하드웨어의 정보를 가져올 수 있는지 파악할 예정이다.

ARCH 는 0x4가 AHB, 0x5가 AXI 버스아키텍처임을 기억하자.

빌드 과정은 생략하고 설명하겠다. (위의 make 파일을 쓴 것과 동일)

1. `file build/실행파일.axf` 명령어를 통해서 실행 파일을 읽는다.
2. `list` 명령어를 통해서 디버깅 심벌을 읽는다.
3. `info register` 명령어를 통해서 초기 레지스터 값을 확인할 수 있다.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153761044-ae1a0cc0-2e3d-4eb0-b4ba-64f1cceeec79.png" alt="디버깅 심벌과 초기 레지스터 값 확인"/>
</p>

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153761111-ae788f43-edae-4eaf-b105-188f39b6fc90.png" alt="디버깅 심벌과 초기 레지스터 값 확인"/>
</p>

위의 두 이미지를 확인하면, 변경된 디버깅 심벌과 초기 레지스터 값이 아무것도 대입안된 상태임을 확인할 수 있다.

이제, `s` 명령어를 통해서 실행 파일을 한줄한줄씩 실행하면서 변경된 점을 확인하자.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153761127-df1c22f6-b8a7-4fda-b372-1a3d2cbc9266.png" alt="한줄 씩 출력하면서 레지스트리 값 변경확인"/>
</p>

초기 벡터의 `LDR R0, =0x10000000` 를 통해서 r0에 해당 주소값이 들어간 것을 확인 할 수 있다.

<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153761142-91f48cd2-3a8b-49f4-aab2-9d694f238ab2.png" alt="한줄 씩 출력하면서 레지스트리 값 변경확인"/>
</p>


초기 벡터의 `LDR R1, [R0]` 를 통해서 r1에 r0 주소에 값을 대입한다. 여기서 0x1780500이 SYS_ID 값이다. (0x10000000가 Id Register의 주소이고, 해당 값을 ARM Core가 읽어서 ID_SYS로 추출하여 대입한 결과로 0x1780500이 나온다.)

예상 할 수 있는 것은 Id Register 상수에 HBI가 기본 값으로 포함되기때문에 0x178이 예상 된 것이다.

이를 이제 분석을 해보자.
0x1780500 -> 2진수로 변경하면 00000001 01111000 00000101 00000000와 같다. 이를 다시 SYS_ID 레지스터와 비교해보자.


<p align="center">
	<img src="https://user-images.githubusercontent.com/22961251/153761153-2beed5f9-11e1-4ce0-a3c4-ca0362534bab.png" alt="SYS_ID 분석"/>
</p>

상수인 HBI와 ARCH의 값이 정확히 일치함을 알 수 있다.
또한, 위에서 설명한 SYS_ID 값 항목 표와 대치하면

REV 값이 0x0 이므로 Rev A(보드 리비전)에 해당하며, ARCH 또한 0x5에 해당하므로 버스 아키텍처는 AXI임을 알 수 있다.
