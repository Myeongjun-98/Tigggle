// wallet.js
// 전역적으로 현재 연/월을 관리할 변수
let currentYear;
let currentMonth;

let currentTransactionIdForModal = null; // 상세보기 모달에 표시된 거래 ID
let currentEditingTransactionId = null;  // 현재 '수정 중인' 거래 ID

// 페이지의 모든 HTML 요소가 로드되면 이 스크립트를 실행합니다.
document.addEventListener('DOMContentLoaded', () => {

    // 1. 페이지가 로드될 때, 현재 날짜를 기준으로 초기화 함수를 호출합니다.
    const today = new Date();
    currentYear = today.getFullYear();
    currentMonth = today.getMonth() + 1; // getMonth()는 0부터 시작하므로 +1 해줍니다.

    initializeWalletPage(currentYear, currentMonth);

    // 2. '이전 달', '다음 달' 버튼에 클릭 이벤트 리스너를 추가합니다.

    document.getElementById('TR-previous-month-btn').addEventListener('click', () => {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        initializeWalletPage(currentYear, currentMonth);
    });

    document.getElementById('TR-next-month-btn').addEventListener('click', () => {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        initializeWalletPage(currentYear, currentMonth);
    });

    // 상세보기 모달의 '수정' 버튼
    const editBtn = document.getElementById('detail-edit-btn');
    if (editBtn) {
        editBtn.addEventListener('click', () => {
            // '수정' 버튼을 누르면, 현재 보고 있는 거래내역의 ID로
            // '수정 모드'로 내역 작성 모달을 엽니다.
            openCreateModalInEditMode(currentTransactionIdForModal);
        });
    }

    const detailCloseBtn = document.querySelector('.detail-close-button');
    if(detailCloseBtn) {
        detailCloseBtn.addEventListener('click', () => {
            document.getElementById('TR-detail-modal').classList.add('TR-hidden');
        });
    }

    // * 일괄삭제 리스너
    const deleteSelectedBtn = document.querySelector('.TR-delete-selected-btn');
    if (deleteSelectedBtn) {
        deleteSelectedBtn.addEventListener('click', () => {
            // 1. 현재 화면에 있는 모든 체크박스 중, 체크된 것만 찾습니다.
            const checkedItems = document.querySelectorAll('.TR-item-checkbox:checked');

            if (checkedItems.length === 0) {
                showAlert("삭제할 항목을 선택해주세요.");
                return;
            }

            // 2. 체크된 항목들의 value(transactionId)를 모아 배열로 만듭니다.
            const idsToDelete = Array.from(checkedItems).map(checkbox => checkbox.value);

            // 3. 사용자에게 최종 확인을 받습니다.
            if (confirm(`${idsToDelete.length}개의 항목을 정말 삭제하시겠습니까?`)) {
                // 4. 백엔드 API를 호출합니다.
                deleteSelectedTransactions(idsToDelete);
            }
        });
    }

    const schedulePopupBtn = document.getElementById('open-schedule-popup-btn');
    if (schedulePopupBtn) {
        schedulePopupBtn.addEventListener('click', (event) => {
            // a 태그의 기본 동작을 한 번 더 확실하게 막아줍니다.
            event.preventDefault();

            // 팝업을 여는 함수를 여기서 직접 호출합니다.
            openPopup('/transaction/scheduled-transaction', '정기 입/출금 관리', 900, 700);
        });
    }
    initializeCreateModal();
});

/*
 * 페이지의 모든 데이터를 불러오고 그리는 메인 함수
 * @param {number} year - 조회할 연도
 * @param {number} month - 조회할 월
 */
