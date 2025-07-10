// modal.js

document.addEventListener('DOMContentLoaded', () => {
    initializeCreateModal();
    initializeAlertModal();
});

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

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

    // openModalBtn이 null이 아닐 때만 이벤트 리스너를 등록 (오류 방지)
    if (openModalBtn) {
        openModalBtn.addEventListener('click', () => {
            document.getElementById('transaction-modal').classList.remove('TR-hidden');
        });
    }

    // --- 이벤트 리스너 등록 ---

    // 2. '내역 입력' 버튼 클릭 시 모달창 보이기
    openModalBtn.addEventListener('click', () => {
        modalOverlay.classList.remove('TR-hidden');
    });

    // 3. '닫기(X)' 버튼 클릭 시 모달창 숨기기
    closeModalBtn.addEventListener('click', () => {
        modalOverlay.classList.add('TR-hidden');

        form.reset();

        // 동적으로 나타났던 섹션들도 모두 다시 숨깁니다.
        document.getElementById('expense-details').classList.add('TR-hidden');
        document.getElementById('TR-credit-card-details').classList.add('TR-hidden');
        document.getElementById('TR-my-account-transfer-details').classList.add('TR-hidden');

        // '구분' 라디오 버튼을 기본값('지출')으로 되돌립니다.
        document.getElementById('TR-type-expense').checked = true;

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
        event.preventDefault(); // 폼의 기본 제출 동작(새로고침)을 막습니다.

        // 1. DTO에 담을 데이터 객체 생성
        const createDto = {};

        // 2. 현재 선택된 값을 기준으로 DTO 객체를 채웁니다.
        const transactionType = form.querySelector('input[name="transactionType"]:checked').value;
        createDto.transactionType = transactionType;

        // 공통 필드 값 가져오기
        createDto.transactionDate = document.getElementById('TR-tx-date').value;
        createDto.amount = document.getElementById('TR-tx-amount').value;
        createDto.description = document.getElementById('TR-tx-description').value;
        createDto.keywordId = document.getElementById('TR-tx-keyword').value;
        createDto.note = document.getElementById('TR-tx-note').value;

        // 지출(EXPENSE)일 경우에만 추가 정보 수집
        if (transactionType === 'EXPENSE') {
            const payMethod = document.getElementById('TR-tx-pay-method').value;
            createDto.payMethod = payMethod;
            const sourceId = document.getElementById('TR-tx-source-asset').value;
            createDto.sourceAssetId = sourceId
            const destinationId = document.getElementById('TR-tx-destination-asset').value;
            // 신용카드일 경우 할부 정보 수집
            if (payMethod === 'CREDIT_CARD') {
                createDto.creditCardId = document.getElementById('TR-tx-source-asset').value;
                createDto.installment = document.getElementById('TR-tx-installment').value;
            }
            // 내 계좌 이체일 경우 목적지 자산 정보 수집
            if (payMethod === 'MY_ACCOUNT_TRANSFER') {
                createDto.destinationAssetId = destinationId;
                if(sourceId && destinationId && sourceId === destinationId){
                    showAlert("같은 계좌를 선택하셨습니다.")
                    return;
                }
            }

        } else { // 수입(INCOME)일 경우, 출금 자산 ID가 필요. (어느 자산으로 수입이 되었는지)
            createDto.sourceAssetId = document.getElementById('TR-tx-income-asset').value;
        }


        // 3. fetch API를 사용하여 백엔드로 데이터 전송
        try {
            const response = await fetch('/api/transactions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(createDto), // JavaScript 객체를 JSON 문자열로 변환
            });

            if (response.ok) {
                showAlert('거래내역이 성공적으로 저장되었습니다.');

                // 저장 후 적었던 값들 초기화
                document.getElementById('TR-create-form').reset();
                document.getElementById('TR-credit-card-details').classList.add('TR-hidden');
                document.getElementById('TR-my-account-transfer-details').classList.add('TR-hidden');

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