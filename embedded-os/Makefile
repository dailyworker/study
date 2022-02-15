# RealViewPB의 아키텍처와 CPU 정보 및 타겟 보드 정보
ARCH = armv7-a
MCPU = cortex-a8

TARGET = rvpb 

# 툴체인 상수화 
CC = arm-none-eabi-gcc
AS = arm-none-eabi-as
LD = arm-none-eabi-ld
OC = arm-none-eabi-objcopy
# 사용되는 링커스크립트 이름
LINKER_SCRIPT = ./navlios.ld
MAP_FILE = build/navlios.map
# 어셈블리어 소스 파일 목록과 오브젝트 파일 목록을 가져온다.
ASM_SRCS = $(wildcard boot/*.S) 
# 확장자.S를 .o로 바꾼다음에 디렉터리도 build로 만들어 ASM_OBJ변수에 저장
# 실제 오브젝트 파일로 생성하는 부분은 아래에서 수행
ASM_OBJS = $(patsubst boot/%.S, build/%.os, $(ASM_SRCS)) 

VPATH = boot \
        hal/$(TARGET)

C_SRCS = $(notdir $(wildcard boot/*.c))
C_SRCS += $(notdir $(wildcard hal/$(TARGET)/*.c))
C_OBJS = $(patsubst %.c, build/%.o, $(C_SRCS))

# include 파일 정보
INC_DIRS  = -I include 			\
            -I hal	   			\
            -I hal/$(TARGET)

CFLAGS = -c -g -std=c11

# 최종 ELF 파일 이름과 바이너리 파일 이름 
navlios = build/navlios.axf
navlios_bin = build/navlios.bin
# node.js의 package.json의 script와 같이 어떤 명령을 받는지 명시
# all : , clean : 은 PHONY에 명시된 명령에 대한 처리를 담당
.PHONY: all clean run debug gdb

all: $(navlios)

clean:
	@rm -fr build

run: $(navlios)
	qemu-system-arm -M realview-pb-a8 -kernel $(navlios) -nographic

debug: $(navlios)
	qemu-system-arm -M realview-pb-a8 -kernel $(navlios) -S -gdb tcp::1234,ipv4

gdb:
	arm-none-eabi-gdb

# 링커를 통한 실행파일 생성 부분
$(navlios): $(ASM_OBJS) $(C_OBJS) $(LINKER_SCRIPT)
	$(LD) -n -T $(LINKER_SCRIPT) -o $(navlios) $(ASM_OBJS) $(C_OBJS) -Map=$(MAP_FILE)
	$(OC) -O binary $(navlios) $(navlios_bin)

# *.S 파일을 .o로 컴파일 
build/%.o: %.c
	mkdir -p $(shell dirname $@)
	$(CC) -march=$(ARCH) -mcpu=$(MCPU) $(INC_DIRS) $(CFLAGS) -o $@ $<

build/%.os: %.S
	mkdir -p $(shell dirname $@)
	$(CC) -march=$(ARCH) -mcpu=$(MCPU) $(INC_DIRS) $(CFLAGS) -o $@ $<