async function initializeWalletPage(year, month) {
    const container = document.getElementById('TR-history-container');
    container.innerHTML = '<div>가계부 내역을 가져오는 중...</div>';

    try {
        // 단 한 번의 API 호출로 페이지에 필요한 모든 정보를 가져옵니다.

        // 1. 현재 페이지의 경로(pathname)에서 assetId를 추출합니다.
        const pathParts = window.location.pathname.split('/');
        const assetId = pathParts[pathParts.length - 1]; // 예: /transaction/wallet/5 -> "5"

        // 2. 기본 API URL을 만듭니다.
        let apiUrl = `/api/wallet/page?year=${year}&month=${month}`;

        // 3. assetId가 숫자 형태이고, 'wallet'이 아닐 경우에만 파라미터로 추가합니다.
        if (!isNaN(assetId) && assetId.trim() !== 'wallet') {
            apiUrl += `&assetId=${assetId}`;
        }

        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error('데이터를 불러오는 데 실패했습니다.');
        }

        const pageData = await response.json();

        // API 응답의 status 값에 따라 화면을 다르게 처리합니다.
        if (pageData.status === 'NO_ASSET') {
            displayNoAssetMessage();
        } else {
            // 데이터가 성공적으로 오면, 각 부분을 업데이트하는 함수들을 호출합니다.
            updateAssetInfo(pageData.currentAsset);
            updateMonthlySummary(pageData.monthlyLedger);
            renderTransactionList(pageData.monthlyLedger.dailyLedgers);
        }
    } catch (error) {
        container.innerHTML = `<div>오류 발생: ${error.message}</div>`;
        console.error("페이지 초기화 중 오류:", error);
    }
}


// * 페이지 상단의 현재 자산 정보를 업데이트하는 함수
// * @param {object} assetData - AssetSummaryDto에 해당하는 데이터
function updateAssetInfo(assetData) {
    const container = document.getElementById('TR-ordinary-account-info');
    const balanceSpan = document.getElementById('TR-asset-balance');

    let assetHtml = '';
    // 자산 타입에 따라 다른 HTML을 생성
    if (assetData.bankName) { // 은행 이름이 있으면 은행 계좌로 판단
        assetHtml = `
            <span>${assetData.bankName}</span>
            <span>${assetData.accountNumber}</span>
        `;
    } else { // 은행 이름이 없으면 현금으로 판단
        assetHtml = `<span>${assetData.alias}</span>`;
    }

    container.innerHTML = assetHtml;
    if(assetData.balance)
    balanceSpan.innerText = assetData.balance.toLocaleString() + '원';
    else balanceSpan.innerText = " 0원 "
}

// * 월별 요약 정보(총수입, 총지출)를 업데이트하는 함수
// * @param {object} ledgerData - MonthlyLedgerDto에 해당하는 데이터
function updateMonthlySummary(ledgerData) {
    document.getElementById('TR-current-month-display').innerText = `📅 ${ledgerData.year}년 ${ledgerData.month}월`;
    document.getElementById('TR-monthly-income').innerText = (ledgerData.monthlyTotalIncome ? ledgerData.monthlyTotalIncome.toLocaleString() : 0) + '원';
    document.getElementById('TR-monthly-expense').innerText = (ledgerData.monthlyTotalExpense ? ledgerData.monthlyTotalExpense.toLocaleString() : 0) + '원';
}

// * 일별로 그룹핑된 거래 내역 리스트를 그리는 함수
// * @param {Array} dailyLedgers - DailyLedgerDto 배열
function renderTransactionList(dailyLedgers) {
    const container = document.getElementById('TR-history-container');
    container.innerHTML = ''; // 기존 내용을 모두 비웁니다.

    if (!dailyLedgers || dailyLedgers.length === 0) {
        container.innerHTML = '<div class="TR-no-data">이번 달 거래내역이 없습니다.</div>';
        return;
    }

    dailyLedgers.forEach(dailyLedger => {
        const transactionsHtml = dailyLedger.transactions.map(tx => `
            <li class="TR-item">
                <input type="checkbox" class="TR-item-checkbox" value="${tx.id}">
                <div onclick="openDetailModal(${tx.id})">
                    <span class="TR-each-day-description">${tx.description}</span>
                    <span class="TR-each-day-amount ${tx.isConsumption ? 'expense' : 'income'}">
                        ${tx.amount.toLocaleString()}원
                    </span>
                    <span class="TR-each-day-date">${formatTime(tx.transactionDate)}</span>
                </div>
            </li>
        `).join('');

        const dailyGroupHtml = `
            <div class="transaction-group">
                <h2>${dailyLedger.day}일 (${dailyLedger.dayOfWeek})</h2>
                <ul class="transaction-list">
                    ${transactionsHtml}
                </ul>
            </div>
        `;
        container.innerHTML += dailyGroupHtml;
    });
}

