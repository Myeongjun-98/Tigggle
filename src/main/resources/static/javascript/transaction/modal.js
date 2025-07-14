// modal.js

document.addEventListener('DOMContentLoaded', () => {
    initializeCreateModal();
    initializeAlertModal();
});

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");

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
    const openModalBtn = document.querySelector('.TR-actions-save');
    const closeModalBtn = document.querySelector('.close-button');

    const transactionTypeRadios = document.querySelectorAll('input[name="transactionType"]');
    const payMethodSelect = document.getElementById('TR-tx-pay-method');
    const installmentInput = document.getElementById('TR-tx-installment');
    const destinationAssetSelect = document.getElementById('TR-tx-destination-asset');

    const incomeDetailSection = document.getElementById('income-details');
    const expenseDetailsSection = document.getElementById('expense-details');
    const creditCardDetailsSection = document.getElementById('TR-credit-card-details');
    const myAccountTransferDetailsSection = document.getElementById('TR-my-account-transfer-details');

    // 2. '내역 입력' 버튼 클릭 시 모달창 보이기
    openModalBtn.addEventListener('click', () => {
        modalOverlay.classList.remove('TR-hidden');
    });

    // 3. '닫기(X)' 버튼 클릭 시 모달창 숨기기
    closeModalBtn.addEventListener('click', () => {
        modalOverlay.classList.add('TR-hidden');

        // form.reset();
        //
        // // 동적으로 나타났던 섹션들도 모두 다시 숨깁니다.
        // document.getElementById('expense-details').classList.add('TR-hidden');
        // document.getElementById('TR-credit-card-details').classList.add('TR-hidden');
        // document.getElementById('TR-my-account-transfer-details').classList.add('TR-hidden');
        //
        // // '구분' 라디오 버튼을 기본값('지출')으로 되돌립니다.
        // document.getElementById('TR-type-expense').checked = true;

        resetCreateModalToDefault();

    });

    // 4. '수입/지출' 라디오 버튼 변경 시
    transactionTypeRadios.forEach(radio => {
        radio.addEventListener('change', async(event) => {
            if (event.target.value === 'EXPENSE') {
                expenseDetailsSection.classList.remove('TR-hidden');
                incomeDetailSection.classList.add('TR-hidden');

                // '지출' 선택 시, '거래 방식'의 change 이벤트를 강제로 발생시켜
                // 기존 로직을 재활용합니다. (이 부분은 유지하는 것이 효율적입니다)
                payMethodSelect.dispatchEvent(new Event('change'));

            } else { // INCOME 선택 시
                expenseDetailsSection.classList.add('TR-hidden');
                incomeDetailSection.classList.remove('TR-hidden');

                const incomeAssetSelect = document.getElementById('TR-tx-income-asset');
                incomeAssetSelect.innerHTML = '<option value="">불러오는 중...</option>'; // 로딩 표시

                try {
                    // 3. 컨트롤러에 정의된 API를 호출합니다.
                    // isConsumption=false 파라미터를 반드시 포함해야 합니다.
                    const response = await fetch('/api/transactions/when-income?isConsumption=false');
                    if (!response.ok) {
                        throw new Error('입금 계좌 목록을 불러오는 데 실패했습니다.');
                    }

                    const assets = await response.json();

                    // 4. 받아온 데이터로 드롭다운을 새로 채웁니다.
                    incomeAssetSelect.innerHTML = ''; // 로딩 메시지 제거

                    const defaultOption = document.createElement('option');
                    defaultOption.value = "";
                    defaultOption.textContent = "== 입금 계좌 선택 ==";
                    incomeAssetSelect.appendChild(defaultOption);

                    assets.forEach(asset => {
                        const option = document.createElement('option');
                        option.value = asset.id;
                        option.textContent = `[${asset.type}] ${asset.alias}`;
                        incomeAssetSelect.appendChild(option);
                    });

                } catch (error) {
                    console.error('수입 자산 목록 조회 중 오류:', error);
                    incomeAssetSelect.innerHTML = '<option value="">목록을 불러올 수 없습니다.</option>';
                }

                // 지출 관련 세부 섹션을 모두 숨기고, 그 안의 값도 초기화/비활성화합니다.
                creditCardDetailsSection.classList.add('TR-hidden');
                installmentInput.value = '0'; // 할부 개월 수 초기화

                myAccountTransferDetailsSection.classList.add('TR-hidden');
                destinationAssetSelect.value = ''; // 목적지 자산 선택 초기화
            }
        });
    });

    // 5. '거래 방식' 선택 변경 시
    payMethodSelect.addEventListener('change', async (event) => {
        const selectedMethod = event.target.value;

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

        // --- 출금 자산 목록을 동적으로 불러오는 로직 ---
        const sourceAssetSelect = document.getElementById('TR-tx-source-asset');
        sourceAssetSelect.innerHTML = '<option>불러오는 중...</option>'; // 로딩 중 표시

        // 신용카드는 출금 자산이 없으므로 API를 호출하지 않음
        if (selectedMethod === 'CREDIT_CARD') {
            sourceAssetSelect.innerHTML = '<option value="">카드를 선택하세요</option>'; // 예시
            try {
                const response = await fetch(`/api/transactions/by-paymethod?payMethod=${selectedMethod}`);
                const assets = await response.json();

                sourceAssetSelect.innerHTML = ''; // 기존 옵션을 모두 비웁니다.

                sourceAssetSelect.innerHTML = '<option value="">== 카드 선택 ==</option>>'

                assets.forEach(asset => {
                    const option = document.createElement('option');
                    option.value = asset.id;
                    option.textContent = `[${asset.type}] ${asset.alias}`;
                    sourceAssetSelect.appendChild(option);
                });

            } catch (error) {
                console.error('카드/계좌 목록을 불러오는 데 실패했습니다:', error);
                sourceAssetSelect.innerHTML = '<option value="">목록을 불러올 수 없습니다.</option>';
            }
            return; // 아래 로직을 실행하지 않고 종료
        }

        // 입금처 계좌를 불러옴!

        try {
            // 1. 백엔드 API를 호출하여 선택된 거래 방식에 맞는 자산 목록을 요청
            const response = await fetch(`/api/transactions/by-paymethod?payMethod=${selectedMethod}`);
            if (!response.ok) throw new Error('자산 목록 로딩 실패');

            const assets = await response.json();

            // 2. 받아온 데이터로 <option> 태그를 만들어 드롭다운을 새로 채움
            sourceAssetSelect.innerHTML = ''; // 로딩 중 메시지를 지웁니다.

            if (assets.length === 0) {
                sourceAssetSelect.innerHTML = '<option value="">선택 가능한 자산이 없습니다.</option>';
            } else {
                sourceAssetSelect.innerHTML = '<option value="">== 현금/계좌 선택 ==</option>>'
                destinationAssetSelect.innerHTML = '<option value="">== 현금/계좌 선택 ==</option>>'
                assets.forEach(asset => {
                    const option = document.createElement('option');
                    option.value = asset.id;
                    option.textContent = `[${asset.type}] ${asset.alias}`;
                    sourceAssetSelect.appendChild(option);
                });

                // MYACCOUNT일 시 입금처도 입력!
                if(selectedMethod === 'MY_ACCOUNT_TRANSFER'){
                    const destinationAssetSelect = document.getElementById('TR-tx-destination-asset')

                    assets.forEach(asset => {
                        const option = document.createElement('option');
                        option.value = asset.id;
                        option.textContent = `[${asset.type}] ${asset.alias}`;
                        destinationAssetSelect.appendChild(option);
                    })
                }
            }
        } catch (error) {
            console.error('카드/계좌 목록을 불러오는 데 실패했습니다:', error);
            sourceAssetSelect.innerHTML = '<option value="">목록을 불러올 수 없습니다.</option>';
        }
        });

    // * 폼 제출 이벤트 처리
    const form = document.getElementById('TR-create-form')

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        console.log('[SUBMIT] 저장 버튼 클릭됨. 현재 currentEditingTransactionId:', currentEditingTransactionId);

        const isEditMode = currentEditingTransactionId !== null;
        const dto = {}; // 서버로 보낼 DTO 객체

        // --- 1. DTO 객체 채우기 ---

        if (isEditMode) {
            // [수정 모드] TransactionUpdateDto에 맞는 필드만 채웁니다.
            dto.description = document.getElementById('TR-tx-description').value;
            dto.amount = document.getElementById('TR-tx-amount').value;
            dto.transactionDate = document.getElementById('TR-tx-date').value;
            dto.note = document.getElementById('TR-tx-note').value;
            dto.keywordId = document.getElementById('TR-tx-keyword').value;
            // isConsumption은 UpdateDto에 추가하기로 했습니다.
            dto.isConsumption = document.querySelector('input[name="transactionType"]:checked').value === 'EXPENSE';
        } else {
            // [생성 모드] TransactionCreateRequestDto에 맞는 모든 필드를 채웁니다.
            const transactionType = form.querySelector('input[name="transactionType"]:checked').value;
            dto.transactionType = transactionType;

            dto.transactionDate = document.getElementById('TR-tx-date').value;
            dto.amount = document.getElementById('TR-tx-amount').value;
            dto.description = document.getElementById('TR-tx-description').value;
            dto.keywordId = document.getElementById('TR-tx-keyword').value;
            dto.note = document.getElementById('TR-tx-note').value;

            if (transactionType === 'EXPENSE') {
                const payMethod = document.getElementById('TR-tx-pay-method').value;
                dto.payMethod = payMethod;

                const sourceId = document.getElementById('TR-tx-source-asset').value;
                if (payMethod === 'CREDIT_CARD') {
                    dto.creditCardId = sourceId;
                } else {
                    dto.sourceAssetId = sourceId;
                }

                if (payMethod === 'MY_ACCOUNT_TRANSFER') {
                    const destinationId = document.getElementById('TR-tx-destination-asset').value;
                    dto.destinationAssetId = destinationId;
                    if (sourceId && destinationId && sourceId === destinationId) {
                        showAlert("출금과 입금 계좌는 같을 수 없습니다.");
                        return;
                    }
                }
            } else { // INCOME
                dto.sourceAssetId = document.getElementById('TR-tx-income-asset').value;
            }
        }

        // --- 2. API 호출 및 결과 처리 (수정 없음) ---

        const url = isEditMode ? `/api/transactions/${currentEditingTransactionId}` : '/api/transactions';
        const method = isEditMode ? 'PATCH' : 'POST';

        // 디버깅을 위해 전송 직전의 DTO를 콘솔에 출력해봅니다.
        console.log(`[${method}] 요청 전송:`, url);
        console.log('전송할 DTO 데이터:', dto);

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(dto),
            });

            if (response.ok) {
                const successMessage = isEditMode ? '성공적으로 수정되었습니다.' : '성공적으로 저장되었습니다.';
                showAlert(successMessage);
                resetAndCloseModal();
            } else {
                const errorText = await response.text();
                showAlert(`저장에 실패했습니다: ${errorText}`);
            }
        } catch (error) {
            console.error('API 호출 중 오류 발생:', error);
            showAlert('저장 중 오류가 발생했습니다.');
        }
    });
}

