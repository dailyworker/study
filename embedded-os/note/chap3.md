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

![링커로 실행 파일 만든 결과](https://user-images.githubusercontent.com/22961251/153757341-9adf84fd-8f30-40b1-9463-a2011679a899.png)

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

![QEMU에 실행파일을 로드 후 실행](https://user-images.githubusercontent.com/22961251/153757509-18c76db8-7d07-430d-985c-b494e57a4b1e.png)

그러면 새 터미널 창을 열어서 gdb로 통신하면 아래와 같이 보인다.

![QEMU GDB 통신](https://user-images.githubusercontent.com/22961251/153757534-61670ea6-3b17-42a3-a747-9bb1d97d20f9.png)

여기서 이 결과를 보면 0xE1A00001인데 이건, 실행 파일을 덤프떠서 봤을 때 나온 값이다.
즉, QEMU에 실행파일이 제대로 로드되었음을 알 수 있다.

# 빌드 자동화

위와 같은 복잡한 과정을 계속 개발 과정속에서 반복하면 불편할 수 밖에 없다.
따라서, 빌드 자동화 도구가 필요한 시점인데 이때 `Makefile` 을 사용해서 처리하고자한다.

초기 `Makefile` 에 대한 자세한 주석은 [Makefile](https://github.com/dailyworker/study/blob/931ab99de79c7eecd1edea317c20584b6f37a97d/embedded-os/Makefile)을 참고하면된다.

만들어진 `Makefile` 통해서 `make all` 을 했을 시 결과는 아래와 같다.

![Make all 결과](https://user-images.githubusercontent.com/22961251/153758949-343aa242-86d0-44e7-b523-aa3401852a04.png)

위의 주석에서 `.PHONY` 에는 all 뿐만 아니라, debug 명령도 지원하는데 `make debug` 를 수행하면 아래와 같이 QEMU에 실행 파일을 올려서 gdb로 디버깅을 할 수 있다.

![Make debug 결과](https://user-images.githubusercontent.com/22961251/153759220-4ee73cc3-e383-4b5b-b32f-06c662c22ce4.png)

# 하드웨어 정보 읽어오기