// * 거래내역 상세보기 모달을 열고 API를 통해 데이터를 채웁니다.
// * 이 함수는 renderTransactionList에서 생성된 <li>의 onclick에 의해 호출됩니다.
// * @param {number} transactionId - 상세 조회할 거래내역의 ID
async function openDetailModal(transactionId) {
    // 전역 변수에 현재 보고 있는 거래 ID를 저장하여 '수정' 버튼이 사용할 수 있도록 합니다.
    currentTransactionIdForModal = transactionId;

    const detailModal = document.getElementById('TR-detail-modal');
    document.getElementById('detail-description').innerText = '불러오는 중...';
    detailModal.classList.remove('TR-hidden');

    try {
        const response = await fetch(`/api/transactions/${transactionId}`);
        if (!response.ok) throw new Error('상세 내역을 불러오는데 실패했습니다.');

        const detailData = await response.json();

        const sign = detailData.consumption ? '-' : '+';
        const formattedAmount = detailData.amount.toLocaleString() + '원';
        // 받아온 데이터로 상세보기 모달의 각 영역을 채웁니다.
        document.getElementById('detail-amount').innerText = `${sign} ${formattedAmount}`;
        document.getElementById('detail-description').innerText = detailData.description;
        document.getElementById('detail-date').innerText = new Date(detailData.transactionDate).toLocaleString('ko-KR');
        document.getElementById('detail-keyword').innerText = detailData.keyword;
        document.getElementById('detail-payMethod').innerText = convertPaymethodKo(detailData.payMethod);
        document.getElementById('detail-note').innerText = detailData.note || '메모 없음';

    } catch (error) {
        showAlert(error.message);
        detailModal.classList.add('TR-hidden');
    }
}

// * 자산이 없을 때 메시지를 표시하는 함수
function displayNoAssetMessage() {
    document.getElementById('TR-history-container').innerHTML = `
        <div class="TR-no-data">
            <h2>등록된 지갑 자산이 없습니다.</h2>
            <a href="/asset/register">자산 등록하러 가기</a>
        </div>
    `;
    // 다른 요약 정보들도 비워주는 것이 좋습니다.
    document.getElementById('TR-current-month-display').innerText = '자산 없음';
    document.getElementById('TR-monthly-income').innerText = '0원';
    document.getElementById('TR-monthly-expense').innerText = '0원';
}

// * LocalDateTime 문자열에서 시간(HH:mm)만 추출하는 헬퍼 함수
// * @param {string} dateTimeString - 예: "2025-07-27T14:30:00"
function formatTime(dateTimeString) {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

function convertPaymethodKo(payMethod){
    switch(payMethod){
        case "MYACCOUNT":
            return "내 자산 간 이체"
        case "NORMAL":
            return "보통거래"
        case "CREDITCARD":
            return "신용카드"
        case "SCHEDULED":
            return "정기결제"
        default:
            return "기타"
    }
}

// 일괄 삭제 API를 호출하는 새로운 함수
async function deleteSelectedTransactions(ids) {

    try {
        const response = await fetch('/api/transactions', { // URL에서 ID 제거
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json', // Body가 JSON임을 명시
                [header]: token
            },
            body: JSON.stringify(ids) // ID 배열을 JSON 문자열로 변환하여 Body에 담아 전송
        });

        if (response.ok) {
            showAlert("선택한 항목이 성공적으로 삭제되었습니다.");
            initializeWalletPage(currentYear, currentMonth); // 목록 새로고침
        } else {
            const errorText = await response.text();
            showAlert(`삭제에 실패했습니다: ${errorText}`);
        }
    } catch (error) {
        console.error("일괄 삭제 중 오류:", error);
        showAlert("삭제 중 오류가 발생했습니다.");
    }
}


 // * '수정' 버튼 클릭 시, 특정 거래내역의 데이터로 '내역 작성 모달'을 채워서 열어줍니다.
 // * @param {number} transactionId - 수정할 거래내역의 ID
