package com.Tigggle.Service;

import com.Tigggle.Constant.Transaction.Frequency;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Asset;
import com.Tigggle.Entity.Transaction.ScheduledTransaction;
import com.Tigggle.Repository.Transaction.AssetRepository;
import com.Tigggle.Repository.Transaction.ScheduledTransactionRepository;
import com.Tigggle.Repository.Transaction.TransactionRepository;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.ScheduledTransactionService;
import com.Tigggle.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestConfig.class)
public class SchedulingServiceTest {

    @Autowired
    private ScheduledTransactionService scheduledTransactionService;

    @Autowired
    private ScheduledTransactionRepository scheduledTransactionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository memberRepository;

    @Autowired
    private AssetRepository assetRepository;

//    @Test
//    @DisplayName("매월 25일 정기 거래가 오늘 날짜일 때, 실제 거래내역이 생성된다")
//    void executeMonthlySchedule_Success() {
//        // given (준비): 25일에 실행될 정기 거래 규칙을 DB에 미리 저장합니다.
//        Member testMember = memberRepository.save(new Member(...));
//        Asset testAsset = assetRepository.save(new Asset(..., testMember, ...));
//
//        ScheduledTransaction monthlySchedule = new ScheduledTransaction();
//        monthlySchedule.setAsset(testAsset);
//        monthlySchedule.setFrequency(Frequency.MONTHLY);
//        monthlySchedule.setDayOfExecution(25); // 실행일을 25일로 설정
//        monthlySchedule.setActive(true);
//        // ... (나머지 필수 필드 설정)
//        scheduledTransactionRepository.save(monthlySchedule);
//
//        long beforeCount = transactionRepository.count();
//
//        // when (실행): 스케줄링 서비스를 직접 실행합니다.
//        scheduledTransactionService.executeScheduledTransactions();
//
//        // then (검증): 실제 Transaction이 하나 더 생성되었는지 확인합니다.
//        long afterCount = transactionRepository.count();
//        assertEquals(beforeCount + 1, afterCount, "정기 거래가 생성되어야 합니다.");
//    }

}
