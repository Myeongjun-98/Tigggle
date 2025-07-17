package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.ScheduledTransactionCreateDto;
import com.Tigggle.DTO.Transaction.ScheduledTransactionDto;
import com.Tigggle.DTO.Transaction.ScheduledTransactionUpdateDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.ScheduledTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduledTransactionController {

    private final ScheduledTransactionService scheduledTransactionService;
    private final UserRepository memberRepository;

    @GetMapping("/api/scheduled-transactions")
    public ResponseEntity<List<ScheduledTransactionDto>> getScheduledTransactionList(Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());
        List<ScheduledTransactionDto> dtoList = scheduledTransactionService.getScheduledTransactions(member);
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/api/scheduled-transactions/create") // POST /api/scheduled-transactions 요청을 처리
    public ResponseEntity<Void> createScheduledTransaction(
            @RequestBody ScheduledTransactionCreateDto createDto,
            Principal principal) {

        // 1. Principal에서 현재 로그인한 사용자의 Member 엔티티를 조회합니다.
        Member member = memberRepository.findByAccessId(principal.getName());

        // 2. 서비스 계층에 DTO와 Member 객체를 전달하여 로직 실행을 위임합니다.
        scheduledTransactionService.createScheduledTransaction(createDto, member);

        System.out.println("Controller가 받은 DTO의 isConsumption 값: " + createDto.isConsumption());


        // 3. 성공적으로 처리되었음을 알리는 HTTP 201 Created 응답을 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/scheduled-transactions")
    public ResponseEntity<Void> deleteScheduledTransactions(
            @RequestBody List<Long> scheduleIds,
            Principal principal) {

        Member member = memberRepository.findByAccessId(principal.getName());

        scheduledTransactionService.deleteScheduledTransactions(scheduleIds, member);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/scheduled-transactions/{scheduleId}")
    public ResponseEntity<Void> updateScheduledTransaction(
            @PathVariable Long scheduleId,
            @RequestBody ScheduledTransactionUpdateDto updateDto,
            Principal principal) {

        Member member = memberRepository.findByAccessId(principal.getName());

        scheduledTransactionService.updateScheduledTransaction(scheduleId, updateDto, member);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/scheduled-transactions/{scheduleId}")
    public ResponseEntity<ScheduledTransactionDto> getScheduledTransaction(@PathVariable Long scheduleId) {
        // 서비스에 상세 DTO를 요청 (소유권 검증은 서비스에서 처리)
        ScheduledTransactionDto scheduleDto = scheduledTransactionService.getSingleScheduledTransaction(scheduleId);
        return ResponseEntity.ok(scheduleDto);
    }
}
