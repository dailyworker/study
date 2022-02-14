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
		
	dummy_handler:
		B		.
.end
