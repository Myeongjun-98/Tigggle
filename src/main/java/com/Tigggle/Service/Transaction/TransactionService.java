package com.Tigggle.Service.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.Tigggle.Constant.Transaction.PayMethod;
import com.Tigggle.DTO.Transaction.TransactionCreateRequestDto;
import com.Tigggle.DTO.Transaction.TransactionDetailDto;
import com.Tigggle.DTO.Transaction.TransactionUpdateDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.*;
import com.Tigggle.Repository.Transaction.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

     private final AssetRepository assetRepository;
     private final CashRepository cashRepository;
     private final TransactionRepository transactionRepository;
     private final KeywordsRepository keywordsRepository;
     private final CardRepository cardRepository;
     private final BankAccountRepository bankAccountRepository;
     private final CreditCardTransactionRepository creditCardTransactionRepository;
     private final CreditCardInstallmentPaymentRepository creditCardInstallmentPaymentRepository;
     private final ScheduledTransactionRepository scheduledTransactionRepository;

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

     //! ▼▼▼▼▼▼▼ 거래내역 저장
     //* 첫 번째 분기 (수익, 지출)
     @Transactional
     public void createTransaction(TransactionCreateRequestDto createDto, Member member) {

          if (createDto.getTransactionType() == null) {
               throw new IllegalArgumentException("거래 타입(transactionType)은 필수입니다.");
          }

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
//          Asset asset = (Asset) assetRepository.findByIdAndMember(dto.getSourceAssetId(), member).
//                  orElseThrow(() -> new SecurityException("자산에 대한 권한이 없습니다."));

          Asset asset = assetRepository.findByIdAndMemberWithLock(dto.getSourceAssetId(), member.getId()).orElseThrow(
                  () -> new IllegalArgumentException("자산을 찾을 수 없습니다."));

          Transaction tx = new Transaction();
          tx.setAsset(asset);
          tx.setDescription(dto.getDescription());
          tx.setAmount(dto.getAmount());
          tx.setTransactionDate(dto.getTransactionDate());
          tx.setConsumption(false); // 수입이므로 false
          tx.setPayMethod(PayMethod.NORMAL); // 수입 거래의 pay_method를 NORMAL로 설정합니다.
          tx.setReflectOnAsset(true); // 자산에 즉시 반영
          tx.setNote(dto.getNote());
          tx.setKeyword(keywordsRepository.findById(dto.getKeywordId()).orElseThrow());

          transactionRepository.save(tx);

          assetService.updateBalance(tx.getAsset(), dto.getAmount(), tx.isConsumption());
     }

     // ** 지출 처리 로직
     private void processExpense(TransactionCreateRequestDto dto, Member member) {

//          // 사용자 검증
//          Asset asset = (Asset) assetRepository.findByIdAndMember(dto.getSourceAssetId(), member).
//                  orElseThrow(() -> new SecurityException("올바른 자산을 선택해주세요."));

          Asset asset = assetRepository.findByIdAndMemberWithLock(dto.getSourceAssetId(), member.getId()).orElseThrow(
                  () -> new IllegalArgumentException("자산을 찾을 수 없습니다."));

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

               case "SCHEDULED":
                    // 정기 결제도 체크카드/이체처럼 자산에서 즉시 출금되는 것으로 처리
                    processDirectExpense(dto, true, PayMethod.SCHEDULED, member, asset);
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
          assetService.updateBalance(sourceAsset, dto.getAmount(), true);

          // *** 내 자산 안에서의 금액 이동인 경우, 해당 목적지에 입금처리
          if (payMethod == PayMethod.MYACCOUNT) {

               String incomeDesciption = String.format("[%s]로부터 %s", sourceAsset.getAlias(), "자산 이동");

               Asset destinationAsset = assetRepository.findByIdAndMemberWithLock(dto.getDestinationAssetId(), member.getId()).orElseThrow(
                       () -> new IllegalArgumentException("자산을 찾을 수 없습니다."));

//               Asset destinationAsset = (Asset) assetRepository.findByIdAndMember(dto.getDestinationAssetId(), member)
//                       .orElseThrow(() -> new SecurityException("목적지 자산에 대한 권한이 없습니다."));

               Transaction incomeTx = new Transaction();
               incomeTx.setAsset(destinationAsset);
               incomeTx.setDescription(incomeDesciption);
               incomeTx.setAmount(dto.getAmount());
               incomeTx.setTransactionDate(dto.getTransactionDate());
               incomeTx.setConsumption(false);
               incomeTx.setReflectOnAsset(false);
               incomeTx.setNote(dto.getNote());
               incomeTx.setKeyword(keyword);
               incomeTx.setPayMethod(PayMethod.MYACCOUNT);

               transactionRepository.save(incomeTx);

               assetService.updateBalance(destinationAsset, dto.getAmount(), false);
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
     //! ▲▲▲▲▲▲ 거래내역 저장

     // * 거래내역 자세히보기
     public TransactionDetailDto getTransactionDetail(Long transactionId, Member member) {
          Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("거래내역을 찾을 수 없습니다."));

          // 1. Keyword가 null일 경우를 대비하여 안전하게 처리
          String keywordStr;
          if (transaction.getKeyword() != null) {
               Keywords keyword = transaction.getKeyword();
               // 마이너 키워드가 없을 경우를 대비
               keywordStr = keyword.getMinorKeyword() == null
                       ? keyword.getMajorKeyword()
                       : keyword.getMajorKeyword() + " > " + keyword.getMinorKeyword();
          } else {
               keywordStr = "분류 없음";
          }

          // 2. PayMethod가 null일 경우를 대비하여 안전하게 처리
          String payMethodStr = (transaction.getPayMethod() != null)
                  ? transaction.getPayMethod().toString()
                  : "기타";

          // 3. 자산 이름도 안전하게 가져오기
          String sourceAssetName = (transaction.getAsset() != null)
                  ? transaction.getAsset().getAlias()
                  : "알 수 없는 자산";

         return new TransactionDetailDto(
                 transaction.isConsumption(),
                 transaction.getDescription(),
                 transaction.getAmount(),
                 transaction.getTransactionDate(),
                 keywordStr,
                 transaction.getKeyword().getId(),
                 payMethodStr,
                 transaction.getNote());
     }

//     // * 거래내역 삭제
//     @Transactional
//     public void deleteTransaction(List<Long> transactionIds, Member member) {
//          // 1. 소유권 검증과 함께 삭제할 거래내역을 조회합니다.
//          for(Long transactionId : transactionIds){
//          Transaction transaction = transactionRepository.findByIdAndMember(transactionId, member)
//                  .orElseThrow(() -> new SecurityException("삭제할 권한이 없는 거래내역이 포함되어 있습니다. ID: " + transactionId));
//
//               Asset assetToUpdate = transaction.getAsset();
//
//               Long a = assetRepository.findBalanceById(assetToUpdate.getId());
//
//               // 결과 변수 저장!
//               Long res;
//
//               if(transaction.isConsumption())
//               // 지울 거래내역이 지출이었을 경우, 지출했던 값을 더함
//                    res = a + transaction.getAmount();
//               // 수익이었으면 다시 뺴야함
//               else res = a - transaction.getAmount();
//
//               assetRepository.updateBalance(res, assetToUpdate.getId());
//               try {
//                    transactionRepository.delete(transaction);
//               } catch (Exception e) {
//                    System.err.println("  -> [오류!] Transaction 삭제 중 예외 발생: " + e.getMessage());
//                    // 이 예외가 트랜잭션 롤백의 원인일 가능성이 매우 높습니다.
//               }
//          }
//     }

     @Transactional
     public void deleteTransaction(List<Long> transactionIds, Member member) {
          for (Long transactionId : transactionIds) {
               Transaction transaction = transactionRepository.findByIdAndMember(transactionId, member) // member.getId() 사용 확인
                       .orElseThrow(() -> new SecurityException("권한이 없는 자산입니다."));

               // AssetService를 통해 잔액을 안전하게 복구
               assetService.updateBalance(
                       transaction.getAsset(),
                       transaction.getAmount(),
                       !transaction.isConsumption() // 논리 반전
               );

               transactionRepository.delete(transaction);
          }
     }

     // * 거래내역 수정
     @Transactional
     public void updateTransaction(Long transactionId, TransactionUpdateDto dto, Member member){
          // 1. 소유권 검증과 함께 수정할 원본 거래내역을 조회합니다.
          Transaction originalTx = transactionRepository.findByIdAndMember(transactionId, member)
                  .orElseThrow(() -> new SecurityException("수정할 권한이 없는 거래내역입니다."));

          // 2. '수정 전' 금액과 타입을 변수에 저장해 둡니다.
          Long originalAmount = originalTx.getAmount();
          Asset originalAsset = originalTx.getAsset();
          boolean originalIsConsumption = originalTx.isConsumption();

          assetService.updateBalance(originalAsset, originalAmount, !originalIsConsumption);

          // 3. '수정 후'의 새로운 값들을 DTO로부터 엔티티에 반영합니다.
          originalTx.setDescription(dto.getDescription());
          originalTx.setAmount(dto.getAmount());
          originalTx.setTransactionDate(dto.getTransactionDate());
          originalTx.setNote(dto.getNote());
          originalTx.setKeyword(keywordsRepository.findById(dto.getKeywordId()).orElseThrow());

          transactionRepository.save(originalTx);

          assetService.updateBalance(originalTx.getAsset(), originalTx.getAmount(), originalTx.isConsumption());

//          // --- 4. 자산 잔액 재계산 ---
//
//
//          // 4-1. 먼저, '수정 전' 거래의 효과를 취소합니다. (삭제 로직과 동일)
//          //       (지출이었으면 더하고, 수입이었으면 뺍니다)
//          Long balanceAfterRevert = assetRepository.findBalanceById(originalAsset.getId());
//          if (originalIsConsumption) {
//               balanceAfterRevert += originalAmount;
//          } else {
//               balanceAfterRevert -= originalAmount;
//          }
//
//          // 4-2. 그 다음, '수정 후' 거래의 효과를 새로 적용합니다.
//          //       (DTO에 isConsumption이 있다고 가정. 없다면 transactionType으로 변경)
//          Long finalBalance;
//          if (dto.isConsumption()) { // 수정 후에도 지출이라면
//               finalBalance = balanceAfterRevert - dto.getAmount();
//          } else { // 수정 후 수입이라면
//               finalBalance = balanceAfterRevert + dto.getAmount();
//          }
//
//          // 4-3. 계산된 최종 잔액을 DB에 직접 UPDATE 합니다.
//          assetRepository.updateBalance(finalBalance, originalAsset.getId());
//
//          // 5. 모든 변경사항이 반영된 Transaction 엔티티를 저장합니다.
//          transactionRepository.save(originalTx);
          }
     }