// 폼을 닫고 모든 상태를 초기화하는 헬퍼 함수
function resetAndCloseModal() {
    currentEditingTransactionId = null; // 수정 모드 종료
    const form = document.getElementById('TR-create-form')
    if(form) form.reset();

    // 비활성화 했던 필드들을 다시 활성화
    document.querySelectorAll('#TR-create-form select, #TR-create-form input[type="radio"]')
        .forEach(el => el.disabled = false);

    document.getElementById('transaction-modal').classList.add('TR-hidden');

    initializeWalletPage(currentYear, currentMonth); // 목록 새로고침
}

function resetCreateModalToDefault() {

    console.log('[RESET] 리셋 함수 호출됨. currentEditingTransactionId를 null로 초기화합니다.');

    currentEditingTransactionId = null;

    // 1. 폼의 모든 입력 값을 HTML 기본값으로 리셋
    document.getElementById('TR-create-form').reset();

    // 2. '지출'이 기본 선택이 되도록 설정
    document.getElementById('TR-type-expense').checked = true;

    // 3. UI 상태를 '지출' 기본 상태로 강제 변경
    document.getElementById('expense-details').classList.remove('TR-hidden');
    document.getElementById('income-details').classList.add('TR-hidden');
    document.getElementById('TR-credit-card-details').classList.add('TR-hidden');
    document.getElementById('TR-my-account-transfer-details').classList.add('TR-hidden');

    // 4. '거래 방식' 드롭다운의 change 이벤트를 강제로 발생시켜,
    //    '보통거래'에 해당하는 자산 목록을 미리 불러오게 함
    document.getElementById('TR-tx-pay-method').dispatchEvent(new Event('change'));

    // 5. 수정 모드였다면, 비활성화했던 필드들을 다시 활성화
    document.querySelectorAll('#TR-create-form select, #TR-create-form input[type="radio"]')
        .forEach(el => el.disabled = false);

    // 6. 모달 제목과 버튼 텍스트를 '생성 모드'로 되돌림
    document.querySelector('#transaction-modal h2').innerText = '새로운 거래내역 작성';
    document.querySelector('#transaction-modal .submit-button').innerText = '저장하기';
}