async function openCreateModalInEditMode(transactionId) {
    if (!transactionId) return;

    const expenseRadio = document.getElementById('TR-type-expense');
    const incomeRadio = document.getElementById('TR-type-income');
    const createModal = document.getElementById('transaction-modal');

    try {
        const response = await fetch(`/api/transactions/${transactionId}`);
        if (!response.ok) throw new Error('수정할 내역을 불러오는 데 실패했습니다.');
        const detailData = await response.json();

        document.getElementById('TR-detail-modal').classList.add('TR-hidden');
        createModal.classList.remove('TR-hidden');

        currentEditingTransactionId = transactionId; // '수정 모드'로 전환

        createModal.querySelector('h2').innerText = '거래내역 수정';
        createModal.querySelector('.transaction-submit-btn').innerText = '수정하기';

        // 가져온 데이터로 폼 필드를 채웁니다.
        document.getElementById('TR-tx-date').value = detailData.transactionDate.substring(0, 16);
        document.getElementById('TR-tx-amount').value = detailData.amount;
        document.getElementById('TR-tx-description').value = detailData.description;
        document.getElementById('TR-tx-note').value = detailData.note;
        document.getElementById('TR-tx-keyword').value = detailData.keywordId;
        // document.getElementById('TR-tx-pay-method').value = detailData.payMethod;

        if(detailData.consumption){
            expenseRadio.checked = true;
            expenseRadio.dispatchEvent(new Event('change'));
        }
        else{
            incomeRadio.checked = true;
            incomeRadio.dispatchEvent(new Event('change'));
        }

        document.querySelectorAll('input[name="transactionType"]').forEach(radio => {
            radio.disabled = true;
            
            // 비활성화 대신 그냥 숨겨버리기
            document.getElementById('expense-details').classList.add('TR-hidden');
            // document.getElementById('expense-details').disabled = true;
            document.getElementById('TR-my-account-transfer-details').classList.add('TR-hidden');

        document.getElementById('TR-tx-pay-method').disable = true;
        document.getElementById('TR-tx-source-asset').classList.add('TR-hidden');
        document.getElementById('TR-tx-income-asset').classList.add('TR-hidden');
        document.getElementById('TR-tx-destination-asset').classList.add('TR-hidden');
        });

    } catch (error) {
        showAlert('수정 정보를 불러오는 중 오류가 발생했습니다.');
        createModal.classList.add('TR-hidden')
        currentEditingTransactionId = null;
    }
}

/*
 * 지정된 URL을 정해진 크기의 팝업 창으로 엽니다.
 * @param {string} url - 팝업으로 열 페이지의 URL
 * @param {string} windowName - 팝업 창의 이름
 * @param {number} width - 팝업 창의 너비
 * @param {number} height - 팝업 창의 높이
 */
function openPopup(url, windowName, width, height) {
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);
    const options = `width=${width},height=${height},top=${top},left=${left},scrollbars=yes,resizable=yes`;

    window.open(url, windowName, options);
}

// * 사용자에게 메시지를 보여주는 커스텀 알림 모달 함수
// * @param {string} message - 표시할 메시지
function showAlert(message) {
    // 1. 필요한 HTML 요소들을 ID로 찾습니다.
    const modal = document.getElementById('TR-alert-modal');
    const messageElement = document.getElementById('TR-alert-message');
    const closeButton = document.getElementById('TR-alert-close-btn');

    // 2. 만약 필수 요소 중 하나라도 없다면, 기본 alert를 사용합니다.
    if (!modal || !messageElement || !closeButton) {
        console.error('Alert modal 또는 그 안의 요소를 찾을 수 없습니다.');
        alert(message); // 비상조치
        return;
    }

    // 3. 모달의 p 태그에 메시지를 설정합니다.
    messageElement.textContent = message;

    // 4. 모달을 화면에 표시합니다.
    modal.classList.remove('TR-hidden');

    // 5. '확인' 버튼을 누르면 모달이 닫히도록 이벤트 리스너를 설정합니다.
    // (이미 리스너가 있다면 중복 추가되지 않도록, 한번만 실행되는 { once: true } 옵션을 사용합니다.)
    closeButton.addEventListener('click', () => {
        modal.classList.add('TR-hidden');
    }, { once: true });
}