package com.Tigggle.Service.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.DTO.Transaction.AssetListDto;
import com.Tigggle.DTO.Transaction.TransactionCreateRequestDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.Tigggle.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

     private final AssetRepository assetRepository;
     private final CashRepository cashRepository;
     private final UserRepository memberRepository;
     private final OrdinaryRepository ordinaryRepository;
     private final TransactionRepository transactionRepository;
     private final KeywordsRepository keywordsRepository;
     private final CardRepository cardRepository;
     private final CheckCardRepository checkCardRepository;
     private final CreditCardRepository creditCardRepository;
     private final CreditCardTransactionRepository creditCardTransactionRepository;
     private final CreditCardInstallmentPaymentRepository creditCardInstallmentPaymentRepository;

     private final AssetService assetService;

     //! 디폴트 관련
     //* ○ "지갑"을 클릭했을 시, 디폴트 지갑자산을 결정해주는 메서드.
     public Asset determineDefaultWalletAsset(Long memberId) {
          // 1. Repository를 통해 '지갑'에 해당하는 모든 자산(현금, 보통예금)을 조회합니다.
          List<Asset> walletAssets = assetRepository.findCashAndOrdinaryAssetsByMemberId(memberId);

          // 2. 비어있는지 확인 (둘 다 없는 경우)
          if (walletAssets.isEmpty()) {
               return null; // 자산이 없음을 알리기 위해 null 반환
          }

          // 3. 우선순위 적용: OrdinaryAccount가 있는지 먼저 확인합니다.
          // walletAssets 리스트에서 타입이 OrdinaryAccount인 것을 찾아보고, 있다면 첫 번째 것을 반환합니다.
          Optional<Asset> ordinaryAccount = walletAssets.stream()
                  .filter(asset -> asset instanceof OrdinaryAccount)
                  .findFirst();

          if (ordinaryAccount.isPresent()) {
               return ordinaryAccount.get(); // OrdinaryAccount가 존재하므로, 이 자산이 기본값이 됨
          }

          // 4. OrdinaryAccount가 없다면, Cash가 유일한 옵션이므로 첫 번째 자산을 반환합니다.
          // 이 시점에 리스트에는 Cash만 남아있게 됩니다.
          return walletAssets.get(0);
     }
     //! 디폴트 관련

     //! 거래내역 저장
     //* 첫 번째 분기 (수익, 지출)
     @Transactional
     public void createTransaction(TransactionCreateRequestDto createDto, Member member) {

          // DTO의 타입에 따라 로직 분기
          switch (createDto.getTransactionType()) {
               case "INCOME":
                    processIncome(createDto, member);
                    break;
               case "EXPENSE":
                    processExpense(createDto, member);
                    break;
               default:
                    throw new IllegalArgumentException("알 수 없는 거래 타입입니다.");
          }
     }

     // ** 수입 처리 로직
     private void processIncome(TransactionCreateRequestDto dto, Member member) {

          // 사용자 검증
          Asset asset = (Asset) assetRepository.findByIdAndMember(dto.getSourceAssetId(), member).
                  orElseThrow(() -> new SecurityException("자산에 대한 권한이 없습니다."));

          Transaction tx = new Transaction();
          tx.setAsset(asset);
          tx.setDescription(dto.getDescription());
          tx.setAmount(dto.getAmount());
          tx.setTransactionDate(dto.getTransactionDate());
          tx.setConsumption(false); // 수입이므로 false
          tx.setReflectOnAsset(true); // 자산에 즉시 반영
          tx.setNote(dto.getNote());
          tx.setKeyword(keywordsRepository.findById(dto.getKeywordId()).orElseThrow());

          transactionRepository.save(tx);
          assetService.updateBalance(asset.getId(), dto.getAmount(), false); // 자산 잔액 증가
     }

     // ** 지출 처리 로직
     private void processExpense(TransactionCreateRequestDto dto, Member member) {

          // 사용자 검증
          Asset asset = (Asset) assetRepository.findByIdAndMember(dto.getSourceAssetId(), member).
                  orElseThrow(() -> new SecurityException("자산에 대한 권한이 없습니다."));

          // ** 결제 수단에 따라 다시 두 번째 로직 분기
          switch (dto.getPayMethod()) {
               case "NORMAL":
                    // 타행이체는 자산에서 즉시 출금되는 거래
                    processDirectExpense(dto, true, PayMethod.NORMAL, member, asset);
                    break;

               case "MY_ACCOUNT_TRANSFER":
                    // 내계좌이체는 자산 반영 여부가 false
                    processDirectExpense(dto, false, PayMethod.MYACCOUNT, member, asset);
                    break;

               case "CREDIT_CARD":
                    // 신용카드는 별도의 테이블에 기록
                    processCreditCardExpense(dto, member);
                    break;
          }
     }

     // ** 직접 출금되는 지출 처리
     private void processDirectExpense(TransactionCreateRequestDto dto,
                                       boolean reflectOnAsset, PayMethod payMethod, Member member, Asset sourceAsset) {

          Keywords keyword = keywordsRepository.findById(dto.getKeywordId()).orElseThrow();

          Transaction expenseTx = new Transaction();
          expenseTx.setAsset(sourceAsset);
          expenseTx.setDescription(dto.getDescription());
          expenseTx.setAmount(dto.getAmount());
          expenseTx.setTransactionDate(dto.getTransactionDate());
          expenseTx.setConsumption(true); // 지출이므로 true
          expenseTx.setReflectOnAsset(reflectOnAsset);
          expenseTx.setNote(dto.getNote());
          expenseTx.setPayMethod(payMethod);
          expenseTx.setKeyword(keyword);
          transactionRepository.save(expenseTx);

          if (reflectOnAsset) {
               assetService.updateBalance(sourceAsset.getId(), dto.getAmount(), true); // 자산 잔액 감소
          }


          // *** 내 자산 안에서의 금액 이동인 경우, 해당 목적지에 입금처리
          if (payMethod == PayMethod.MYACCOUNT) {

               String incomeDesciption = String.format("'%s'로부터 %s", sourceAsset.getAlias(), "자산 이동");

               Asset destinationAsset = (Asset) assetRepository.findByIdAndMember(dto.getDestinationAssetId(), member)
                       .orElseThrow(() -> new SecurityException("목적지 자산에 대한 권한이 없습니다."));

               Transaction incomeTx = new Transaction();
               incomeTx.setAsset(destinationAsset);
               incomeTx.setDescription(incomeDesciption);
               incomeTx.setAmount(dto.getAmount());
               incomeTx.setTransactionDate(dto.getTransactionDate());
               incomeTx.setConsumption(false);
               incomeTx.setReflectOnAsset(false);
               incomeTx.setNote(dto.getNote());
               incomeTx.setKeyword(keyword);

               transactionRepository.save(incomeTx);
          }
     }

     // ** 신용카드 지출 처리 (할부 로직 포함)
     private void processCreditCardExpense(TransactionCreateRequestDto dto, Member member) {
          CreditCard card = (CreditCard) cardRepository.findByIdAndMember(dto.getCreditCardId(), member)
                  .orElseThrow(() -> new SecurityException("카드에 대한 권한이 없습니다."));

          // 1. 원본 거래내역 저장
          CreditCardTransaction originalTx = new CreditCardTransaction();
          originalTx.setCard(card);
          originalTx.setDescription(dto.getDescription());
          originalTx.setTransactionDate(dto.getTransactionDate());
          originalTx.setKeyword(keywordsRepository.findById(dto.getKeywordId()).orElseThrow());
          originalTx.setOriginalAmount(dto.getAmount());
          originalTx.setInstallment(dto.getInstallment());
          originalTx.setNote(dto.getNote());

          creditCardTransactionRepository.save(originalTx);

          // 2. 할부 개월 수에 맞춰 할부 청구 내역 생성
          LocalDate paymentDate = dto.getTransactionDate().toLocalDate().withDayOfMonth(card.getExpenseDay());
          // 이자 계산 후 발생금액을 전체지불금액에 합산
          float interestRate = card.getCreditCardInterest();
          // 이자율은 1년에 해당하는 비율이므로 한 달로 나눔
          long totalFee = (long) (dto.getAmount() * interestRate * (dto.getInstallment() / 12.0));
          long totalRepaymentAmount = dto.getAmount() + totalFee;
          long monthlyPayment = 0;
          // 할부개월이 존재(0이 아닐 경우)할 경우, 달마다 부과될 이자를 계산.
          if (dto.getInstallment() > 0) {
               monthlyPayment = totalRepaymentAmount / dto.getInstallment();
          }

          // 납부 총액 추적 변수
          long totalPaid = 0;
          for (int i = 0; i < dto.getInstallment(); i++) {
               CreditCardInstallmentPayment installmentPayment = new CreditCardInstallmentPayment();

               installmentPayment.setOriginalTransaction(originalTx);

               // 금액 나누기로 인해 1원 등이 비어서 부과될 경우를 방지
               if (i == dto.getInstallment() - 1) {
                    monthlyPayment = totalRepaymentAmount - totalPaid;
               } else {
                    totalPaid += monthlyPayment;
               }

               installmentPayment.setPaymentAmount(monthlyPayment);
               installmentPayment.setPaymentDueDate(paymentDate.plusMonths(i + 1)); // 다음달부터 청구
               installmentPayment.setSequence(i + 1);
               installmentPayment.setSettled(false);

               creditCardInstallmentPaymentRepository.save(installmentPayment);
          }
     }
     //! 거래내역 저장


}