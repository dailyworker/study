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
