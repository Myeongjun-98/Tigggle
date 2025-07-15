// modal.js

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
        if(currentEditingTransactionId === null){
        resetCreateModalToDefault();
        }

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
        const sourceAssetSelect = document.getElementById('TR-tx-source-asset');

        // 1. 상세 옵션 섹션들의 표시 여부를 먼저 결정합니다.
        creditCardDetailsSection.classList.toggle('TR-hidden', selectedMethod !== 'CREDIT_CARD');
        myAccountTransferDetailsSection.classList.toggle('TR-hidden', selectedMethod !== 'MY_ACCOUNT_TRANSFER');

        // 2. 다른 옵션으로 변경 시, 관련 필드 값을 초기화합니다.
        if (selectedMethod !== 'CREDIT_CARD') installmentInput.value = '0';
        if (selectedMethod !== 'MY_ACCOUNT_TRANSFER') destinationAssetSelect.value = '';

        // 3. API를 호출하여 '현금/카드/계좌' 드롭다운을 채웁니다.
        sourceAssetSelect.innerHTML = '<option>불러오는 중...</option>';
        destinationAssetSelect.innerHTML = ''; // 목적지 자산은 미리 비워둡니다.

        // `payMethod` 값이 있어야만 API를 호출합니다.
        if (!selectedMethod) {
            sourceAssetSelect.innerHTML = '<option>== 거래 방식을 선택하세요 ==</option>';
            return;
        }

        try {
            const response = await fetch(`/api/transactions/by-paymethod?payMethod=${selectedMethod}`);
            if (!response.ok) {
                throw new Error('자산 목록을 불러오는 데 실패했습니다.');
            }
            const assets = await response.json();

            // 4. 받아온 자산 목록으로 드롭다운을 채웁니다.
            sourceAssetSelect.innerHTML = '';
            destinationAssetSelect.innerHTML = ''; // 입금처 목록도 미리 비워줍니다.

            const defaultOption = document.createElement('option');
            defaultOption.value = "";
            defaultOption.textContent = "== 선택 ==";
            sourceAssetSelect.appendChild(defaultOption.cloneNode(true));
            destinationAssetSelect.appendChild(defaultOption.cloneNode(true));

            if (assets.length > 0) {
                assets.forEach(asset => {
                    const option = document.createElement('option');
                    option.value = asset.id;
                    option.textContent = `[${asset.type}] ${asset.alias}`;

                    // 출금 계좌 드롭다운에 옵션 추가
                    sourceAssetSelect.appendChild(option.cloneNode(true));

                    // '내 계좌 이체'일 경우에만 입금처 드롭다운에도 옵션을 추가합니다.
                    if (selectedMethod === 'MY_ACCOUNT_TRANSFER') {
                        destinationAssetSelect.appendChild(option);
                    }
                });
            }

        } catch (error) {
            console.error('자산 목록 조회 중 오류:', error);
            sourceAssetSelect.innerHTML = `<option>${error.message}</option>`;
        }
    });

    // * 폼 제출 이벤트 처리
    const form = document.getElementById('TR-create-form')

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const submitBtn = document.querySelector('.transaction-submit-btn');
        submitBtn.disabled = true;
        submitBtn.textContent = '저장 중...';

        const isEditMode = currentEditingTransactionId !== null;
        const dto = {}; // 서버로 보낼 DTO 객체

        // --- 1. DTO 객체 채우기 ---t

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
            console.error('폼 제출 중 오류 발생:', error);
            showAlert('저장 중 오류가 발생했습니다.');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = isEditMode ? '수정' : '저장';
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
    document.querySelector('#transaction-modal .transaction-submit-btn').innerText = '저장하기';
}