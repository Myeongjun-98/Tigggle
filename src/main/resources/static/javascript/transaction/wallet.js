// wallet.js
// 전역적으로 현재 연/월을 관리할 변수
let currentYear;
let currentMonth;

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
        const response = await fetch(`/api/wallet/page?year=${year}&month=${month}`);
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

/**
 * 페이지 상단의 현재 자산 정보를 업데이트하는 함수
 * @param {object} assetData - AssetSummaryDto에 해당하는 데이터
 */
function updateAssetInfo(assetData) {
    // TODO: HTML에 자산 별칭, 계좌번호 등을 표시할 요소의 ID를 지정하고 아래 코드를 완성해야 합니다.
    // document.getElementById('TR-asset-alias').innerText = assetData.alias;
    const container = document.getElementById('TR-asset-info-container');
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
    balanceSpan.innerText = assetData.balance.toLocaleString() + '원';
}

/**
 * 월별 요약 정보(총수입, 총지출)를 업데이트하는 함수
 * @param {object} ledgerData - MonthlyLedgerDto에 해당하는 데이터
 */
function updateMonthlySummary(ledgerData) {
    document.getElementById('TR-current-month-display').innerText = `📅 ${ledgerData.year}년 ${ledgerData.month}월`;
    document.getElementById('TR-monthly-income').innerText = ledgerData.monthlyTotalIncome.toLocaleString() + '원';
    document.getElementById('TR-monthly-expense').innerText = ledgerData.monthlyTotalExpense.toLocaleString() + '원';
}

/**
 * 일별로 그룹핑된 거래 내역 리스트를 그리는 함수
 * @param {Array} dailyLedgers - DailyLedgerDto 배열
 */
function renderTransactionList(dailyLedgers) {
    const container = document.getElementById('TR-history-container');
    container.innerHTML = ''; // 기존 내용을 모두 비웁니다.

    if (!dailyLedgers || dailyLedgers.length === 0) {
        container.innerHTML = '<div class="TR-no-data">이번 달 거래내역이 없습니다.</div>';
        return;
    }

    dailyLedgers.forEach(dailyLedger => {
        const transactionsHtml = dailyLedger.transactions.map(tx => `
            <li class="TR-item" onclick="openDetailModal(${tx.id})">
                <input type="checkbox">
                <span class="TR-each-day-description">${tx.description}</span>
                <span class="TR-each-day-amount ${tx.isConsumption ? 'expense' : 'income'}">
                    ${tx.amount.toLocaleString()}원
                </span>
                <span class="TR-each-day-date">${formatTime(tx.transactionDate)}</span>
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
// * @param {number} transactionId - 상세 조회할 거래내역의 ID
async function openDetailModal(transactionId) {
    const detailModal = document.getElementById('TR-detail-modal');
    // 로딩 중에 내용을 비워줍니다.
    document.getElementById('detail-description').innerText = '불러오는 중...';
    detailModal.classList.remove('TR-hidden');

    try {
        // 1. 단일 거래내역 조회 API를 호출합니다.
        const response = await fetch(`/api/transactions/${transactionId}`);
        if (!response.ok) throw new Error('상세 내역을 불러오는데 실패했습니다.');

        const detailData = await response.json();

        // 2. 받아온 데이터로 상세보기 모달의 각 영역을 채웁니다.
        document.getElementById('detail-type').innerText = detailData.isConsumption ? '-' : '+';
        document.getElementById('detail-amount').innerText = detailData.amount.toLocaleString() + '원';
        document.getElementById('detail-description').innerText = detailData.description;
        document.getElementById('detail-date').innerText = new Date(detailData.transactionDate).toLocaleString('ko-KR');
        document.getElementById('detail-keyword').innerText = detailData.keyword; // DTO 필드명에 맞게 수정 필요
        document.getElementById('detail-payMethod').innerText = convertPaymethodKo(detailData.payMethod); // DTO 필드명에 맞게 수정 필요
        document.getElementById('detail-note').innerText = detailData.note || '메모 없음';

    } catch (error) {
        // 실패 시 알림 모달을 재사용할 수 있습니다.
        showAlert(error.message);
        detailModal.classList.add('TR-hidden');
    }

    document.querySelector('.detail-close-button').addEventListener('click', () => {
        detailModal.classList.add('TR-hidden');
    });

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
    }
}