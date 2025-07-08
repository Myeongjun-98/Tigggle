// 페이지 로드 완료 시, 모달 관련 로직을 초기화합니다.
document.addEventListener('DOMContentLoaded', () => {
    initializeCreateModal();
    initializeAlertModal();
});

// * 알림 모달 관련 함수
// 알림 모달의 요소들을 찾아 이벤트를 연결하는 함수
function initializeAlertModal() {
    const alertModal = document.getElementById('TR-alert-modal');
    const alertCloseBtn = document.getElementById('TR-alert-close-btn');

    alertCloseBtn.addEventListener('click', () => {
        alertModal.classList.add('TR-hidden');
    });
}

// 메시지를 설정하고 알림 모달을 보여주는 함수
function showAlert(message) {
    const alertModal = document.getElementById('TR-alert-modal');
    const alertMessage = document.getElementById('TR-alert-message');

    alertMessage.innerText = message;
    alertModal.classList.remove('TR-hidden');
}
// * 알림 모달 관련 함수

function initializeCreateModal() {
    // 1. 필요한 HTML 요소들을 미리 찾아 변수에 할당합니다.
    const modalOverlay = document.getElementById('transaction-modal');
    const openModalBtn = document.querySelector('.TR-actions-save'); // '내역 입력' 버튼 ID
    const closeModalBtn = document.querySelector('.close-button');

    const transactionTypeRadios = document.querySelectorAll('input[name="transactionType"]');
    const payMethodSelect = document.getElementById('TR-tx-pay-method');
    const installmentInput = document.getElementById('TR-tx-installment');
    const destinationAssetSelect = document.getElementById('TR-tx-destination-asset');

    const expenseDetailsSection = document.getElementById('expense-details');
    const creditCardDetailsSection = document.getElementById('TR-credit-card-details');
    const myAccountTransferDetailsSection = document.getElementById('TR-my-account-transfer-details');


    // --- 이벤트 리스너 등록 ---

    // 2. '내역 입력' 버튼 클릭 시 모달창 보이기
    openModalBtn.addEventListener('click', () => {
        modalOverlay.classList.remove('TR-hidden');
    });

    // 3. '닫기(X)' 버튼 클릭 시 모달창 숨기기
    closeModalBtn.addEventListener('click', () => {
        modalOverlay.classList.add('TR-hidden');
    });

    // 4. '수입/지출' 라디오 버튼 변경 시
    transactionTypeRadios.forEach(radio => {
        radio.addEventListener('change', (event) => {
            if (event.target.value === 'EXPENSE') {
                expenseDetailsSection.classList.remove('TR-hidden');
            } else { // INCOME 선택 시
                expenseDetailsSection.classList.add('TR-hidden');

                // 지출 관련 세부 섹션을 모두 숨기고, 그 안의 값도 초기화/비활성화합니다.
                creditCardDetailsSection.classList.add('TR-hidden');
                installmentInput.value = '0'; // 할부 개월 수 초기화

                myAccountTransferDetailsSection.classList.add('TR-hidden');
                destinationAssetSelect.value = ''; // 목적지 자산 선택 초기화
            }
        });
    });

    // 5. '결제 수단' 선택 변경 시
    payMethodSelect.addEventListener('change', (event) => {
        const selectedMethod = event.target.value;

        // ! ▼▼▼▼▼ 디버깅을 위해 이 한 줄을 추가해주세요 ▼▼▼▼▼
        console.log('선택된 결제 수단 값:', selectedMethod);
        // ! ▲▲▲▲▲ 디버깅을 위해 이 한 줄을 추가해주세요 ▲▲▲▲▲

        // 신용카드 할부 섹션 처리
        if (selectedMethod === 'CREDIT_CARD') {
            creditCardDetailsSection.classList.remove('TR-hidden');
        } else {
            creditCardDetailsSection.classList.add('TR-hidden');
            installmentInput.value = '0'; // 신용카드가 아니면 할부 개월 수 초기화
        }

        // 내 계좌 이체 섹션 처리
        if (selectedMethod === 'MY_ACCOUNT_TRANSFER') {
            myAccountTransferDetailsSection.classList.remove('TR-hidden');
        } else {
            myAccountTransferDetailsSection.classList.add('TR-hidden');
            destinationAssetSelect.value = ''; // 내 계좌 이체가 아니면 목적지 자산 초기화
        }

        });



    // * 폼 제출 이벤트 처리
    const form = document.getElementById('TR-create-form')

    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // 폼의 기본 제출 동작(새로고침)을 막습니다.

        // 1. DTO에 담을 데이터 객체 생성
        const createDto = {};

        // 2. 현재 선택된 값을 기준으로 DTO 객체를 채웁니다.
        const transactionType = form.querySelector('input[name="transactionType"]:checked').value;
        createDto.transactionType = transactionType;

        // 공통 필드 값 가져오기
        createDto.transactionDate = document.getElementById('tx-date').value;
        createDto.amount = document.getElementById('tx-amount').value;
        createDto.description = document.getElementById('tx-description').value;
        createDto.keywordId = document.getElementById('tx-keyword').value;
        createDto.note = document.getElementById('tx-note').value;

        // 지출(EXPENSE)일 경우에만 추가 정보 수집
        if (transactionType === 'EXPENSE') {
            const payMethod = document.getElementById('tx-pay-method').value;
            createDto.payMethod = payMethod;
            createDto.sourceAssetId = document.getElementById('tx-source-asset').value;

            // 신용카드일 경우 할부 정보 수집
            if (payMethod === 'CREDIT_CARD') {
                createDto.creditCardId = document.getElementById('tx-source-asset').value;
                createDto.installment = document.getElementById('tx-installment').value;
            }
            // 내 계좌 이체일 경우 목적지 자산 정보 수집
            if (payMethod === 'MY_ACCOUNT_TRANSFER') {
                createDto.destinationAssetId = document.getElementById('tx-destination-asset').value;
            }

        } else { // 수입(INCOME)일 경우, 출금 자산 ID가 필요. (어느 자산으로 수입이 되었는지)
            createDto.sourceAssetId = document.getElementById('tx-source-asset').value;
        }


        // 3. fetch API를 사용하여 백엔드로 데이터 전송
        try {
            const response = await fetch('/api/transactions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(createDto), // JavaScript 객체를 JSON 문자열로 변환
            });

            if (response.ok) {
                showAlert('거래내역이 성공적으로 저장되었습니다.');

                // 성공 시 내역 작성 모달은 닫고, 가계부 목록은 새로고침
                document.getElementById('transaction-modal').classList.add('TR-hidden');
                initializeWalletPage(currentYear, currentMonth);
            } else {
                const errorData = await response.json();
                showAlert(`저장에 실패했습니다: ${errorData.message || '알 수 없는 오류'}`);
            }

        } catch (error) {
            console.error('API 호출 중 오류 발생:', error);
            alert('저장 중 오류가 발생했습니다.');
        }

    });
}